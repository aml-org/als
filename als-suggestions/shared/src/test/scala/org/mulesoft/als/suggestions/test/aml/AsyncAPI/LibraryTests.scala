package org.mulesoft.als.suggestions.test.aml.AsyncAPI

import org.mulesoft.als.suggestions.test.aml.AMLSuggestionsTest

class LibraryTests extends AMLSuggestionsTest {

  def rootPath: String = "AML/AsyncAPI"

  test("test001") {
    this.runTest("library/test001.yaml", Set("schemas:\n  "))
  }

  test("test002") {
    this.runTest("library/test002.yaml",
                 Set("externalDocs:\n      ", "description: ", "headers:\n      ", "tags:\n      "))
  }

  test("test003") {
    this.runTest("library/test003.yaml", Set())
  }

  test("test004") {
    this.runTest(
      "library/test004.yaml",
      Set(
        "\n            - null",
        "\n            - boolean",
        "\n            - string",
        "\n            - array",
        "\n            - object",
        "\n            - number",
        "\n            - integer"
      )
    )
  }

  test("test005") {
    this.runTest("library/test005.yaml", Set("null", "boolean", "string", "array", "object", "number", "integer"))
  }

  test("test006") {
    this.runTest(
      "library/test006.yaml",
      Set(
        "pattern: ",
        "maxItems: ",
        "required: ",
        "items:\n        ",
        "exclusiveMaximum: ",
        "\"$schema\": ",
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
    this.runTest("library/test007.yaml", Set("name: ", "description: "))
  }
}
