package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.apicontract.client.scala.model.domain.security.Settings
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.AMLEnumCompletionPlugin

import scala.concurrent.Future

object RamlEnumCompletionPlugin extends AMLCompletionPlugin {
  override def id: String = "AMLEnumCompletionPlugin"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    if (request.amfObject.isInstanceOf[Settings] && request.amfObject.fields.fields().isEmpty) emptySuggestion
    else AMLEnumCompletionPlugin.resolve(request)
  }
}
