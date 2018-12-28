package org.mulesoft.als.suggestions.test.raml10

class BodyTests extends RAML10Test {

  test("Method with no bodies") {
    this.runTest(
      "body/test001.raml",
      Set(
        "application/json:\n        ",
        "application/xml:\n        ",
        "multipart/form-data:\n        ",
        "application/x-www-form-urlencoded:\n        ",
        "displayName:",
        "type:",
        "xml:\n        ",
        "default:",
        "description:",
        "schema:",
        "examples:\n        ",
        "example:\n        ",
        "facets:\n        ",
        "properties:\n        "
      )
    )
  }

  test("Method with some bodies") {
    this.runTest(
      "body/test002.raml",
      Set("application/json:\n      ", "multipart/form-data:\n      ", "application/x-www-form-urlencoded:\n      "))
  }

  test("Response with no bodies") {
    this.runTest(
      "body/test003.raml",
      Set(
        "application/json:\n            ",
        "application/xml:\n            ",
        "displayName:",
        "type:",
        "xml:\n            ",
        "default:",
        "description:",
        "schema:",
        "examples:\n            ",
        "example:\n            ",
        "facets:\n            ",
        "properties:\n            "
      )
    )
  }

  test("Response with some bodies") {
    this.runTest("body/test004.raml", Set("application/json:\n          "))
  }

  test("Method body type shortcut") {
    this.runTest(
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
    this.runTest(
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
