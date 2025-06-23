package org.mulesoft.als.server.modules.completion.mcp

import scala.concurrent.ExecutionContext

class MCPSuggestionsTests extends MCPSuggestionTestServer {

  override implicit val executionContext = ExecutionContext.Implicits.global

  // todo: JsonLDInstanceDocuments currently do not contain Lexical information, cannot proceed with tooling

  test("test 01") {
    runTest("structure1/base.mcp.json", Set.empty) // todo: add correct expected
  }
  test("test 02") {
    runTest("structure1/tool.mcp.json", Set()) // todo: add correct expected
  }
}
