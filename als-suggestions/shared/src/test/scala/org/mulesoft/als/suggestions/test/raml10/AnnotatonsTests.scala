package org.mulesoft.als.suggestions.test.raml10

class AnnotatonsTests extends RAML10Test {
  test("Method annotations 1") {
    this.runSuggestionTest("annotations/test01.raml", Set("AOne", "ATwo"))
  }

  test("Method annotations 2") {
    this.runSuggestionTest("annotations/test02.raml", Set("AOne): ", "ATwo): "))
  }

  test("Resource annotations 1") {
    this.runSuggestionTest("annotations/test03.raml", Set("AOne", "ATwo"))
  }

  test("Resource annotations 2") {
    this.runSuggestionTest("annotations/test04.raml", Set("AOne): ", "ATwo): "))
  }

  test("TypeDeclaration annotations 1") {
    this.runSuggestionTest("annotations/test05.raml", Set("AOne", "ATwo"))
  }

  test("TypeDeclaration annotations 2") {
    this.runSuggestionTest("annotations/test06.raml", Set("AOne): ", "ATwo): "))
  }
}
