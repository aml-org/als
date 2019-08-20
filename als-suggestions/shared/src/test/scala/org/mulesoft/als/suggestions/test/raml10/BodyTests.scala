package org.mulesoft.als.suggestions.test.raml10

class BodyTests extends RAML10Test {

  ignore("Method with no bodies") {
    this.runSuggestionTest(
      "body/test001.raml",
      Set(
        "application/json:\n        ",
        "application/xml:\n        ",
        "displayName: ",
        "type: ",
        "enum: ",
        "xml:\n        ",
        "default: ",
        "description: ",
        "schema: ",
        "examples:\n        ",
        "example:\n        ",
        "facets:\n        ",
        "properties:\n        ",
        "items: "
      )
    )
  }

  test("Method with some bodies") {
    this.runSuggestionTest("body/test002.raml", Set("application/json:\n        "))
  }

  test("Response with no bodies") {
    this.runSuggestionTest(
      "body/test003.raml",
      Set(
        "application/json:\n            ",
        "application/xml:\n            ",
        "displayName: ",
        "type:\n            ",
        "enum:\n            ",
        "xml:\n            ",
        "default: ",
        "description: ",
        "examples:\n            ",
        "example:\n            ",
        "facets:\n            ",
        "properties:\n            ",
        "items:\n            "
      )
    )
  }

  test("Response with some bodies") {
    this.runSuggestionTest("body/test004.raml", Set("application/json:\n            "))
  }

  test("Method body type shortcut") {
    this.runSuggestionTest(
      "body/test005.raml",
      Set("boolean",
          "integer",
          "datetime",
          "date-only",
          "datetime-only",
          "file",
          "any",
          "number",
          "string",
          "time-only",
          "nil",
          "array",
          "object",
          "A",
          "B")
    )
  }

  test("Response body type shortcut") {
    this.runSuggestionTest(
      "body/test006.raml",
      Set("boolean",
          "integer",
          "datetime",
          "date-only",
          "datetime-only",
          "file",
          "any",
          "number",
          "string",
          "time-only",
          "nil",
          "array",
          "object",
          "A",
          "B")
    )
  }
}
