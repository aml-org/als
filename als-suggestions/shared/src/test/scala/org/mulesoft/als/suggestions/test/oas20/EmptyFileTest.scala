package org.mulesoft.als.suggestions.test.oas20

class EmptyFileTest extends OAS20Test {
  // TODO: enable when OAS20Dialect is enabled
  ignore("Empty YAML file completion") {
    this.runSuggestionTest(
      "empty/file/empty.yml",
      Set(
        "#%Patch/AsyncAPI0.6",
        "#%Library/AsyncAPI0.6",
        "#%SecurityScheme/AsyncAPI0.6",
        "swagger: \"2.0\"",
        "#%Schema/AsyncAPI0.6",
        "#%AsyncAPI0.6",
        "#%Message/AsyncAPI0.6"
      )
    )
  }

  ignore("After Key empty YAML") {
    this.runSuggestionTest("empty/file/afterKey.yml", Set("swagger: \"2.0\""))
  }

  ignore("Empty JSON file completion (no brackets)") {
    this.runSuggestionTest("empty/file/withoutBracket.json", Set("{\n  \"swagger\": \"2.0\"\n}"))
  }

  ignore("Empty JSON file completion") {
    this.runSuggestionTest("empty/file/empty.json", Set("\"swagger\": \"2.0\""))
  }

  ignore("Empty JSON file completion (with swagger key)") {
    this.runSuggestionTest("empty/file/emptyWithKey.json", Set("\"swagger\": \"2.0\""))
  }

  test("Empty JSON file completion (with wrong swagger key)") {
    this.runSuggestionTest("empty/file/emptyWithWrongKey.json", Set())
  }
}
