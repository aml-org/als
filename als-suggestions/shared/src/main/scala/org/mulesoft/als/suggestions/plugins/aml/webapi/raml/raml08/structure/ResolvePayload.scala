package org.mulesoft.als.suggestions.plugins.aml.webapi.raml.raml08.structure

import amf.plugins.domain.webapi.models.Payload
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.ResolveIfApplies
import org.mulesoft.als.suggestions.plugins.aml.webapi.raml.raml08.Raml08TypeFacetsCompletionPlugin

import scala.concurrent.Future

object ResolvePayload extends ResolveIfApplies {
  override def resolve(request: AmlCompletionRequest): Option[Future[Seq[RawSuggestion]]] = {
    request.amfObject match {
      case _: Payload =>
        applies(Future.successful(Seq()))
      case _ => notApply
    }
  }
}
