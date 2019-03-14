package org.mulesoft.als.suggestions.test.raml08

class BodyTests extends RAML08Test {

  test("Method with no bodies") {
    this.runTest(
      "body/test001.raml",
      Set(
        "application/json:\n        ",
        "application/xml:\n        ",
        "multipart/form-data:\n        ",
        "application/x-www-form-urlencoded:\n        ",
        "formParameters:\n        ",
        "description: ",
        "schema: ",
        "example: "
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
      Set("application/json:\n            ",
          "application/xml:\n            ",
          "formParameters:\n            ",
          "description: ",
          "schema: ",
          "example: ")
    )
  }

  test("Response with some bodies") {
    this.runTest("body/test004.raml", Set("application/json:\n          "))
  }
}
