package org.mulesoft.als.suggestions.plugins.aml.webapi.async

import amf.apicontract.client.scala.model.domain.{EndPoint, Server}
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.ResolveIfApplies
import org.mulesoft.als.suggestions.plugins.aml.webapi.ExceptionPlugin

import scala.concurrent.Future

object Async2ServerExceptionPlugin extends ExceptionPlugin with AsyncEndpointServerIdentifier {
  override def id: String = "ServerExceptionPlugin"

  override def applies(request: AmlCompletionRequest): Boolean =
    isServerFromEndpoint(request)

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = emptySuggestion
}

object ServerResolver extends ResolveIfApplies with AsyncEndpointServerIdentifier {
  override def resolve(request: AmlCompletionRequest): Option[Future[Seq[RawSuggestion]]] =
    if (isServerFromEndpoint(request)) Some(Future.successful(Seq.empty)) else None
}

trait AsyncEndpointServerIdentifier {
  def isServerFromEndpoint(request: AmlCompletionRequest): Boolean =
    request.amfObject.isInstanceOf[Server] &&
      request.branchStack.headOption.exists(_.isInstanceOf[EndPoint])
}
