package org.mulesoft.als.suggestions.plugins.aml.webapi.oas.oas30

import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.oas.OASRefTag

import scala.concurrent.Future

object OAS30RefTag extends AMLCompletionPlugin {
  override def id: String = "AMLRefTagCompletionPlugin"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    if (Oas30ExceptionPlugins.applyAny(request)) emptySuggestion
    else OASRefTag.resolve(request)
  }
}
