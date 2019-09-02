package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.plugins.domain.webapi.models.security.{OAuth1Settings, OAuth2Settings, ParametrizedSecurityScheme}
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml._
import org.mulesoft.als.suggestions.plugins.aml.webapi.raml.raml10.Raml10SecuritySchemesDialect

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object SecuritySettingsFacetsCompletionPlugin extends AMLCompletionPlugin {
  override def id: String = "SecuritySettingsFacetsCompletionPlugin"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      val fromReference =
        request.branchStack.exists(_.isInstanceOf[ParametrizedSecurityScheme])
      request.branchStack.headOption match {
        case Some(_: OAuth1Settings) if !fromReference =>
          Raml10SecuritySchemesDialect.OAuth1Settings.propertiesRaw(request.indentation)
        case Some(_: OAuth2Settings) if fromReference =>
          Seq(RawSuggestion("scopes", request.indentation, isAKey = true, "security"))
        case Some(_: OAuth2Settings) =>
          Raml10SecuritySchemesDialect.OAuth2Settings.propertiesRaw(request.indentation)
        case _ => Nil
      }
    }
  }
}
