package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.apicontract.client.scala.model.domain.security.{OpenIdConnectSettings, SecurityScheme}
import amf.core.client.scala.model.domain.DomainElement
import org.mulesoft.als.suggestions._
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.plugins.aml.webapi.SecuredByCompletionPlugin

object RamlSecuredByCompletionPlugin extends SecuredByCompletionPlugin{

  protected def rawSuggestionBuilder(request: AmlCompletionRequest)(name: String, de: DomainElement): RawSuggestion = {
    if (isAKey(request) || compatibleParametrizedSecurityScheme(request)) {
      if (shouldBeAnObjectRange(de)) {
          RawSuggestion.apply(name, SuggestionStructure(isKey = true, rangeKind = ObjectRange))
      } else
        RawSuggestion.apply(name, SuggestionStructure(isKey = true, rangeKind = BoolScalarRange))
    } else if (!isAKey(request) && !isSecurityScalarValue(request))
      RawSuggestion.apply(name, SuggestionStructure(rangeKind = ArrayRange))
    else RawSuggestion.apply(name, SuggestionStructure())
  }

  private def isOpenIdConnectSettingsWithScopes(s: SecurityScheme): Boolean =
    s.settings match {
      case oi: OpenIdConnectSettings => oi.scopes.nonEmpty
      case _ => false
    }

  private def shouldBeAnObjectRange(de: DomainElement): Boolean = {
    de match {
      case s: SecurityScheme =>
        s.`type`.is("OAuth 2.0") || isOpenIdConnectSettingsWithScopes(s)
      case _ => false
    }
  }
}
