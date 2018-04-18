package org.mulesoft.high.level.test.RAML10.AST

import org.mulesoft.high.level.test.RAML10.RAML10ASTTest

class Response extends RAML10ASTTest {

  test("Response types") {
    runTest("ASTTests/Response/api.raml", project => {

      var expectedValue = "200"
      project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("responses").head.attribute("code") match {
        case Some(a) => a.value match {
          case Some("200") => succeed
          case _ => fail(s"Expected value: $expectedValue, actual: ${a.value}")
        }
        case _ => fail("'types' attribute not found")
      }
    })
  }

  test("Response headers") {
    runTest("ASTTests/Response/api.raml", project => {

      var expectedValue = 1
      var length = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("responses").head.elements("headers").length
      if (length == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }

  test("Response header name") {
    runTest("ASTTests/Response/api.raml", project => {

      var expectedValue = "If-Match"
      var actualValue = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("responses").head.elements("headers").head.attribute("name").get.value.get
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Response body") {
    runTest("ASTTests/Response/api.raml", project => {

      var expectedValue = 1
      var length = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("responses").head.elements("body").length
      if (length == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }

  test("Response description") {
    runTest("ASTTests/Response/api.raml", project => {

      var expectedValue = "Ok"
      project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("responses").head.attribute("description") match {
        case Some(a) => a.value match {
          case Some("Ok") => succeed
          case _ => fail(s"Expected value: $expectedValue, actual: ${a.value}")
        }
        case _ => fail("'description' attribute not found")
      }
    })
  }
}
