package org.mulesoft.high.level.test.RAML08.AST

import org.mulesoft.high.level.test.RAML08.RAML08ASTTest

class Method extends RAML08ASTTest {

  test("Method queryParameters") {
    runTest("ASTTests/Method/query_parameters.raml", project => {

      var expectedValue = 1
      var length = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("queryParameters").length
      if (length == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }

  test("Method headers") {
    runTest("ASTTests/Method/headers.raml", project => {

      var expectedValue = 4
      var length = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("headers").length
      if (length == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }

  test("Method responses") {
    runTest("ASTTests/Method/responses.raml", project => {

      var expectedValue = 3
      var length = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("responses").length
      if (length == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }

  test("Method body") {
    runTest("ASTTests/Method/body.raml", project => {

      var expectedValue = 1
      var length = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("body").length
      if (length == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }

  test("Method protocols") {
    runTest("ASTTests/Method/protocols.raml", project => {

      var expectedValue = 2
      var length = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.attributes("protocols").length
      if (length == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }

  test("Method is") {
    runTest("ASTTests/Method/is.raml", project => {

      var expectedValue = 2
      var length = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("is").length
      if (length == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }

  test("Method securedBy") {
    runTest("ASTTests/Method/secured_by.raml", project => {

      var expectedValue = 1
      var length = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("securedBy").length
      if (length == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }
/*

  test("Method securedBy name") {
    runTest("ASTTests/Method/secured_by.raml", project => {

      var expectedValue = "oauth2"
      var length = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("securedBy").head.attribute("name").get.value
      if (length == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }
*/

  test("Method description"){
    runTest( "ASTTests/Method/description.raml", project => {

      var expectedValue = "test"
      project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.attribute("description") match {
        case Some(a) => a.value match {
          case Some(expectedValue) => succeed
          case _ => fail(s"Expected value: $expectedValue, actual: ${a.value}")
        }
        case _ => fail("'description' attribute not found")
      }
    })
  }

  test("Method method"){
    runTest( "ASTTests/Method/method.raml", project => {

      var expectedValue = "post"
      project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.attribute("method") match {
        case Some(a) => a.value match {
          case Some(expectedValue) => succeed
          case _ => fail(s"Expected value: $expectedValue, actual: ${a.value}")
        }
        case _ => fail("'method' attribute not found")
      }
    })
  }
}
