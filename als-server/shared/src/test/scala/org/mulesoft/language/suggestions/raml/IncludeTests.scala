package org.mulesoft.language.suggestions.raml

class IncludeTests extends RAMLSuggestionTest {

  test("test01") {
    this.runTest("includes/testGroup01/test01.raml", Set("testFragment.raml", "testFragment2.raml"))
  }

  test("test02") {
    this.runTest("includes/testGroup01/test02.raml", Set("testFragment.raml", "testFragment2.raml"))
  }

  test("test03") {
    this.runTest("includes/testGroup01/test03.raml", Set("raml"))
  }

  test("test04") {
    this.runTest("includes/testGroup01/test04.raml", Set("fragments/"))
  }

  test("test05") {
    this.runTest("includes/testGroup02/test01.raml", Set("test02.raml", "testFragment.raml", "testFragment2.raml"))
  }

  test("test06") {
    this.runTest("includes/testGroup02/test02.raml", Set("raml"))
  }
}
