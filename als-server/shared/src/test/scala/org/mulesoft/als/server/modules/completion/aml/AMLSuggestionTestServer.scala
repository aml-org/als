package org.mulesoft.als.server.modules.completion.aml

import org.mulesoft.als.server.modules.completion.ServerSuggestionsTest

trait AMLSuggestionTestServer extends ServerSuggestionsTest {

  def rootPath: String = "suggestions/aml"

  def format: String = "AML"
}
