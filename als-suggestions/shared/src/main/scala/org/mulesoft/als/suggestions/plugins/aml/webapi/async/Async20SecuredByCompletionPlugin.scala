package org.mulesoft.als.suggestions.plugins.aml.webapi.async

import amf.core.client.scala.model.domain.DomainElement
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.plugins.aml.webapi.SecuredByCompletionPlugin
import org.mulesoft.als.suggestions.{ArrayRange, BoolScalarRange, RawSuggestion, SuggestionStructure}

object Async20SecuredByCompletionPlugin extends SecuredByCompletionPlugin{

  protected def rawSuggestionBuilder(request: AmlCompletionRequest)(name: String, de: DomainElement): RawSuggestion = {
    if (isAKey(request) || compatibleParametrizedSecurityScheme(request)) {
      if (hasScopes(de))
        RawSuggestion.apply(name, SuggestionStructure(isKey = true, rangeKind = ArrayRange))
      else
        RawSuggestion.apply(name, SuggestionStructure(isKey = true, rangeKind = BoolScalarRange))
    } else if (!isAKey(request) && !isSecurityScalarValue(request))
      RawSuggestion.apply(name, SuggestionStructure(rangeKind = ArrayRange))
    else RawSuggestion.apply(name, SuggestionStructure())
  }
}
