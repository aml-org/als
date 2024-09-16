package org.mulesoft.als.suggestions.plugins.aml.webapi.async

import amf.apicontract.client.scala.model.domain.{Payload, Server}
import amf.core.client.scala.model.domain.Shape
import amf.shapes.client.scala.model.domain.AnyShape
import org.mulesoft.als.common.ASTPartBranch
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.{AmlCompletionRequest, AmlCompletionRequestBuilder}
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.WebApiTypeFacetsCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.avroschema.{AvroTypesCompletionPlugin, FieldTypeKnowledge}
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.AsyncApi20Dialect
import org.mulesoft.amfintegration.dialect.dialects.avro.AvroDialect

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Async20PayloadCompletionPlugin
    extends AMLCompletionPlugin
    with AsyncMediaTypePluginFinder
    with FieldTypeKnowledge {
  override def id: String = "Async20PayloadCompletionPlugin"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    val branchStack = request.branchStack
    branchStack.headOption match { // check replacing this with findFirst
      case Some(p: Payload)
          if request.astPartBranch.isKeyDescendantOf("payload") &&
            !branchStack.exists(_.isInstanceOf[Server]) =>
        request.amfObject match {
          case s: Shape => resolveFindingPluginByMediaType(p, request)
        }
      case Some(p: Payload) if isInsidePayload(request.astPartBranch) => resolveFindingPluginByMediaType(p, request)
      case _ => Future(Seq.empty)
    }
  }

  private def resolveFindingPluginByMediaType(payload: Payload, request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    val branchStack = request.branchStack
    findPluginForMediaType(payload)
      .map {
        case wf: WebApiTypeFacetsCompletionPlugin =>
          Future(wf.resolveShape(request.amfObject.asInstanceOf[AnyShape], branchStack, AsyncApi20Dialect()))
        case generic =>
          generic.resolve(request)
      }
      .getOrElse(
        Future(
          Async20TypeFacetsCompletionPlugin
            .resolveShape(request.amfObject.asInstanceOf[AnyShape], branchStack, AsyncApi20Dialect())
        )
      )
  }

  private def isInsidePayload(astPartBranch: ASTPartBranch): Boolean =
    astPartBranch.isInBranchOf("payload")

}
