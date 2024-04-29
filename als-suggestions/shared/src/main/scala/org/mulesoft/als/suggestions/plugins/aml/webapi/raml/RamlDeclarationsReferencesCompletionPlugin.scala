package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.core.client.scala.model.domain.Shape
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.AMLRamlStyleDeclarationsReferences

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object RamlDeclarationsReferencesCompletionPlugin extends AMLCompletionPlugin {

  override def id: String = "AMLRamlStyleDeclarationsReferences"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    request.amfObject match {
      case _: Shape => Future { Nil }
      case _        => AMLRamlStyleDeclarationsReferences.resolve(request)
    }
  }
}
