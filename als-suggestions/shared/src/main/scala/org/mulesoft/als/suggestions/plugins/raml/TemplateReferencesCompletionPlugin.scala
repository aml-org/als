package org.mulesoft.als.suggestions.plugins.raml

import amf.core.remote.{Raml10, Vendor}
import org.mulesoft.als.suggestions.implementation.{CompletionResponse, Suggestion}
import org.mulesoft.als.suggestions.interfaces._
import org.mulesoft.high.level.interfaces.IHighLevelNode
import org.mulesoft.high.level.{Declaration, Search}
import org.mulesoft.positioning.IPositionsMapper
import org.mulesoft.typesystem.syaml.to.json.YRange
import org.yaml.model._

import scala.concurrent.{Future, Promise}

class TemplateReferencesCompletionPlugin extends ICompletionPlugin {

  override def id: String = TemplateReferencesCompletionPlugin.ID

  override def languages: Seq[Vendor] = TemplateReferencesCompletionPlugin.supportedLanguages

  override def isApplicable(request: ICompletionRequest): Boolean = request.config.astProvider match {

    case Some(astProvider) =>
      languages.indexOf(astProvider.language) >= 0
    case _ => false
  }

  override def suggest(request: ICompletionRequest): Future[ICompletionResponse] = {
    val paramFor = inParamOf(request)

    val result: Seq[Suggestion] = request.astNode.flatMap(_.parent) match {
      case Some(n) =>
        var owner            = n.asElement.get
        var propName: String = n.property.flatMap(_.nameId).getOrElse("")
        if (owner.definition.isAssignableFrom("TraitRef") || owner.definition.isAssignableFrom("ResourceTypeRef")) {
          owner = n.parent.get
        } else {
          request.actualYamlLocation match {
            case Some(l) =>
              l.keyValue.map(_.yPart.toString) match {
                case Some(str) => propName = str
                case _         =>
              }
            case _ =>
          }
        }
        val usedTemplateNames =
          owner.elements(propName).flatMap(_.asElement).flatMap(_.attribute("name").flatMap(_.value).map(_.toString))

        var actualPrefix = request.prefix

        var squareBracketsRequired = false

        var refYamlKind: Option[RefYamlKind] = None
        var readableName                     = ""
        var declarations = propName match {
          case "type" =>
            refYamlKind = templateRefYamlKind(request, owner, "type")
            readableName = "Resource type"
            Search.getDeclarations(n.astUnit, "ResourceType")

          case "is" =>
            squareBracketsRequired = true
            refYamlKind = templateRefYamlKind(request, owner, "is")
            readableName = "Trait"
            Search.getDeclarations(n.astUnit, "Trait")

          case _ => Seq()
        }
        declarations = declarations.filter(x => {
          val nameOpt = x.node
            .attribute("name")
            .flatMap(_.value)
            .map(name => {
              if (x.namespace.isDefined) {
                s"${x.namespace}.$name"
              } else {
                name.toString
              }
            })
          nameOpt.isDefined && !usedTemplateNames.contains(nameOpt.get)
        })

        if (paramFor.isDefined) {
          declarations
            .find(declaration =>
              paramFor.contains(declaration.node.attribute("name").get.value.asInstanceOf[Some[String]].get))
            .map { declaration =>
              val paramNames =
                declaration.node.attributes("parameters").map(param => param.value.asInstanceOf[Some[String]].get)

              if (actualPrefix.indexOf("{") >= 0) {
                actualPrefix = actualPrefix.substring(actualPrefix.indexOf("{") + 1).trim()
              }

              paramNames.map(name => {
                Suggestion(name, readableName + " parameter", name, actualPrefix)
              })
            }
            .getOrElse(Nil)
        } else {
          declarations.map(declaration => {
            val ts = toTemplateSuggestion(declaration, refYamlKind.get, request.prefix)
            Suggestion(ts.get.text, readableName, ts.get.name, request.prefix)
          })
        }

      case _ => Seq()
    }

    val response = CompletionResponse(result, LocationKind.VALUE_COMPLETION, request)
    Promise.successful(response).future
  }

  def inParamOf(request: ICompletionRequest): Option[String] = {
    val text: String = request.config.editorStateProvider.get.getText

    val currentPosition = request.position

    val lineStart = text.substring(0, currentPosition).lastIndexOf("\n") + 1

    if (lineStart < 0)
      return None

    var line = text.substring(lineStart, currentPosition)

    val openSquaresCount = line.count(_ == "{")

    if (openSquaresCount < 2)
      return None

    if (line.last == "{")
      line = line + " "

    val rightExps = line.split("\\{")

    val canContainReference = rightExps(rightExps.length - 2)

    val referenceParts = canContainReference.split(":")

    if (referenceParts.length != 2) None
    else Some(referenceParts(0))
  }

  def templateRefYamlKind(request: ICompletionRequest, owner: IHighLevelNode, propName: String): Option[RefYamlKind] = {
    request.astNode.flatMap(node => {

      var openBracket = false

      def isEntry(entry: YMapEntry, propName: String): Boolean = {
        if (entry.key.value.toString == propName) true
        else
          entry.value.value.asInstanceOf[YMap].entries.head match {
            case h if h.key.value.toString == propName =>
              openBracket = true
              true
            case _ => false
          }
      }
      val pm = node.astUnit.positionsMapper
      Option(owner).flatMap(pNode => {
        val si = pNode.sourceInfo
        si.yamlSources.headOption
          .filter(_.isInstanceOf[YMapEntry])
          .map(_.asInstanceOf[YMapEntry].value.value)
          .filter(_.isInstanceOf[YMap])
          .flatMap(_.asInstanceOf[YMap].entries.find(e => isEntry(e, propName)))
          .flatMap(propNode => {
            val line    = YRange(propNode, Option(pm)).start.line
            val lineStr = pm.lineString(line)
            val offset  = lineStr.map(pm.lineOffset).getOrElse(-1)
            propNode.value.value match {
              case _: YScalar => Some(RefYamlKind.scalar(offset))
              case seq: YSequence =>
                val fl           = isFlow(seq, pm)
                val off          = if (fl) -1 else offset
                var wrappedInMap = false
                var wrappedFlow  = false
                seq.nodes
                  .find(YRange(_, Some(pm)).containsPosition(request.position))
                  .foreach(x => {
                    wrappedInMap = x.value.isInstanceOf[YMap]
                    if (wrappedInMap) {
                      wrappedFlow = isFlow(x.value, pm)
                    }
                  })
                Some(RefYamlKind.sequence(fl || openBracket, wrappedInMap, wrappedFlow, off))
              case map: YMap =>
                val fl           = isFlow(map, pm)
                val off          = if (fl) -1 else offset
                var wrappedInMap = false
                var wrappedFlow  = false
                map.entries
                  .find(YRange(_, Some(pm)).containsPosition(request.position))
                  .foreach(x => {
                    wrappedInMap = x.value.value.isInstanceOf[YMap]
                    if (wrappedInMap) {
                      wrappedFlow = isFlow(x.value.value, pm)
                    }
                  })
                Some(RefYamlKind.map(fl || openBracket, wrappedInMap, wrappedFlow, off))
              case _ => None
            }
          })
      })
    })
  }

  def toTemplateSuggestion(decl: Declaration, kind: RefYamlKind, prefix: String): Option[TemplateSuggestion] = {
    val declNode = decl.node
    val nameOpt  = declNode.attribute("name").flatMap(_.value).map(_.toString)

    def withOffset(off: Int, c: Int): String = " " * (off + c)

    def traitTemplate(off: Int, name: String, params: Seq[String]) = {
      val paramOffStr = withOffset(off, 6)
      val valOffStr   = withOffset(off, 2)

      params match {
        case Nil if kind.inMap && !kind.flow => TemplateSuggestion(name, s"- $name", kind)
        case Nil                             => TemplateSuggestion(name, name, kind)
        case _ =>
          kind.inMap match {
            case true if !kind.flow && !kind.wrappedFlow && !kind.inSequence =>
              TemplateSuggestion(name, s"- " + toBlockObject(name, params, paramOffStr), kind)
            case false if kind.inSequence && !kind.flow && !kind.wrappedFlow =>
              TemplateSuggestion(name, toBlockObject(name, params, paramOffStr), kind)
            case _ if kind.inSequence || kind.flow || kind.wrappedFlow =>
              TemplateSuggestion(name, toFlowObject(name, params), kind)
            case _ =>
              TemplateSuggestion(name, s"\n$valOffStr- " + toBlockObject(name, params, paramOffStr), kind)
          }
      }
    }

    def resourceTypeTemplate(off: Int, name: String, params: Seq[String]) = {
      val paramOffStr = withOffset(off, 4)
      val valOffStr   = withOffset(off, 2)

      params match {
        case Nil if kind.inMap && !kind.flow => TemplateSuggestion(name, s"$name:", kind)
        case Nil                             => TemplateSuggestion(name, name, kind)
        case _ =>
          kind.inMap match {
            case true if !kind.flow && kind.wrappedFlow =>
              TemplateSuggestion(name, toFlowObject(name, params), kind)
            case true if !kind.flow =>
              TemplateSuggestion(name, toBlockObject(name, params, paramOffStr), kind)
            case true =>
              TemplateSuggestion(name, toFlowObject(name, params), kind)
            case _ =>
              TemplateSuggestion(name, s"\n$valOffStr" + toBlockObject(name, params, paramOffStr), kind)
          }
      }
    }

    nameOpt.flatMap(plainName => {
      val off    = kind.valueOffset
      val name   = decl.namespace.map(nsOpt => s"${nsOpt}.$plainName").getOrElse(plainName)
      val params = declNode.attributes("parameters").flatMap(_.value).map(_.toString)

      declNode.definition.nameId match {
        case Some("Trait")        => Option(traitTemplate(off, name, params))
        case Some("ResourceType") => Option(resourceTypeTemplate(off, name, params))
        case _                    => None
      }
    })
  }

  private def toBlockObject(name: String, params: Seq[String], paramOffStr: String) = {
    s"$name:\n" + params.map(x => s"$paramOffStr$x:").mkString("\n")
  }

  private def toFlowObject(name: String, params: Seq[String]) = {
    s"{ $name: { " + params.map(x => s" $x : ").mkString(", ") + "} }"
  }

  def isFlow(yPart: YPart, pm: IPositionsMapper): Boolean = {

    val r = YRange(yPart, Option(pm))
    pm.initRange(r)
    val str = pm.getText.substring(r.start.position, r.end.position).trim

    !str.isEmpty && {
      val ch0 = str.charAt(0)
      val ch1 = str.charAt(str.length - 1)
      (ch0 == '[' && ch1 == ']') || (ch0 == '{' && ch1 == '}')
    }
  }
}

object TemplateReferencesCompletionPlugin {
  val ID = "templateRef.completion"

  val supportedLanguages: List[Vendor] = List(Raml10)

  def apply(): TemplateReferencesCompletionPlugin = new TemplateReferencesCompletionPlugin()
}

case class RefYamlKind(inSequence: Boolean,
                       inMap: Boolean,
                       flow: Boolean,
                       wrappedInMap: Boolean,
                       wrappedFlow: Boolean,
                       valueOffset: Int)

object RefYamlKind {
  def scalar(valOffset: Int = -1): RefYamlKind = RefYamlKind(false, false, false, false, false, valOffset)
  def map(flow: Boolean, wrappedInMap: Boolean, wrappedFlow: Boolean, valOffset: Int = -1): RefYamlKind =
    RefYamlKind(false, true, flow, wrappedInMap, wrappedFlow, valOffset)
  def sequence(flow: Boolean, wrappedInMap: Boolean, wrappedFlow: Boolean, valOffset: Int = -1): RefYamlKind =
    RefYamlKind(true, false, flow, wrappedInMap, wrappedFlow, valOffset)
}

case class TemplateSuggestion(name: String, text: String, kind: RefYamlKind)
