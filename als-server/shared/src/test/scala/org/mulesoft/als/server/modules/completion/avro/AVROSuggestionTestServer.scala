package org.mulesoft.als.server.modules.completion.avro

import org.mulesoft.als.server.modules.completion.ServerSuggestionsTest

trait AVROSuggestionTestServer extends ServerSuggestionsTest {

  def rootPath: String = "suggestions/avro"

  def format: String = "AVRO"
}
