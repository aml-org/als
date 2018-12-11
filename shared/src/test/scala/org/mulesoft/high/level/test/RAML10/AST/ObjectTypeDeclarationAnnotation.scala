package org.mulesoft.high.level.test.RAML10.AST

import org.mulesoft.high.level.test.RAML10.RAML10ASTTest

class ObjectTypeDeclarationAnnotation extends RAML10ASTTest {

  test("ObjectTypeDeclaration properties"){
    runTest( "ASTTests/ObjectTypeDeclaration/objectTypeDeclarationAnnotation.raml", project => {

      var expectedValue = 2
      var length = project.rootASTUnit.rootNode.elements("annotationTypes").head.elements("properties").length
      if (length == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }

  test("ObjectTypeDeclaration minProperties") {
    runTest("ASTTests/ObjectTypeDeclaration/objectTypeDeclarationAnnotation.raml", project => {

      var expectedValue = 1
      var actualValue = project.rootASTUnit.rootNode.elements("annotationTypes").head.attribute("minProperties").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ObjectTypeDeclaration maxProperties") {
    runTest("ASTTests/ObjectTypeDeclaration/objectTypeDeclarationAnnotation.raml", project => {

      var expectedValue = 2
      var actualValue = project.rootASTUnit.rootNode.elements("annotationTypes").head.attribute("maxProperties").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ObjectTypeDeclaration additionalProperties") {
    runTest("ASTTests/ObjectTypeDeclaration/objectTypeDeclarationAnnotation.raml", project => {

      var expectedValue = false
      var actualValue = project.rootASTUnit.rootNode.elements("annotationTypes").head.attribute("additionalProperties").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }
}