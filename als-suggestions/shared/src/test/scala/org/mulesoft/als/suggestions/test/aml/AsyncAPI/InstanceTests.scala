package org.mulesoft.als.suggestions.test.aml.AsyncAPI

class InstanceTests extends AMLAsyncApi06SuggestionTest {

  def rootPath: String = "AML/AsyncAPI"

  test("test001") {
    runAsyncApiTest(
      "instance/test001.yaml",
      Set(
        "securitySchemes:\n  ",
        "schemas:\n  ",
        "uses:\n  ",
        "security:\n  - ",
        "servers:\n  - ",
        "simpleMap:\n  ",
        """servers:
          |  -
          |    url: $1
          |    scheme: $2""".stripMargin
      )
    )
  }

  test("test002") {
    runAsyncApiTest(
      "instance/test002.yaml",
      Set("termsOfService: ", "contact:\n  ", "description: ", "title: ", "license:\n  ")
    )
  }

  test("test003") {
    runAsyncApiTest("instance/test003.yaml", Set())
  }

  test("test004") {
    runAsyncApiTest(
      "instance/test004.yaml",
      Set("number", "string", "\"null\"", "object", "array", "boolean", "integer")
    )
  }

  test("test005") {
    runAsyncApiTest("instance/test005.yaml", Set("boolean", "\"null\"", "string", "array", "number", "integer"))
  }

  test("test006") {
    runAsyncApiTest(
      "instance/test006.yaml",
      Set(
        "externalDocs:\n  ",
        "headers:\n  ",
        "tags:\n  - ",
        "simpleMap:\n  ",
        """externalDocs:
                      |  url: $1""".stripMargin,
        """tags:
                        |  -
                        |    name: $1""".stripMargin
      )
    )
  }

  test("test007") {
    runAsyncApiTest("instance/test007.yaml", Set("name: ", "description: "))
  }

  test("test008") {
    runAsyncApiTest("instance/test008.yaml", Set("\"null\"", "string", "array", "object", "number", "integer"))
  }

  test("test root level suggestions") {
    runAsyncApiTest(
      "instance/root-suggestions.yaml",
      Set(
        "topics:\n  ",
        "schemas:\n  ",
        "info:\n  ",
        "externalDocs:\n  ",
        "servers:\n  - ",
        "baseTopic: ",
        "asyncapi: ",
        "messages:\n  ",
        "security:\n  - ",
        "simpleMap:\n  ",
        "securitySchemes:\n  ",
        "uses:\n  ",
        """info:
          |  title: $1
          |  version: $2""".stripMargin,
        """servers:
          |  -
          |    url: $1
          |    scheme: $2""".stripMargin,
        """externalDocs:
          |  url: $1""".stripMargin
      )
    )
  }

  test("empty file test") {
    runAsyncApiTest("instance/empty.yaml", Set("#%Library / AsyncAPI 0.6"))
  }

  test("test suggestions with component key") {
    withDialect(
      "instance/component-key-suggestions.yaml",
      Set(
        "asyncapi: ",
        "baseTopic: ",
        "info:\n  ",
        "servers:\n  - ",
        "topics:\n  ",
        "security:\n  - ",
        "externalDocs:\n  ",
        "simpleMap:\n  ",
        "components:\n  ",
        "uses:\n  ",
        """servers:
          |  -
          |    url: $1
          |    scheme: $2""".stripMargin,
        """info:
          |  title: $1
          |  version: $2""".stripMargin,
        """externalDocs:
          |  url: $1""".stripMargin
      ),
      "dialect10.yaml"
    )
  }

  test("test declaration suggestions in component key") {
    withDialect(
      "instance/suggestions-in-component-key.yaml",
      Set("schemas:\n  ", "messages:\n  ", "securitySchemes:\n  "),
      "dialect10.yaml"
    )
  }
}
