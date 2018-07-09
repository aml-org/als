package org.mulesoft.high.level.test.RAML08.AST

import org.mulesoft.high.level.test.RAML08.RAML08ASTTest

class BodyLike extends RAML08ASTTest{
  test("BodyLike name") {
    runTest("ASTTests/BodyLike/api.raml", project => {

      var expectedValue = "application/x-www-form-urlencoded"
      var actualValue = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("body").head.attribute("name").get.value.get
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("BodyLike formParameters") {
    runTest("ASTTests/BodyLike/api.raml", project => {

      var expectedValue = 2
      var actualValue = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("body").head.elements("formParameters").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("BodyLike example") {
    runTest("ASTTests/BodyLike/api.raml", project => {

      var expectedValue = Some("{\n  \"input\": \"s3://zencodertesting/test.mov\"\n}\n")
      var actualValue = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("responses").head.elements("body").head.attribute("example").get.value
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }
}
