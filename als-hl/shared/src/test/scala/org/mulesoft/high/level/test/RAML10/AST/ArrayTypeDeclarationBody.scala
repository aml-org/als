package org.mulesoft.high.level.test.RAML10.AST

import org.mulesoft.high.level.test.RAML10.RAML10ASTTest

class ArrayTypeDeclarationBody extends RAML10ASTTest {

  test("ArrayTypeDeclarationBody items") {
    runTest("ASTTests/ArrayTypeDeclaration/arrayTypeDeclarationBody.raml", project => {

      var expectedValue = 1
      var actualValue = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("body").head.elements("items").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ArrayTypeDeclarationBody uniqueItems") {
    runTest("ASTTests/ArrayTypeDeclaration/arrayTypeDeclarationBody.raml", project => {

      var expectedValue = false
      var actualValue = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("body").head.attribute("uniqueItems").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ArrayTypeDeclarationBody minItems") {
    runTest("ASTTests/ArrayTypeDeclaration/arrayTypeDeclarationBody.raml", project => {

      var expectedValue = 2
      var actualValue = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("body").head.attribute("minItems").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ArrayTypeDeclarationBody maxItems"){
    runTest( "ASTTests/ArrayTypeDeclaration/arrayTypeDeclarationBody.raml", project => {

      var expectedValue = 4
      var length = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("body").head.attribute("maxItems").get.value
      if (length == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }
}