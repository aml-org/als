package org.mulesoft.als.suggestions.test.raml10

class ResourceTypeDefinitionTests extends RAML10Test {

  test("Name level test") {
    this.runSuggestionTest("resourceTypeDefinition/name-level.raml", Set.empty)
  }

  test("Endpoint facets level") {
    this.runSuggestionTest(
      "resourceTypeDefinition/endpoint-level.raml",
      Set(
        "displayName: ",
        "description: ",
        "usage: ",
        "get:\n      ",
        "patch:\n      ",
        "put:\n      ",
        "post:\n      ",
        "delete:\n      ",
        "options:\n      ",
        "head:\n      ",
        "is:\n      ",
        "type:\n      ",
        "securedBy: ",
        "uriParameters:\n      "
      )
    )
  }

  test("Operation facets level") {
    this.runSuggestionTest(
      "resourceTypeDefinition/operation-level.raml",
      Set(
        "displayName: ",
        "description: ",
        "queryParameters:\n        ",
        "headers:\n        ",
        "queryString:\n        ",
        "responses:\n        ",
        "body:\n        ",
        "protocols: ",
        "is:\n        ",
        "securedBy: "
      )
    )
  }

  test("Resource reference in resource type def") {
    this.runSuggestionTest(
      "resourceTypeDefinition/resource-type-ref.raml",
      Set("r2")
    )
  }

}
