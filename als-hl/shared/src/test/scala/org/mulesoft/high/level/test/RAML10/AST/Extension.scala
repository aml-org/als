package org.mulesoft.high.level.test.RAML10.AST

import org.mulesoft.high.level.test.RAML10.RAML10ASTTest

class Extension extends RAML10ASTTest {

  test("Extension usage"){
    runTest( "ASTTests/Extension/Extension.raml", project => {

      var expectedValue = "usage"
      var actualValue = project.rootASTUnit.rootNode.attribute("usage").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Extension extends"){
    runTest( "ASTTests/Extension/Extension.raml", project => {

      var expectedValue = "/Extension/api.raml)"
      var actualValue = project.rootASTUnit.rootNode.attribute("extends").get.value
      if (actualValue.toString().endsWith(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }
}