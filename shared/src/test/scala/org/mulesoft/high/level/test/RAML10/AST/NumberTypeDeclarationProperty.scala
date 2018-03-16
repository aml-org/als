package org.mulesoft.high.level.test.RAML10.AST

import org.mulesoft.high.level.test.RAML10.RAML10ASTTest

class NumberTypeDeclarationProperty extends RAML10ASTTest {

  test("NumberTypeDeclarationProperty minimum") {
    runTest("ASTTests/NumberTypeDeclaration/numberTypeDeclarationProperty.raml", project => {

      var expectedValue = "2"
      var actualValue = project.rootASTUnit.rootNode.elements("types").head.elements("properties").head.attribute("minimum").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("NumberTypeDeclarationProperty maximum") {
    runTest("ASTTests/NumberTypeDeclaration/numberTypeDeclarationProperty.raml", project => {

      var expectedValue = "6"
      var actualValue = project.rootASTUnit.rootNode.elements("types").head.elements("properties").head.attribute("maximum").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("NumberTypeDeclarationProperty format") {
    runTest("ASTTests/NumberTypeDeclaration/numberTypeDeclarationProperty.raml", project => {

      var expectedValue = "int32"
      var actualValue = project.rootASTUnit.rootNode.elements("types").head.elements("properties").head.attribute("format").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("NumberTypeDeclarationProperty multipleOf"){
    runTest( "ASTTests/NumberTypeDeclaration/numberTypeDeclarationProperty.raml", project => {

      var expectedValue = 2
      var length = project.rootASTUnit.rootNode.elements("types").head.elements("properties").head.attribute("multipleOf").get.value
      if (length == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }

  test("NumberTypeDeclarationProperty enum"){
    runTest( "ASTTests/NumberTypeDeclaration/numberTypeDeclarationProperty.raml", project => {
      var expectedValue = 2
      var actualValue = project.rootASTUnit.rootNode.elements("types").head.elements("properties").head.attributes("enum").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }
}
