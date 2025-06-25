package org.mulesoft.als.server.modules.completion.mcp

import scala.concurrent.ExecutionContext

class MCPSuggestionsTests extends MCPSuggestionTestServer {

  override implicit val executionContext = ExecutionContext.Implicits.global

  // todo: JsonLDInstanceDocuments currently do not contain Lexical information, cannot proceed with tooling

  test("base") {
    runTest("structure1/base.mcp.json", Set.empty) // todo: add correct expected
  }
  test("tool object") {
    runTest("structure1/tool.mcp.json", Set()) // todo: add correct expected
  }
  test("tool array") {
    runTest("structure1/tool-in-array.mcp.json", Set()) // todo: add correct expected
  }
  test("input schema value") {
    runTest("structure1/input-schema-type.mcp.json", Set()) // todo: add correct expected
  }
  test("complex") {
    runTest("structure1/complete.mcp.json", Set()) // todo: add correct expected
  }
}
