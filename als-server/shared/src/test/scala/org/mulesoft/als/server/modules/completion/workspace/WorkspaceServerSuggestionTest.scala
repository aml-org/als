package org.mulesoft.als.server.modules.completion.workspace

import org.mulesoft.als.server.modules.completion.ServerWorkspaceSuggestionsTest

import scala.concurrent.ExecutionContext

class WorkspaceServerSuggestionTest extends ServerWorkspaceSuggestionsTest {

  override implicit def executionContext: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
  override def rootPath: String                            = "workspace"

  test("test tooling in no-spec external fragments") {
    runTest(
      "external-fragment-1/schema.json",
      "api.raml",
      "external-fragment-1",
      Set(
        "\"$schema\": \"http://json-schema.org/draft/2019-09/schema#\""
      )
    )
  }

  test("test tooling in json schema external fragments") {
    runTest(
      "external-fragment-2/schema.json",
      "api.raml",
      "external-fragment-2",
      Set(
        "\"null\"",
        "\"object\"",
        "\"boolean\"",
        "\"array\"",
        "\"string\"",
        "\"integer\"",
        "\"number\""
      )
    )
  }

  test("test tooling in empty json") {
    runTest(
      "external-fragment-3/schema.json",
      "api.yaml",
      "external-fragment-3",
      Set(
        "{\n    \"swagger\": \"2.0\"\n}",
        "{\n    \"asyncapi\": \"2.0.0\"\n}",
        "{\n    \"$schema\": \"http://json-schema.org/draft-07/schema#\"\n}",
        "{\n    \"openapi\": \"3.0.0\"\n}",
        "{\n    \"$schema\": \"http://json-schema.org/draft-04/schema#\"\n}",
        "{\n    \"$schema\": \"http://json-schema.org/draft/2019-09/schema#\"\n}",
        "{\n    \"asyncapi\": \"2.6.0\"\n}"
      )
    )
  }
}
