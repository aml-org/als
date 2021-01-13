package org.mulesoft.als.suggestions.test.aml.AsyncAPI

class FragmentTests extends AMLAsyncApi06SuggestionTest {

  def rootPath: String = "AML/AsyncAPI"

  test("test001") {
    runAsyncApiTest(
      "fragment/test001.yaml",
      Set(
        "externalDocs:\n  ",
        "uses:\n  ",
        "description: ",
        "headers:\n  ",
        "tags:\n  - ",
        "externalDocs:\n  url: $1",
        """tags:
                      |  -
                      |    name: $1""".stripMargin
      )
    )
  }

  test("test002") {
    runAsyncApiTest(
      "fragment/test002.yaml",
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

  test("test003") {
    runAsyncApiTest("fragment/test003.yaml", Set())
  }

  test("test004") {
    runAsyncApiTest(
      "fragment/test004.yaml",
      Set("number", "string", "\"null\"", "object", "array", "boolean", "integer")
    )
  }

  test("test005") {
    runAsyncApiTest("fragment/test005.yaml", Set("\"null\"", "boolean", "string", "array", "number", "integer"))
  }

  test("test006") {
    runAsyncApiTest(
      "fragment/test006.yaml",
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
    runAsyncApiTest("fragment/test007.yaml", Set("name: ", "description: "))
  }
}
