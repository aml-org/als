package org.mulesoft.high.level.test.RAML10.AST

import org.mulesoft.high.level.test.RAML10.RAML10ASTTest

class ArrayTypeDeclaration extends RAML10ASTTest {

  test("ArrayTypeDeclaration items") {
    runTest("ASTTests/ArrayTypeDeclaration/arrayTypeDeclarationRoot.raml", project => {

      var expectedValue = 1
      var actualValue = project.rootASTUnit.rootNode.elements("types").head.elements("items").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ArrayTypeDeclaration uniqueItems") {
    runTest("ASTTests/ArrayTypeDeclaration/arrayTypeDeclarationRoot.raml", project => {

      var expectedValue = false
      var actualValue = project.rootASTUnit.rootNode.elements("types").head.attribute("uniqueItems").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ArrayTypeDeclaration minItems") {
    runTest("ASTTests/ArrayTypeDeclaration/arrayTypeDeclarationRoot.raml", project => {

      var expectedValue = 2
      var actualValue = project.rootASTUnit.rootNode.elements("types").head.attribute("minItems").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ArrayTypeDeclaration maxItems"){
    runTest( "ASTTests/ArrayTypeDeclaration/arrayTypeDeclarationRoot.raml", project => {

      var expectedValue = 4
      var length = project.rootASTUnit.rootNode.elements("types").head.attribute("maxItems").get.value
      if (length == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }
}
