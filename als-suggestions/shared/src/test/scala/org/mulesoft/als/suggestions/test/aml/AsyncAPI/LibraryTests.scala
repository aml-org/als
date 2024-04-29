package org.mulesoft.als.suggestions.test.aml.AsyncAPI

class LibraryTests extends AMLAsyncApi06SuggestionTest {

  def rootPath: String = "AML/AsyncAPI"

  test("test001") {
    runAsyncApiTest("library/test001.yaml", Set("schemas:\n  ", "uses:\n  "))
  }

  test("test002") {
    runAsyncApiTest(
      "library/test002.yaml",
      Set(
        "externalDocs:\n  ",
        "description: ",
        "headers:\n  ",
        "tags:\n  - ",
        """tags:
                           |  -
                           |    name: $1""".stripMargin,
        """externalDocs:
                           |  url: $1""".stripMargin
      )
    )
  }

  test("test003") {
    runAsyncApiTest("library/test003.yaml", Set())
  }

  test("test004") {
    runAsyncApiTest(
      "library/test004.yaml",
      Set("number", "string", "\"null\"", "object", "array", "boolean", "integer")
    )
  }

  test("test005") {
    runAsyncApiTest("library/test005.yaml", Set("\"null\"", "boolean", "string", "array", "number", "integer"))
  }

  test("test006") {
    runAsyncApiTest(
      "library/test006.yaml",
      Set(
        "pattern: ",
        "maxItems: ",
        "required:\n  - ",
        "items:\n  ",
        "exclusiveMaximum: ",
        "$schema: ",
        "type: ",
        "xml:\n  ",
        "key: ",
        "minimum: ",
        "maximum: ",
        "default: ",
        "exclusiveMinimum: ",
        "multipleOf: ",
        "description: ",
        "minProperties: ",
        "patternProperties:\n  ",
        "maxLength: ",
        "title: ",
        "minLength: ",
        "minItems: ",
        "additionalItems: ",
        "id: ",
        "uniqueItems: "
      )
    )
  }

  test("test007") {
    runAsyncApiTest("library/test007.yaml", Set("name: ", "description: "))
  }
}
