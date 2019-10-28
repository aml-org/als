package org.mulesoft.als.suggestions.test.aml.AsyncAPI

import org.mulesoft.als.suggestions.test.aml.AMLSuggestionsTest

class LibraryTests extends AMLSuggestionsTest {

  def rootPath: String = "AML/AsyncAPI"

  test("test001") {
    this.runSuggestionTest("library/test001.yaml", Set("schemas:\n  ", "uses:\n  "))
  }

  test("test002") {
    this.runSuggestionTest("library/test002.yaml",
                           Set("externalDocs:\n      ", "description: ", "headers:\n      ", "tags:\n      - "))
  }

  test("test003") {
    this.runSuggestionTest("library/test003.yaml", Set())
  }

  test("test004") {
    this.runSuggestionTest(
      "library/test004.yaml",
      Set("number", "string", "\"null\"", "object", "array", "boolean", "integer")
    )
  }

  test("test005") {
    this.runSuggestionTest("library/test005.yaml", Set("\"null\"", "boolean", "string", "array", "number", "integer"))
  }

  test("test006") {
    this.runSuggestionTest(
      "library/test006.yaml",
      Set(
        "pattern: ",
        "maxItems: ",
        "required:\n        - ",
        "items:\n        ",
        "exclusiveMaximum: ",
        "$schema: ",
        "type: ",
        "xml:\n        ",
        "key: ",
        "minimum: ",
        "maximum: ",
        "default: ",
        "exclusiveMinimum: ",
        "multipleOf: ",
        "description: ",
        "minProperties: ",
        "patternProperties:\n        ",
        "maxLength: ",
        "title: ",
        "minLength: ",
        "minItems: ",
        "additionalItems: ",
        "id: ",
        "uniqueItems: "
      )
    )
  }

  test("test007") {
    this.runSuggestionTest("library/test007.yaml", Set("name: ", "description: "))
  }
}
