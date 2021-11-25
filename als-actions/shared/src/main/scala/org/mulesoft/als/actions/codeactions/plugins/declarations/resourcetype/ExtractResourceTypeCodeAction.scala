package org.mulesoft.als.actions.codeactions.plugins.declarations.resourcetype

import amf.aml.client.scala.model.document.Dialect
import amf.aml.client.scala.model.domain.SemanticExtension
import amf.apicontract.client.scala.model.domain.EndPoint
import amf.apicontract.client.scala.model.domain.api.WebApi
import amf.apicontract.internal.annotations.ParentEndPoint
import amf.apicontract.internal.spec.common.emitter.{Raml10EndPointEmitter, RamlEndPointEmitter}
import amf.apicontract.internal.spec.raml.emitter.context.Raml10SpecEmitterContext
import amf.core.client.scala.errorhandling.UnhandledErrorHandler
import amf.core.internal.parser.domain.Annotations
import amf.core.internal.remote.Spec
import amf.core.internal.render.SpecOrdering
import org.mulesoft.als.actions.codeactions.plugins.CodeActionKindTitle
import org.mulesoft.als.actions.codeactions.plugins.base.{
  CodeActionFactory,
  CodeActionRequestParams,
  CodeActionResponsePlugin
}
import org.mulesoft.als.actions.codeactions.plugins.declarations.common.{ConverterExtractor, ExtractorCommon}
import org.mulesoft.amfintegration.AmfImplicits.BaseUnitImp
import org.mulesoft.lsp.feature.telemetry.TelemetryProvider
import org.yaml.model.{YDocument, YMap, YNodePlain}

import scala.collection.mutable.ListBuffer

case class ExtractResourceTypeCodeAction(params: CodeActionRequestParams)
    extends ConverterExtractor[EndPoint, EndPoint] {
  override protected val kindTitle: CodeActionKindTitle = ExtractResourceTypeCodeAction

  override val declarationKey: String = "resourceTypes"

  override lazy val isApplicable: Boolean =
    params.bu.sourceSpec.contains(Spec.RAML10) && original.isDefined

  override protected def newName: String = ExtractorCommon.nameNotInList("resourceType", params.bu.declaredNames.toSet)

  override protected def telemetry: TelemetryProvider = params.telemetryProvider

  override protected def msg(params: CodeActionRequestParams): String =
    s"Extract resource type: \n\t${params.uri}\t${params.range}"

  override protected def uri(params: CodeActionRequestParams): String =
    params.uri

  override def transform(original: EndPoint): EndPoint =
    EndPoint(original.fields, Annotations())

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
    val node = YDocument.objFromBuilder(build => emitEndpoint(result).emit(build)).node match {
      case ynode: YNodePlain =>
        ynode.value match {
          case map: YMap => map.map.head._2
        }
      case e => e
    }

    s"\n${renderNode(node, yPartBranch.flatMap(_.parentEntry))}\n"
  }

  override protected val findDialectForSemantic: String => Option[(SemanticExtension, Dialect)] =
    params.findDialectForSemantic

}

object ExtractResourceTypeCodeAction extends CodeActionFactory with ExtractResourceTypeKind {
  def apply(params: CodeActionRequestParams): CodeActionResponsePlugin = new ExtractResourceTypeCodeAction(params)
}
