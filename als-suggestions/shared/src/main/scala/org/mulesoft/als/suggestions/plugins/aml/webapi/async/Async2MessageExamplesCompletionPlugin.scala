package org.mulesoft.als.suggestions.plugins.aml.webapi.async

import amf.plugins.domain.webapi.models.{Message, Payload, Response}
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.NodeMappingWrapper

import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.AsyncMessageExampleNode
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.DialectNode

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Async2MessageExamplesCompletionPlugin extends AMLCompletionPlugin with Async2PayloadExampleMatcher {
  override def id: String               = "MessageExamplesCompletionPlugin"
  private val examplesNode: DialectNode = AsyncMessageExampleNode

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    if (isExampleAtPayload(params)) Future.successful(examplesNode.Obj.propertiesRaw(d = params.actualDialect))
    else emptySuggestion
  }
}

trait Async2PayloadExampleMatcher {
  private def applies(params: AmlCompletionRequest): Boolean = {
    params.yPartBranch.isDescendanceOf("examples")
  }

  protected def isExampleAtPayload(params: AmlCompletionRequest): Boolean = {
    params.amfObject
      .isInstanceOf[Payload] && params.branchStack.headOption.exists(_.isInstanceOf[Response]) && applies(params)
  }
}
