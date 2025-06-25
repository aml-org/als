package org.mulesoft.als.server.modules.completion.mcp

import org.mulesoft.als.server.modules.completion.ServerSuggestionsTest

trait MCPSuggestionTestServer extends ServerSuggestionsTest {

  def rootPath: String = "suggestions/mcp"

  def format: String = "MCP"
}
