package org.mulesoft.als.suggestions.test.raml10

class AnnotatonsTests extends RAML10Test {
  test("Method annotations 1") {
    this.runSuggestionTest("annotations/test01.raml", Set("(AOne): ", "(ATwo): "))
  }

  test("Method annotations 2") {
    this.runSuggestionTest(
      "annotations/test02.raml",
      Set(
        "queryString:\n      ",
        "queryParameters:\n      ",
        "description: ",
        "displayName: ",
        "(AOne): ",
        "is:\n      ",
        "protocols: ",
        "headers:\n      ",
        "(ATwo): ",
        "securedBy: ",
        "body:\n      "
      )
    )
  }

  test("Resource annotations 1") {
    this.runSuggestionTest("annotations/test03.raml", Set("(AOne): ", "(ATwo): "))
  }

  ignore("Resource annotations 2") {
    this.runSuggestionTest("annotations/test04.raml", Set("AOne): ", "ATwo): "))
  }

  ignore("TypeDeclaration annotations 1") {
    this.runSuggestionTest("annotations/test05.raml", Set("AOne", "ATwo"))
  }

  ignore("TypeDeclaration annotations 2") {
    this.runSuggestionTest("annotations/test06.raml", Set("AOne): ", "ATwo): "))
  }
}
