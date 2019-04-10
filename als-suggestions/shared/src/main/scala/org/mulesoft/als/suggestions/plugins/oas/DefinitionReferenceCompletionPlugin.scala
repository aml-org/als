package org.mulesoft.als.suggestions.plugins.oas

import amf.core.model.domain.AmfScalar
import amf.core.remote.{Oas, Vendor}
import org.mulesoft.als.suggestions.implementation.{CompletionResponse, Suggestion}
import org.mulesoft.als.suggestions.interfaces._
import org.mulesoft.high.level.builder.UniverseProvider
import org.mulesoft.high.level.interfaces.{IASTUnit, IHighLevelNode, IParseResult}
import org.mulesoft.typesystem.nominal_interfaces.IProperty
import org.mulesoft.als.suggestions.plugins.oas.DefinitionReferenceCompletionPlugin._
import org.mulesoft.high.level.implementation.SourceInfo
import org.mulesoft.positioning.{IPositionsMapper, PositionsMapper, YamlLocation, YamlPartWithRange}
import org.yaml.model.{DoubleQuoteMark, YScalar}

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.{Future, Promise}

class DefinitionReferenceCompletionPlugin extends ICompletionPlugin {

  override def id: String = DefinitionReferenceCompletionPlugin.ID

  override def languages: Seq[Vendor] = DefinitionReferenceCompletionPlugin.supportedLanguages

  override def suggest(request: ICompletionRequest): Future[ICompletionResponse] = {
    val isJSON = request.config.astProvider.map(_.syntax).contains(Syntax.JSON)
    val prefix =
      if (request.prefix.startsWith("#"))
        request.prefix.substring(1)
      else request.prefix
    val result = determineRefCompletionCase(request) match {
      case Some(spv: SCHEMA_PROPERTY_VALUE) =>
        extractRefs(request)
          .map(x => {
            if (isJSON) {
              val isScalar      = request.yamlLocation.flatMap(_.value).map(_.yPart).exists(_.isInstanceOf[YScalar])
              val pm            = PositionsMapper("/tmp").withText(request.config.originalContent.get)
              val point         = pm.point(request.position)
              val line          = pm.line(point.line).get
              val colonIndex    = Math.max(0, line.lastIndexOf(":", point.column))
              val hasStartQuote = line.lastIndexOf("\"", point.column) >= colonIndex
              val hasEndQuote   = line.indexOf("\"", point.column) >= 0
              var result        = "$ref\": \"" + x
              if (!hasStartQuote)
                result = "\"" + result
              if (!hasEndQuote)
                result = result + "\""
              if (isScalar)
                result = s"{ $result }"
              List(result, result, x)
            } else {
              val keyLine = request.yamlLocation
                .flatMap(_.keyNode)
                .map(_.range.start.line)
                .getOrElse(-1)
              val line    = request.astNode.get.astUnit.positionsMapper.point(request.position).line
              var text    = "\"$ref\": \"" + x + "\""
              val display = "$ref: " + x
              if (line == keyLine)
                text = "\n" + " " * spv.refPropOffset + text
              List(text, display, x)
            }
          })
          .map(x => Suggestion(x.head, s"Reference to ${x(2)}", x(1), prefix))

      case Some(_: REF_PROPERTY_VALUE) =>
        extractRefs(request).map(_uri => {
          var uri = _uri
          val text = if (prefix.startsWith("/") && uri.startsWith("#")) {
            uri = uri.substring(1)
            uri
          } else {
            if (isJSON) {
              val pm            = PositionsMapper("/tmp").withText(request.config.originalContent.get)
              val point         = pm.point(request.position)
              val line          = pm.line(point.line).get
              val colonIndex    = Math.max(0, line.lastIndexOf(":", point.column))
              val hasStartQuote = line.lastIndexOf("\"", point.column) >= colonIndex
              val hasEndQuote   = line.indexOf("\"", point.column) >= 0
              var result        = ""
              if (!hasStartQuote)
                result += "\""
              result += uri
              if (!hasEndQuote)
                result += "\""
              result
            } else {
              val needQuotes =
                request.actualYamlLocation.flatMap(_.value).map(_.yPart) match {
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
          }
          Suggestion(text, s"Reference to ${uri}", uri, prefix)
        })

      case _ => Seq()
    }

    val response = CompletionResponse(result, LocationKind.VALUE_COMPLETION, request)
    Promise.successful(response).future
  }

  def extractRefs(request: ICompletionRequest): Seq[String] = {
    val elementOpt = request.astNode.flatMap(
      x =>
        if (x.isElement)
          x.asElement
        else if (x.isAttr)
          x.parent
        else
        None)
    val propNameOpt             = elementOpt.flatMap(_.property).flatMap(_.nameId)
    var nameOpt: Option[String] = None
    if (propNameOpt.contains("definitions"))
      nameOpt = elementOpt.flatMap(_.attribute("name")).flatMap(_.value).map(_.toString)
    request.astNode.get.astUnit.rootNode
      .elements("definitions")
      .map(x => x.attribute("name").get.value.get)
      .filter(y => !nameOpt.contains(y))
      .map(n => s"#/definitions/$n")
  }

  override def isApplicable(request: ICompletionRequest): Boolean =
    determineRefCompletionCase(request).nonEmpty

  private def determineRefCompletionCase(request: ICompletionRequest): Option[RefCompletionCase] =
    checkPropertyDetectedCase(request) match {
      case Some(x) => Some(x)
      case None =>
        checkPropertyNotDetectedCase(request) match {
          case Some(x) => Some(x)
          case None    => None
        }
    }

  private def checkPropertyNotDetectedCase(request: ICompletionRequest): Option[RefCompletionCase] = {
    val isJSON = request.config.astProvider.map(_.syntax).contains(Syntax.JSON)
    if (request.astNode.isEmpty || !request.astNode.get.isElement)
      None
    else if (request.actualYamlLocation.isEmpty
             || request.actualYamlLocation.get.isEmpty
             || request.actualYamlLocation.get.keyValue.isEmpty)
      None
    else if (isJSON) {
      val node: IHighLevelNode = request.astNode.get.asElement.get
      if (node.parent.isEmpty)
        None
      else {
        if (node.property.flatMap(_.nameId).contains("definitions")
            && node.parent.flatMap(_.definition.nameId).contains("SwaggerObject"))
          Some(SCHEMA_PROPERTY_VALUE(0))
        else {
          val definition     = node.parent.get.definition
          val position: Int  = request.position
          val actualLocation = request.yamlLocation.get
          val pm             = node.astUnit.positionsMapper

          acceptedProperties.get
            .filter(
              _.domain
                .exists(x => definition.isAssignableFrom(x.nameId.get)))
            .flatMap(p =>
              actualLocation.keyValue.flatMap(keyValue =>
                keyValue.yPart match {
                  case scalar: YScalar =>
                    val key = scalar.value
                    if (p.nameId.contains(key))
                      selectPreciseCompletionStyle(request, position, actualLocation, pm, node.astUnit, isJSON)
                    else if (key == "$ref")
                      Some(REF_PROPERTY_VALUE())
                    else
                      None
                  case _ => None
              }))
            .headOption
        }
      }
    } else {
      val node: IHighLevelNode = request.astNode.get.asElement.get
      val definition           = node.definition
      val position: Int        = request.position
      val actualLocation       = request.actualYamlLocation.get
      val pm                   = node.astUnit.positionsMapper

      acceptedProperties.get
        .filter(
          _.domain
            .exists(x => definition.isAssignableFrom(x.nameId.get)))
        .flatMap(p => {
          val keyValue = actualLocation.keyValue.get
          keyValue.yPart match {
            case scalar: YScalar =>
              val key = scalar.value
              if (p.nameId.contains(key))
                selectPreciseCompletionStyle(request, position, actualLocation, pm, node.astUnit, isJSON)
              else if (key == "$ref")
                Some(REF_PROPERTY_VALUE())
              else
                None
            case _ => None
          }
        })
        .headOption
    }
  }

  private def selectPreciseCompletionStyle(request: ICompletionRequest,
                                           position: Int,
                                           actualLocation: YamlLocation,
                                           pm: IPositionsMapper,
                                           astUnit: IASTUnit,
                                           isJSON: Boolean): Option[RefCompletionCase] = {
    val keyValue = actualLocation.keyValue.get
    val keyStart = keyValue.range.start
    val posPoint = pm.point(position)
    if (keyStart.line == posPoint.line) {
      val si = SourceInfo().withSources(List(actualLocation.mapEntry.get.yPart))
      si.init(astUnit.project, Some(astUnit))
      val valOffset = si.valueOffset.get
      Some(SCHEMA_PROPERTY_VALUE(valOffset))
    } else if (isJSON) {
      val isScalar = request.yamlLocation.flatMap(_.value).map(_.yPart).exists(_.isInstanceOf[YScalar])
      if (isScalar)
        Some(REF_PROPERTY_VALUE())
      else
        Some(SCHEMA_PROPERTY_VALUE(request.position))
    } else
      None
  }

  private def checkPropertyDetectedCase(request: ICompletionRequest): Option[RefCompletionCase] = {
    val isJSON = request.config.astProvider.map(_.syntax).contains(Syntax.JSON)
    if (request.astNode.isEmpty)
      None
    else if (request.actualYamlLocation.isEmpty
             || request.actualYamlLocation.get.isEmpty
             || request.actualYamlLocation.get.keyNode.isEmpty)
      None
    else if (request.yamlLocation.get.value.get.yPart != request.actualYamlLocation.get.value.get.yPart)
      None
    else {
      val node: IParseResult = request.astNode.get
      val position: Int      = request.position
      val actualLocation     = request.actualYamlLocation.get
      val pm                 = node.astUnit.positionsMapper

      node.property match {
        case Some(nodeProp) =>
          acceptedProperties.get.find(x => x.nameId.isDefined && x.nameId == nodeProp.nameId) match {
            case Some(p) =>
              if (actualLocation.inKey(position))
                None
              else if (actualLocation.keyValue.isDefined)
                selectPreciseCompletionStyle(request, position, actualLocation, pm, node.astUnit, isJSON)
              else
                None
            case None =>
              if (nodeProp.nameId.contains("$ref"))
                if (node.parent.map(_.definition).exists(_.isAssignableFrom("SchemaObject")))
                  Some(REF_PROPERTY_VALUE())
                else
                  None
              else
                None
          }
        case None => None
      }
    }
  }

}

object DefinitionReferenceCompletionPlugin {

  private val YAML_OFFSET = 2

  val ID = "definition.reference.completion.plugin"

  val supportedLanguages = List(Oas)

  private var acceptedProperties: Option[Seq[IProperty]] = None

  def apply(): DefinitionReferenceCompletionPlugin = {
    acceptedProperties match {
      case Some(_) =>
      case None    => init()
    }
    new DefinitionReferenceCompletionPlugin()
  }

  private def init(): Unit = {

    UniverseProvider.universe(Oas) match {
      case Some(u) =>
        var props: ArrayBuffer[IProperty] = ArrayBuffer()
        acceptedProperties = Some(props)
        u.`type`("SchemaObject")
          .foreach(t => {
            t.property("properties").foreach(p => props += p)
            t.property("items").foreach(p => props += p)
            t.property("additionalProperties").foreach(p => props += p)
            t.property("allOf").foreach(p => props += p)
          })

        u.`type`("SwaggerObject").flatMap(_.property("definitions")).foreach(p => props += p)
        u.`type`("BodyParameterObject").flatMap(_.property("schema")).foreach(p => props += p)
        u.`type`("ResponseObject").flatMap(_.property("schema")).foreach(p => props += p)

      case None => acceptedProperties = Some(Seq())
    }
  }
}

private sealed class RefCompletionCase {}

private sealed class REF_PROPERTY_VALUE() extends RefCompletionCase {}

private object REF_PROPERTY_VALUE {
  def apply(): REF_PROPERTY_VALUE = new REF_PROPERTY_VALUE()
}

private sealed class SCHEMA_PROPERTY_VALUE(val refPropOffset: Int) extends RefCompletionCase {}

private object SCHEMA_PROPERTY_VALUE {
  def apply(refPropOffset: Int): SCHEMA_PROPERTY_VALUE = new SCHEMA_PROPERTY_VALUE(refPropOffset)
}
