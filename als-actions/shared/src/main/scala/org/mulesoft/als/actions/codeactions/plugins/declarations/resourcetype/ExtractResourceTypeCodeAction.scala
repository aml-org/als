package org.mulesoft.als.actions.codeactions.plugins.declarations.resourcetype

import amf.client.render.WebApiDomainElementEmitter
import amf.core.emitter.SpecOrdering
import amf.core.errorhandling.UnhandledErrorHandler
import amf.core.metamodel.domain.templates.AbstractDeclarationModel
import amf.core.model.domain.{AmfObject, DataNode, ObjectNode}
import amf.core.parser.Annotations
import amf.core.remote.Vendor
import amf.plugins.document.webapi.contexts.emitter.raml.Raml10SpecEmitterContext
import amf.plugins.document.webapi.parser.spec.domain.{Raml10EndPointEmitter, RamlEndPointEmitter}
import amf.plugins.domain.webapi.annotations.ParentEndPoint
import amf.plugins.domain.webapi.models.EndPoint
import amf.plugins.domain.webapi.models.templates.ResourceType
import org.mulesoft.als.actions.codeactions.plugins.CodeActionKindTitle
import org.mulesoft.als.actions.codeactions.plugins.base.{
  CodeActionFactory,
  CodeActionRequestParams,
  CodeActionResponsePlugin
}
import org.mulesoft.als.actions.codeactions.plugins.declarations.common.{ConverterExtractor, ExtractorCommon}
import org.mulesoft.als.actions.codeactions.plugins.declarations.samefile.ExtractSameFileDeclaration
import org.mulesoft.amfintegration.AmfImplicits.AmfObjectImp
import org.mulesoft.amfintegration.AmfImplicits.BaseUnitImp
import org.mulesoft.lsp.feature.telemetry.TelemetryProvider
import amf.plugins.domain.webapi.metamodel.templates.ResourceTypeModel
import amf.plugins.domain.webapi.models.api.WebApi
import org.mulesoft.als.actions.codeactions.plugins.declarations.common.ExtractorCommon.renderNode
import org.yaml.model.{YDocument, YMapEntry}
import org.yaml.model.YDocument.{EntryBuilder, PartBuilder}
import shapeless.ops.union.Fields

import scala.collection.convert.ImplicitConversions.`iterator asScala`
import scala.collection.mutable.ListBuffer

case class ExtractResourceTypeCodeAction(params: CodeActionRequestParams)
    extends ConverterExtractor[EndPoint, EndPoint] {
  override protected val kindTitle: CodeActionKindTitle = ExtractResourceTypeCodeAction

  override lazy val isApplicable: Boolean =
    params.bu.sourceVendor.contains(Vendor.RAML10) && original.isDefined

  override protected def newName: String = ExtractorCommon.nameNotInList("resourceType", params.bu.declaredNames.toSet)

  override val fallbackDeclarationKey: Option[String] = Some("resourceTypes")

  override protected def telemetry: TelemetryProvider = params.telemetryProvider

  override protected def msg(params: CodeActionRequestParams): String =
    s"Extract resource type: \n\t${params.uri}\t${params.range}"

  override protected def uri(params: CodeActionRequestParams): String =
    params.uri

  override def transform(original: EndPoint): EndPoint = {
    val result = EndPoint(original.fields, Annotations())
    result
  }

  override lazy val original: Option[EndPoint] = amfObject match {
    case Some(e: EndPoint) => Some(e)
    case _                 => None
  }

  lazy val webApi: Option[WebApi] = maybeTree.flatMap(_.stack.collectFirst {
    case w: WebApi => w
  })

  def getEndpointChildren(endpoint: EndPoint): Seq[EndPoint] =
    webApi.map(_.endPoints.filter(_.parent.exists(_.id == endpoint.id))).getOrElse(Seq())

  implicit val raml10SpecEmitterContext: Raml10SpecEmitterContext = new Raml10SpecEmitterContext(UnhandledErrorHandler)

  def emitEndpoint(endPoint: EndPoint): RamlEndPointEmitter = {
    val children = getEndpointChildren(endPoint)
    children.map(child => EndPoint(child.fields, Annotations() += ParentEndPoint(endPoint)))
    Raml10EndPointEmitter(endPoint, SpecOrdering.Lexical, children.map(emitEndpoint).to[ListBuffer])
  }

  override def modifyEntry(original: EndPoint): String = {
    val result: EndPoint = EndPoint() // Create a copy?

    // Strip parent prefix
    val fullPath = original.path.value()
    val path     = original.parent.map(p => fullPath.replace(p.path.value(), "")).getOrElse(fullPath)

    result.withPath(path)
    result.withResourceType(newName)
    result.withId(original.id)
    val node = YDocument.objFromBuilder(build => emitEndpoint(result).emit(build)).node
    renderNode(node,
               yPartBranch.flatMap(_.parentEntry),
               params.bu,
               params.configuration,
               jsonOptions,
               yamlOptions,
               renderingValue = false)._1 + "\n"
  }
}

object ExtractResourceTypeCodeAction extends CodeActionFactory with ExtractResourceTypeKind {
  def apply(params: CodeActionRequestParams): CodeActionResponsePlugin = new ExtractResourceTypeCodeAction(params)
}
