package org.mulesoft.als.suggestions.test.raml10

class EmptyFileTest extends RAML10Test {

  test("Empty YAML file completion") {
    this.runSuggestionTest("empty/test001.raml", Set("#%RAML 1.0", "#%RAML 0.8", "swagger: '2.0'"))
  }

  test("'#' completion") {
    this.runSuggestionTest("empty/test002.raml", Set("#%RAML 1.0", "#%RAML 0.8"))
  }

  test("'#%' completion") {
    this.runSuggestionTest("empty/test003.raml", Set("#%RAML 1.0", "#%RAML 0.8"))
  }

  test("'#%RA' completion") {
    this.runSuggestionTest("empty/test004.raml", Set("#%RAML 1.0", "#%RAML 0.8"))
  }

  test("'#%RAML' completion") {
    this.runSuggestionTest("empty/test005.raml", Set(" 1.0", " 0.8"))
  }

  test("'#%RAML\\SPACE ' completion") {
    this.runSuggestionTest("empty/test006.raml", Set("1.0", "0.8"))
  }

  test("'#%RAML 1' completion") {
    this.runSuggestionTest("empty/test007.raml", Set("1.0"))
  }

  test("'#%RAML 0' completion") {
    this.runSuggestionTest("empty/test008.raml", Set("0.8"))
  }

  test("'#%RAML 1.' completion") {
    this.runSuggestionTest("empty/test012.raml", Set("1.0"))
  }

  test("'#%RAML 0.' completion") {
    this.runSuggestionTest("empty/test013.raml", Set("0.8"))
  }

  test("'#%RAML 1.0' completion") {
    this.runSuggestionTest(
      "empty/test009.raml",
      Set(
        " ResourceType",
        " Trait",
        " AnnotationTypeDeclaration",
        " DataType",
        " DocumentationItem",
        " NamedExample",
        " Extension",
        " SecurityScheme",
        " Overlay",
        " Library"
      )
    )
  }

  test("'#%RAML 1.0\\SPACE' completion") {
    this.runSuggestionTest(
      "empty/test010.raml",
      Set("ResourceType",
          "Trait",
          "AnnotationTypeDeclaration",
          "DataType",
          "DocumentationItem",
          "NamedExample",
          "Extension",
          "SecurityScheme",
          "Overlay",
          "Library")
    )
  }

  test("'#%RAML 1.0 D' completion") {
    this.runSuggestionTest("empty/test011.raml", Set("DataType", "DocumentationItem"))
  }
}
