package org.mulesoft.als.suggestions.plugins.raml

import amf.core.remote.{Oas, Raml10, Vendor}
import org.mulesoft.als.suggestions.implementation.{CompletionResponse, Suggestion}
import org.mulesoft.als.suggestions.interfaces._
import org.mulesoft.high.level.interfaces.IHighLevelNode
import org.mulesoft.typesystem.nominal_interfaces.IProperty
import org.mulesoft.typesystem.nominal_interfaces.extras.PropertySyntaxExtra
import org.mulesoft.high.level.{Declaration, Search}
import org.mulesoft.positioning.IPositionsMapper
import org.mulesoft.typesystem.syaml.to.json.{YPoint, YRange}
import org.yaml.model._

import scala.collection.mutable
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

    val result = request.astNode.flatMap(_.parent) match {
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
        var usedTemplateNames =
          owner.elements(propName).flatMap(_.asElement).flatMap(_.attribute("name").flatMap(_.value).map(_.toString))

        var actualPrefix = request.prefix

        var squareBracketsRequired = false

        var refYamlKind: Option[RefYamlKind] = None
        var readableName                     = ""
        var declarations = propName match {
          case "type" =>
            refYamlKind = templateRefYamlKind(request, owner, "type")
            readableName = "Resource type"
            Search.getDeclarations(n.astUnit, "ResourceType");

          case "is" =>
            squareBracketsRequired = true
            refYamlKind = templateRefYamlKind(request, owner, "is")
            readableName = "Trait"
            Search.getDeclarations(n.astUnit, "Trait");

          case _ => Seq();
        }
        declarations = declarations.filter(x => {
          var nameOpt = x.node
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
              val typeName = declaration.node.definition.nameId.get
              var paramNames =
                declaration.node.attributes("parameters").map(param => param.value.asInstanceOf[Some[String]].get)

              if (actualPrefix.indexOf("{") >= 0) {
                actualPrefix = actualPrefix.substring(actualPrefix.indexOf("{") + 1).trim()
              }

              paramNames.map(name => Suggestion(name, readableName + " parameter", name, actualPrefix))
            }
            .getOrElse(Nil)
        } else {
          declarations.map(declaration => {
            var ts = toTemplateSuggestion(declaration, refYamlKind.get)
            Suggestion(ts.get.text, readableName, ts.get.name, request.prefix)
          })
        }

      case _ => Seq();
    }

    var response = CompletionResponse(result, LocationKind.VALUE_COMPLETION, request)
    Promise.successful(response).future
  }

  def inParamOf(request: ICompletionRequest): Option[String] = {
    var text: String = request.config.editorStateProvider.get.getText

    var currentPosition = request.position

    var lineStart = text.substring(0, currentPosition).lastIndexOf("\n") + 1

    if (lineStart < 0) {
      return None
    }

    var line = text.substring(lineStart, currentPosition)

    var openSquaresCount = line.count(_ == "{".charAt(0))

    if (openSquaresCount < 2) {
      return None
    }

    if (line.last == "{".charAt(0)) {
      line = line + " "
    }

    var rightExps = line.split("\\{")

    var canContainReference = rightExps(rightExps.length - 2)

    var referenceParts = canContainReference.split(":")

    if (referenceParts.length != 2) None
    else Some(referenceParts(0))
  }

  def templateRefYamlKind(request: ICompletionRequest, owner: IHighLevelNode, propName: String): Option[RefYamlKind] = {
    request.astNode.flatMap(node => {
      var pos = request.position
      var pm  = node.astUnit.positionsMapper
      Option(owner).flatMap(pNode => {
        var si = pNode.sourceInfo
        si.yamlSources.headOption
          .filter(_.isInstanceOf[YMapEntry])
          .map(_.asInstanceOf[YMapEntry].value.value)
          .filter(_.isInstanceOf[YMap])
          .flatMap(_.asInstanceOf[YMap].entries.find(e => e.key.value.toString == propName))
          .flatMap(propNode => {
            var line    = YRange(propNode, Option(pm)).start.line
            var lineStr = pm.lineString(line)
            var offset  = lineStr.map(pm.lineOffset).getOrElse(-1)
            propNode.value.value match {
              case sc: YScalar => Some(RefYamlKind.scalar(offset))
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
                Some(RefYamlKind.sequence(fl, wrappedInMap, wrappedFlow, off))
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
                Some(RefYamlKind.map(fl, wrappedInMap, wrappedFlow, off))
              case _ => None
            }
          })
      })
    })
  }

  def toTemplateSuggestion(decl: Declaration, kind: RefYamlKind): Option[TemplateSuggestion] = {
    val declNode = decl.node
    var nameOpt  = declNode.attribute("name").flatMap(_.value).map(_.toString)
    if (nameOpt.isEmpty) {
      None
    } else {
      var isTrait        = declNode.definition.nameId.contains("Trait")
      var isResourceType = declNode.definition.nameId.contains("ResourceType")
      val off            = kind.valueOffset

      val plainName = nameOpt.get
      val nsOpt     = decl.namespace
      val name      = if (nsOpt.isDefined) s"${nsOpt.get}.$plainName" else plainName
      val params    = declNode.attributes("parameters").flatMap(_.value).map(_.toString)
      if (isTrait) {
        if (params.isEmpty) {
          if (kind.inSequence) {
            Some(TemplateSuggestion(name, name, kind))
          } else if (kind.inMap && !kind.flow) {
            Some(TemplateSuggestion(name, s"- $name", kind))
          } else {
            Some(TemplateSuggestion(name, s"[ $name ]", kind))
          }
        } else {
          if (kind.inMap && !kind.flow) {
            if (kind.wrappedFlow) {
              //var valOffStr = " " * (off + 2)
              //var paramOffStr = valOffStr + "  "
              var text = toFlowObject(name, params)
              Some(TemplateSuggestion(name, text, kind))
            } else {
              var paramOffStr = " " * (off + 6)
              var text        = s"- " + toBlockObject(name, params, paramOffStr)
              Some(TemplateSuggestion(name, text, kind))
            }
          } else if (kind.inMap && kind.flow) {
            //var valOffStr = " " * (off + 2)
            //var paramOffStr = valOffStr + "  "
            var text = toFlowObject(name, params)
            Some(TemplateSuggestion(name, text, kind))
          } else if (kind.inSequence && !kind.flow) {
            if (kind.wrappedFlow) {
              //var valOffStr = " " * (off + 2)
              //var paramOffStr = valOffStr + "  "
              var text = toFlowObject(name, params)
              Some(TemplateSuggestion(name, text, kind))
            } else {
              var paramOffStr = " " * (off + 6)
              var text        = toBlockObject(name, params, paramOffStr)
              Some(TemplateSuggestion(name, text, kind))
            }
          } else if (kind.inSequence && kind.flow) {
            //var valOffStr = " " * (off + 2)
            //var paramOffStr = valOffStr + "  "
            var text = toFlowObject(name, params)
            Some(TemplateSuggestion(name, text, kind))
          } else {
            var valOffStr   = " " * (off + 2)
            var paramOffStr = " " * (off + 6)
            var text        = s"\n$valOffStr- " + toBlockObject(name, params, paramOffStr)
            Some(TemplateSuggestion(name, text, kind))
          }
        }
      } else if (isResourceType) {
        if (params.isEmpty) {
          if (kind.inMap && !kind.flow) {
            Some(TemplateSuggestion(name, s"$name:", kind))
          } else {
            Some(TemplateSuggestion(name, name, kind))
          }
        } else {
          if (kind.inMap && !kind.flow) {
            if (kind.wrappedFlow) {
              //var valOffStr = " " * (off + 2)
              //var paramOffStr = valOffStr + "  "
              var text = toFlowObject(name, params)
              Some(TemplateSuggestion(name, text, kind))
            } else {
              var paramOffStr = " " * (off + 4)
              var text        = toBlockObject(name, params, paramOffStr)
              Some(TemplateSuggestion(name, text, kind))
            }
          } else if (kind.inMap && kind.flow) {
            //var valOffStr = " " * (off + 2)
            //var paramOffStr = valOffStr + "  "
            var text = toFlowObject(name, params)
            Some(TemplateSuggestion(name, text, kind))
          } else {
            var valOffStr   = " " * (off + 2)
            var paramOffStr = " " * (off + 4)
            var text        = s"\n$valOffStr" + toBlockObject(name, params, paramOffStr)
            Some(TemplateSuggestion(name, text, kind))
          }
        }
      } else {
        None
      }
    }
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
    if (str.isEmpty) {
      false
    } else {
      val ch0 = str.charAt(0)
      val ch1 = str.charAt(str.length - 1)
      (ch0 == '[' && ch1 == ']') || (ch0 == '{' && ch1 == '}')
    }
  }
}

object TemplateReferencesCompletionPlugin {
  val ID = "templateRef.completion";

  val supportedLanguages: List[Vendor] = List(Raml10);

  def apply(): TemplateReferencesCompletionPlugin = new TemplateReferencesCompletionPlugin();
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
