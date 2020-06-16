package org.mulesoft.als.suggestions.test.aml.AsyncAPI

import amf.ProfileName
import org.mulesoft.als.suggestions.test.aml.AMLSuggestionsTest

class InstanceTests extends AMLSuggestionsTest {

  def rootPath: String = "AML/AsyncAPI"

  test("test001") {
    runSuggestionTest(
      "instance/test001.yaml",
      Set("securitySchemes:\n  ", "schemas:\n  ", "uses:\n  ", "security:\n  - ", "servers:\n  - ", "simpleMap:\n  "))
  }

  test("test002") {
    runSuggestionTest("instance/test002.yaml",
                      Set("termsOfService: ", "contact:\n  ", "description: ", "title: ", "license:\n  "))
  }

  test("test003") {
    runSuggestionTest("instance/test003.yaml", Set())
  }

  test("test004") {
    runSuggestionTest(
      "instance/test004.yaml",
      Set("number", "string", "\"null\"", "object", "array", "boolean", "integer")
    )
  }

  test("test005") {
    runSuggestionTest("instance/test005.yaml", Set("boolean", "\"null\"", "string", "array", "number", "integer"))
  }

  test("test006") {
    runSuggestionTest("instance/test006.yaml",
                      Set("externalDocs:\n  ", "headers:\n  ", "tags:\n  - ", "simpleMap:\n  "))
  }

  test("test007") {
    runSuggestionTest("instance/test007.yaml", Set("name: ", "description: "))
  }

  test("test008") {
    runSuggestionTest("instance/test008.yaml", Set("\"null\"", "string", "array", "object", "number", "integer"))
  }

  test("test root level suggestions") {
    runSuggestionTest(
      "instance/root-suggestions.yaml",
      Set(
        "topics:\n  ",
        "schemas:\n  ",
        "info:\n  ",
        "externalDocs:\n  ",
        "servers:\n  - ",
        "baseTopic: ",
        "asyncapi: ",
        "messages:\n  ",
        "security:\n  - ",
        "simpleMap:\n  ",
        "securitySchemes:\n  ",
        "uses:\n  "
      )
    )
  }

  test("empty file test") {
    runSuggestionTest("instance/empty.yaml", Set("#%Library / AsyncAPI 0.6"))
  }

  test("test suggestions with component key") {
    withDialect(
      "instance/component-key-suggestions.yaml",
      Set(
        "asyncapi: ",
        "baseTopic: ",
        "info:\n  ",
        "servers:\n  - ",
        "topics:\n  ",
        "security:\n  - ",
        "externalDocs:\n  ",
        "simpleMap:\n  ",
        "components:\n  ",
        "uses:\n  "
      ),
      "dialect10.yaml",
      ProfileName("AsyncAPI 1.0")
    )
  }

  test("test declaration suggestions in component key") {
    withDialect(
      "instance/suggestions-in-component-key.yaml",
      Set("schemas:\n  ", "messages:\n  ", "securitySchemes:\n  "),
      "dialect10.yaml",
      ProfileName("AsyncAPI 1.0")
    )
  }
}
