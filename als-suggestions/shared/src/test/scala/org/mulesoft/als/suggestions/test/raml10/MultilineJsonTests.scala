package org.mulesoft.als.suggestions.test.raml10

class MultilineJsonTests extends RAML10Test {

  test("test01") {
    this.runSuggestionTest("multiline-json/second-line-example.raml", Set())
  }
}
