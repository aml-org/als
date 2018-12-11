package org.mulesoft.high.level.test.RAML10.AST

import org.mulesoft.high.level.test.RAML10.RAML10ASTTest

class ObjectTypeDeclarationProperty extends RAML10ASTTest {

  test("ObjectTypeDeclarationProperty properties"){
    runTest( "ASTTests/ObjectTypeDeclaration/objectTypeDeclarationProperty.raml", project => {

      var expectedValue = 2
      var length = project.rootASTUnit.rootNode.elements("types").head.elements("properties").head.elements("properties").length
      if (length == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }

  test("ObjectTypeDeclarationProperty minProperties") {
    runTest("ASTTests/ObjectTypeDeclaration/objectTypeDeclarationProperty.raml", project => {

      var expectedValue = 1
      var actualValue = project.rootASTUnit.rootNode.elements("types").head.elements("properties").head.attribute("minProperties").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ObjectTypeDeclarationProperty maxProperties") {
    runTest("ASTTests/ObjectTypeDeclaration/objectTypeDeclarationProperty.raml", project => {

      var expectedValue = 2
      var actualValue = project.rootASTUnit.rootNode.elements("types").head.elements("properties").head.attribute("maxProperties").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ObjectTypeDeclarationProperty additionalProperties") {
    runTest("ASTTests/ObjectTypeDeclaration/objectTypeDeclarationProperty.raml", project => {

      var expectedValue = false
      var actualValue = project.rootASTUnit.rootNode.elements("types").head.elements("properties").head.attribute("additionalProperties").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }
}
