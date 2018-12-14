package org.mulesoft.high.level.test.RAML10.AST

import org.mulesoft.high.level.test.RAML10.RAML10ASTTest

class ArrayTypeDeclarationProperty extends RAML10ASTTest {

  test("ArrayTypeDeclarationProperty items") {
    runTest("ASTTests/ArrayTypeDeclaration/arrayTypeDeclarationProperty.raml", project => {

      var expectedValue = 1
      var actualValue = project.rootASTUnit.rootNode.elements("types").head.elements("properties").head.elements("items").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ArrayTypeDeclarationProperty uniqueItems") {
    runTest("ASTTests/ArrayTypeDeclaration/arrayTypeDeclarationProperty.raml", project => {

      var expectedValue = false
      var actualValue = project.rootASTUnit.rootNode.elements("types").head.elements("properties").head.attribute("uniqueItems").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ArrayTypeDeclarationProperty minItems") {
    runTest("ASTTests/ArrayTypeDeclaration/arrayTypeDeclarationProperty.raml", project => {

      var expectedValue = 2
      var actualValue = project.rootASTUnit.rootNode.elements("types").head.elements("properties").head.attribute("minItems").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ArrayTypeDeclarationProperty maxItems"){
    runTest( "ASTTests/ArrayTypeDeclaration/arrayTypeDeclarationProperty.raml", project => {

      var expectedValue = 4
      var length = project.rootASTUnit.rootNode.elements("types").head.elements("properties").head.attribute("maxItems").get.value
      if (length == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }
}