package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.core.model.domain.Shape
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.AMLDeclarationsReferencesCompletionPlugin

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object RAMLDeclarationsReferencesCompletionPlugin extends AMLCompletionPlugin {

  override def id: String = "AMLDeclarationsReferencesCompletionPlugin"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    request.amfObject match {
      case _: Shape => Future { Nil }
      case _        => AMLDeclarationsReferencesCompletionPlugin.resolve(request)
    }
  }
}
