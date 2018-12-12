package org.mulesoft.high.level.test.RAML10.AST

import org.mulesoft.high.level.test.RAML10.RAML10ASTTest

class ArrayTypeDeclarationParameter extends RAML10ASTTest {

  test("ArrayTypeDeclarationParameter items") {
    runTest("ASTTests/ArrayTypeDeclaration/arrayTypeDeclarationParameter.raml", project => {

      var expectedValue = 1
      var actualValue = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("queryParameters").head.elements("items").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ArrayTypeDeclarationParameter uniqueItems") {
    runTest("ASTTests/ArrayTypeDeclaration/arrayTypeDeclarationParameter.raml", project => {

      var expectedValue = false
      var actualValue = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("headers").head.attribute("uniqueItems").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ArrayTypeDeclarationParameter minItems") {
    runTest("ASTTests/ArrayTypeDeclaration/arrayTypeDeclarationParameter.raml", project => {

      var expectedValue = 2
      var actualValue = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("headers").head.attribute("minItems").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ArrayTypeDeclarationParameter maxItems"){
    runTest( "ASTTests/ArrayTypeDeclaration/arrayTypeDeclarationParameter.raml", project => {

      var expectedValue = 4
      var length = project.rootASTUnit.rootNode.elements("resources").head.elements("uriParameters").head.attribute("maxItems").get.value
      if (length == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }
}