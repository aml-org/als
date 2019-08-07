package org.mulesoft.als.suggestions.test.raml10

class TraitDefinitionTests extends RAML10Test {

  test("Name level test") {
    this.runSuggestionTest("traitDefinition/name-level.raml", Set.empty)
  }

  test("Operation facets level") {
    this.runSuggestionTest(
      "traitDefinition/operation-level.raml",
      Set(
        "displayName: ",
        "description: ",
        "queryParameters:\n      ",
        "headers:\n      ",
        "queryString:\n      ",
        "responses:\n      ",
        "body:\n      ",
        "protocols: ",
        "is:\n      ",
        "securedBy: "
      )
    )
  }

  test("Reponses facets level") {
    this.runSuggestionTest(
      "traitDefinition/response-level.raml",
      Set(
        "description: ",
        "headers:\n          ",
        "body:\n          ",
      )
    )
  }

  test("Trait reference in trait def") {
    this.runSuggestionTest(
      "traitDefinition/trait-ref.raml",
      Set("t2")
    )
  }

}
