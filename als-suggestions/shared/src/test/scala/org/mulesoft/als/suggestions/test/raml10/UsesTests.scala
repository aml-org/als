package org.mulesoft.als.suggestions.test.raml10

class UsesTests extends RAML10Test {

  test("test01") {
    this.runSuggestionTest("uses/testGroup01/test01.raml", Set("testLib.raml", "testLib2.raml"))
  }

  test("test02") {
    this.runSuggestionTest("uses/testGroup01/test02.raml", Set("testLib.raml", "testLib2.raml"))
  }

  test("test03") {
    this.runSuggestionTest("uses/testGroup01/test03.raml", Set("raml"))
  }

  test("test04") {
    this.runSuggestionTest("uses/testGroup01/test04.raml", Set("libraries/"))
  }

  test("test05") {
    this.runSuggestionTest("uses/testGroup02/test01.raml", Set("test02.raml", "testLib.raml", "testLib2.raml"))
  }

  test("test06") {
    this.runSuggestionTest("uses/testGroup02/test02.raml", Set("raml"))
  }
}
