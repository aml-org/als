package org.mulesoft.als.suggestions.plugins.aml.webapi.raml.raml10.structure

import amf.plugins.domain.webapi.models.security.ParametrizedSecurityScheme
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Raml10NullCompletionPlugin extends AMLCompletionPlugin {
  override def id: String = "Raml10NullCompletionPlugin"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      request.amfObject match {
        case _: ParametrizedSecurityScheme =>
          Seq(RawSuggestion.plain("null", "null"))
        case _ => Nil
      }
    }
  }
}
