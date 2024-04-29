package org.mulesoft.als.suggestions.plugins.aml.webapi.oas.structure

import amf.shapes.client.scala.model.domain.Example
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.ResolveIfApplies

import scala.concurrent.Future

object ResponseExampleException extends ResolveIfApplies {
  override def resolve(request: AmlCompletionRequest): Option[Future[Seq[RawSuggestion]]] = {
    if (request.amfObject.isInstanceOf[Example]) applies(Future.successful(Seq()))
    else notApply
  }
}
