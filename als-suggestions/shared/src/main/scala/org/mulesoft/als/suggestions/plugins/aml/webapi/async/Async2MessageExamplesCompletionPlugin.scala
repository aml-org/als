package org.mulesoft.als.suggestions.plugins.aml.webapi.async

import amf.apicontract.client.scala.model.domain.{Payload, Response}
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.NodeMappingWrapper
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.{Async20MessageExampleNode, AsyncMessageExampleNode}
import org.mulesoft.amfintegration.dialect.dialects.asyncapi26.Async21MessageExampleNode
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.DialectNode

import scala.concurrent.Future

object Async2MessageExamplesCompletionPlugin extends AMLCompletionPlugin with Async2PayloadExampleMatcher {
  override def id: String = "MessageExamplesCompletionPlugin"
  private def examplesNode(nameAndVersion: String): DialectNode = {
    nameAndVersion match {
      case "asyncapi 2.6.0" => Async21MessageExampleNode
      case _                => Async20MessageExampleNode
    }
  }

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    if (isExampleAtPayload(params))
      Future.successful(
        examplesNode(params.nodeDialect.nameAndVersion()).Obj.propertiesRaw(fromDialect = params.actualDialect)
      )
    else emptySuggestion
  }
}

trait Async2PayloadExampleMatcher {
  private def applies(params: AmlCompletionRequest): Boolean = {
    params.astPartBranch.parentEntryIs("examples")
  }

  protected def isExampleAtPayload(params: AmlCompletionRequest): Boolean = {
    params.amfObject
      .isInstanceOf[Payload] && params.branchStack.headOption.exists(_.isInstanceOf[Response]) && applies(params)
  }
}
