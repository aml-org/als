package org.mulesoft.als.suggestions.test.aml.AsyncAPI

import amf.ProfileName
import org.mulesoft.als.suggestions.test.aml.AMLSuggestionsTest

class InstanceTests extends AMLSuggestionsTest {

  def rootPath: String = "AML/AsyncAPI"

  test("test001") {
    this.runSuggestionTest(
      "instance/test001.yaml",
      Set("securitySchemes:\n  ", "schemas:\n  ", "security:\n  ", "servers:\n  ", "simpleMap:\n  "))
  }

  test("test002") {
    this.runSuggestionTest("instance/test002.yaml",
                           Set("termsOfService: ", "contact:\n    ", "description: ", "title: ", "license:\n    "))
  }

  test("test003") {
    this.runSuggestionTest("instance/test003.yaml", Set())
  }

  test("test004") {
    this.runSuggestionTest(
      "instance/test004.yaml",
      Set(
        "\n          - null",
        "\n          - boolean",
        "\n          - string",
        "\n          - array",
        "\n          - object",
        "\n          - number",
        "\n          - integer"
      )
    )
  }

  test("test005") {
    this.runSuggestionTest("instance/test005.yaml",
                           Set("null", "boolean", "string", "array", "object", "number", "integer"))
  }

  test("test006") {
    this.runSuggestionTest(
      "instance/test006.yaml",
      Set("externalDocs:\n        ", "headers:\n        ", "tags:\n        ", "simpleMap:\n        "))
  }

  test("test007") {
    this.runSuggestionTest("instance/test007.yaml", Set("name: ", "description: "))
  }

  test("test008") {
    this.runSuggestionTest("instance/test008.yaml",
                           Set("null", "boolean", "string", "array", "object", "number", "integer"))
  }

  test("test root level suggestions") {
    this.runSuggestionTest(
      "instance/root-suggestions.yaml",
      Set(
        "topics:\n  ",
        "schemas:\n  ",
        "info:\n  ",
        "externalDocs:\n  ",
        "servers:\n  ",
        "baseTopic: ",
        "asyncapi: ",
        "messages:\n  ",
        "security:\n  ",
        "simpleMap:\n  ",
        "securitySchemes:\n  "
      )
    )
  }

  test("test suggestions with component key") {
    withDialect(
      "instance/component-key-suggestions.yaml",
      Set("asyncapi: ",
          "baseTopic: ",
          "info:\n  ",
          "servers:\n  ",
          "topics:\n  ",
          "security:\n  ",
          "externalDocs:\n  ",
          "simpleMap:\n  ",
          "components:\n  "),
      "dialect10.yaml",
      ProfileName("AsyncAPI 1.0")
    )
  }

  test("test declaration suggestions in component key") {
    withDialect(
      "instance/suggestions-in-component-key.yaml",
      Set("schemas:\n    ", "messages:\n    ", "securitySchemes:\n    "),
      "dialect10.yaml",
      ProfileName("AsyncAPI 1.0")
    )
  }

  test("empty file test") {
    this.runSuggestionTest("instance/empty.yaml", Set("#%Library/AsyncAPI0.6"))
  }
}
