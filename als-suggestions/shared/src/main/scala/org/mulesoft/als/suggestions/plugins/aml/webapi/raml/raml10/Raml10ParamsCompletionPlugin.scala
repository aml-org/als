package org.mulesoft.als.suggestions.plugins.aml.webapi.raml.raml10

import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.plugins.aml.webapi.raml.RamlParamsCompletionPlugin

object Raml10ParamsCompletionPlugin
    extends RamlParamsCompletionPlugin(
      Raml10TypeFacetsCompletionPlugin,
      Seq(RawSuggestion.forKey("required", "parameters", mandatory = false))
    )
