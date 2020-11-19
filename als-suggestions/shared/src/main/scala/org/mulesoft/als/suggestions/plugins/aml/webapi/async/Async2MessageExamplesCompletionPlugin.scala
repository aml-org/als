package org.mulesoft.als.suggestions.plugins.aml.webapi.async

import amf.plugins.domain.webapi.models.Message
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.NodeMappingWrapper
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.AsyncMessageExampleNode
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.DialectNode

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Async2MessageExamplesCompletionPlugin extends AMLCompletionPlugin {
  override def id: String               = "MessageExamplesCompletionPlugin"
  private val examplesNode: DialectNode = AsyncMessageExampleNode

  private def applies(params: AmlCompletionRequest): Boolean = {
    params.yPartBranch.isDescendanceOf("examples")
  }

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      params.amfObject match {
        case _: Message if applies(params) => examplesNode.Obj.propertiesRaw(d = params.actualDialect)
        case _                             => Seq()
      }
    }
  }
}
