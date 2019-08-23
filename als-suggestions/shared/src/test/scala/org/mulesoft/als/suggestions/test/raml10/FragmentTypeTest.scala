package org.mulesoft.als.suggestions.test.raml10

class FragmentTypeTest extends RAML10Test {
  // TODO: Enable with RAML as AML merge (spec-oriented-refactor)
  ignore("'#%RAML' completion") {
    this.runSuggestionTest(
      "fragmentType/test001.raml",
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

  ignore("'#%RAML ' completion") {
    this.runSuggestionTest(
      "fragmentType/test002.raml",
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

  ignore("'#%RAML D' completion") {
    this.runSuggestionTest("fragmentType/test003.raml", Set("DataType", "DocumentationItem"))
  }
}
