package org.mulesoft.high.level.test.RAML10.AST

import org.mulesoft.high.level.test.RAML10.RAML10ASTTest

class ArrayTypeDeclarationAnnotation extends RAML10ASTTest {

  test("ArrayTypeDeclarationAnnotation items") {
    runTest("ASTTests/ArrayTypeDeclaration/arrayTypeDeclarationAnnotation.raml", project => {

      var expectedValue = 1
      var actualValue = project.rootASTUnit.rootNode.elements("annotationTypes").head.elements("items").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ArrayTypeDeclarationAnnotation uniqueItems") {
    runTest("ASTTests/ArrayTypeDeclaration/arrayTypeDeclarationAnnotation.raml", project => {

      var expectedValue = false
      var actualValue = project.rootASTUnit.rootNode.elements("annotationTypes").head.attribute("uniqueItems").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ArrayTypeDeclarationAnnotation minItems") {
    runTest("ASTTests/ArrayTypeDeclaration/arrayTypeDeclarationAnnotation.raml", project => {

      var expectedValue = 2
      var actualValue = project.rootASTUnit.rootNode.elements("annotationTypes").head.attribute("minItems").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ArrayTypeDeclarationAnnotation maxItems"){
    runTest( "ASTTests/ArrayTypeDeclaration/arrayTypeDeclarationAnnotation.raml", project => {

      var expectedValue = 4
      var length = project.rootASTUnit.rootNode.elements("annotationTypes").head.attribute("maxItems").get.value
      if (length == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }
}
