package org.mulesoft.als.suggestions.plugins.aml.webapi.async

import amf.plugins.domain.webapi.metamodel.PayloadModel
import amf.plugins.domain.webapi.models.Payload
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.plugins.aml.AbstractKnownValueCompletionPlugin

import scala.concurrent.Future

object AsyncMessageContentType extends AbstractKnownValueCompletionPlugin {

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    if (request.amfObject.isInstanceOf[Payload] && request.fieldEntry.exists(_.field == PayloadModel.MediaType))
      innerResolver(request, PayloadModel.MediaType, PayloadModel.`type`.head.iri())
    else super.resolve(request)
  }
}
