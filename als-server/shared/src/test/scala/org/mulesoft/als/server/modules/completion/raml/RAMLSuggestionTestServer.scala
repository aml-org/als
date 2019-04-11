package org.mulesoft.als.server.modules.completion.raml

import org.mulesoft.als.server.modules.completion.ServerSuggestionsTest

trait RAMLSuggestionTestServer extends ServerSuggestionsTest {

  def rootPath: String = "suggestions/raml"

  def format: String = "RAML 1.0"
}
