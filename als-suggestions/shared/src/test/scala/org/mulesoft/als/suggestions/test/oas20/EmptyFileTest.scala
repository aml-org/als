package org.mulesoft.als.suggestions.test.oas20

class EmptyFileTest extends OAS20Test {

  test("Empty YAML file completion") {
    this.runSuggestionTest("empty/file/empty.yml", Set("#%RAML 1.0", "#%RAML 0.8", "swagger: '2.0'"))
  }

  test("Empty JSON file completion") {
    this.runSuggestionTest("empty/file/empty.json", Set("\"swagger\": \"2.0\""))
  }

}
