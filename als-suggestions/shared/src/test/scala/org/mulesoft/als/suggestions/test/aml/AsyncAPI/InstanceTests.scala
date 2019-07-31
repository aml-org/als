package org.mulesoft.als.suggestions.test.aml.AsyncAPI

import org.mulesoft.als.suggestions.test.aml.AMLSuggestionsTest

class InstanceTests extends AMLSuggestionsTest {

  def rootPath: String = "AML/AsyncAPI"

  test("test001") {
    this.runSuggestionTest(
      "instance/test001.yaml",
      Set("securitySchemes:\n  ", "schemas:\n  ", "security:\n  ", "servers:\n  ", "simpleMap:\n  "))
  }

  //TODO: review result set, and verify termsOfService is effectively missing
  ignore("test002") {
    this.runSuggestionTest("instance/test002.yaml",
                           Set("contact:\n    ", "description: ", "title: ", "license:\n    "))
  }

  test("test003") {
    this.runSuggestionTest("instance/test003.yaml", Set())
  }

  test("test004") {
    this.runSuggestionTest(
      "instance/test004.yaml",
      Set("[ null ]", "[ boolean ]", "[ string ]", "[ array ]", "[ object ]", "[ number ]", "[ integer ]"))
  }

//    test("test005"){
//        this.runSuggestionTest("instance/test005.yaml", Set("null", "boolean", "string", "array", "object", "number", "integer"))
//    }

  test("test006") {
    this.runSuggestionTest(
      "instance/test006.yaml",
      Set("externalDocs:\n        ", "headers:\n        ", "tags:\n        ", "simpleMap:\n        "))
  }

  test("test007") {
    this.runSuggestionTest("instance/test007.yaml", Set("name: ", "description: "))
  }

}
