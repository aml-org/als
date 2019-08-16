package org.mulesoft.als.suggestions.plugins.oas;

import amf.core.remote.{Oas, Oas20, Vendor}
import org.mulesoft.als.suggestions.implementation.{CompletionResponse, Suggestion}
import org.mulesoft.als.suggestions.interfaces._
import org.mulesoft.high.level.interfaces.IHighLevelNode
import org.mulesoft.high.level.{Declaration, Search}
import org.mulesoft.positioning.PositionsMapper
import org.mulesoft.typesystem.nominal_interfaces.IProperty
import org.mulesoft.typesystem.nominal_interfaces.extras.PropertySyntaxExtra
import org.yaml.model.{DoubleQuoteMark, YScalar}

import scala.collection.mutable
import scala.concurrent.{Future, Promise}

abstract class ReferencePlugin extends ICompletionPlugin {

  override def languages: Seq[Vendor] = ReferencePlugin.supportedLanguages

  def alwaysSequence: Boolean

  def restrictedClasses: Seq[String]

  override def isApplicable(request: ICompletionRequest): Boolean = request.config.astProvider match {
    case Some(astProvider) =>
      if (languages.indexOf(astProvider.language) < 0) {
        false
      } else if (request.astNode
                   .flatMap(_.asAttr)
                   .flatMap(_.property)
                   .flatMap(_.getExtra(PropertySyntaxExtra))
                   .exists(_.isKey)) {
        false
      } else {
        request.actualYamlLocation.flatMap(_.mapEntry) match {
          case Some(entry) if entry.yPart.key.asScalar.exists(_.text == "$ref") =>
            request.actualYamlLocation match {
              case Some(l) => l.inKey(request.position) || request.yamlLocation.get.hasSameValue(l);
              case _       => false;
            }
          case _ => false
        }
      }

    case _ => false
  }

  override def suggest(request: ICompletionRequest): Future[ICompletionResponse] = {
    var isAttr = false
    val isJSON = request.config.astProvider.map(_.syntax).contains(Syntax.JSON)
    val prefix =
      if (request.prefix.startsWith("#"))
        request.prefix.substring(1)
      else
        request.prefix
    val elementOpt: Option[IHighLevelNode] = request.astNode match {
      case Some(n) =>
        if (n.isElement)
          n.asElement
        else if (n.isAttr) {
          if (n.property.flatMap(_.nameId).contains("$ref"))
            isAttr = true
          n.parent
        } else
          None
      case _ => None
    }
    val result = elementOpt match {
      case Some(element) =>
        if (element.definition.isAssignableFrom("ParameterObject") &&
            !request.actualYamlLocation.get.inKey(request.position) &&
            request.astNode.get.isAttr &&
            !request.astNode.get.property.flatMap(_.nameId).contains("$ref"))
          Seq()
        else if (hasTargetClass(element)) {
          val ds = Search.getDeclarations(element.astUnit, definitionClass)

          ds.map(declaration => {
            val uri   = oasDeclarationReference(declaration)
            var label = uri
            val text = if (isAttr) {
              if (prefix.startsWith("/") && uri.startsWith("#")) {
                label = uri.substring(1)
                label
              } else {
                val needQuotes = request.actualYamlLocation.flatMap(_.value).map(_.yPart) match {
                  case Some(l) =>
                    l match {
                      case sc: YScalar =>
                        sc.mark != DoubleQuoteMark
                      case _ => false
                    }
                  case _ => false
                }
                if (needQuotes)
                  "\"" + uri + "\""
                else
                  uri
              }
            } else {
              val result = wrapDeclarationReference(uri, request)
              if (isJSON)
                label = result
              result
            }
            Suggestion(text, s"Reference to ${uri}", label, prefix)
          })
        } else
          Seq()
      case _ => Seq()
    }

    val response = CompletionResponse(result, LocationKind.VALUE_COMPLETION, request)
    Promise.successful(response).future
  }

  def definitionClass: String

  def wrapDeclarationReference(reference: String, request: ICompletionRequest): String = {
    val element = request.astNode.flatMap(n => {
      if (n.isElement)
        n.asElement
      else if (n.isAttr)
        n.parent
      else
        None
    })
    val isJSON = request.config.astProvider.map(_.syntax).contains(Syntax.JSON)
    if (isJSON) {
      val isScalar      = request.yamlLocation.flatMap(_.value).map(_.yPart).exists(_.isInstanceOf[YScalar])
      val pm            = PositionsMapper("/tmp").withText(request.config.originalContent.get)
      val point         = pm.point(request.position)
      val line          = pm.line(point.line).get
      val colonIndex    = Math.max(0, line.lastIndexOf(":", point.column))
      val hasStartQuote = line.lastIndexOf("\"", point.column) >= colonIndex
      val hasEndQuote   = line.indexOf("\"", point.column) >= 0
      var result        = "$ref\": \"" + reference
      if (!hasStartQuote)
        result = "\"" + result
      if (!hasEndQuote)
        result = result + "\""
      if (isScalar)
        result = s"{ $result }"
      result
    } else {
      val keyLine     = element.flatMap(_.sourceInfo.ranges.headOption).map(_.start.line).getOrElse(-1)
      val line        = request.astNode.get.astUnit.positionsMapper.point(request.position).line
      var indentation = ""
      if (!alwaysSequence && line == keyLine) {
        indentation = "\n"
        for { _ <- 0 until request.indentCount + 1 } indentation += request.currentIndent
      }
      indentation + "\"$ref\": \"" + reference + "\""
    }
  }

  def oasDeclarationReference(declaration: Declaration): String = {
    val declarationName = declaration.node.attribute("key").get.value.get

    val propertyName = declaration.node.property.get.nameId.get

    "#/" + propertyName + "/" + declarationName
  }

  def responseDeclarationName(declaration: Declaration): String =
    declaration.node.attribute("key").get.value.get.asInstanceOf[String]

  def hasTargetClass(node: IHighLevelNode): Boolean = {
    !((node.definition.universe.name.get != "OAS") ||
      (restrictedClasses.exists(node.definition.isAssignableFrom)) ||
      (!node.definition.isAssignableFrom(targetClass)))
  }

  def targetClass: String
}

object ReferencePlugin {
  val supportedLanguages: List[Vendor] = List(Oas, Oas20)
}
