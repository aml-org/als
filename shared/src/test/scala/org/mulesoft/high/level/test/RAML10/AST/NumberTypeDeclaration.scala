package org.mulesoft.high.level.test.RAML10.AST

import org.mulesoft.high.level.test.RAML10.RAML10ASTTest

class NumberTypeDeclaration extends RAML10ASTTest {

  test("NumberTypeDeclaration minimum") {
    runTest("ASTTests/NumberTypeDeclaration/numberTypeDeclarationRoot.raml", project => {

      var expectedValue = "2"
      var actualValue = project.rootASTUnit.rootNode.elements("types").head.attribute("minimum").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("NumberTypeDeclaration maximum") {
    runTest("ASTTests/NumberTypeDeclaration/numberTypeDeclarationRoot.raml", project => {

      var expectedValue = "6"
      var actualValue = project.rootASTUnit.rootNode.elements("types").head.attribute("maximum").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("NumberTypeDeclaration format") {
    runTest("ASTTests/NumberTypeDeclaration/numberTypeDeclarationRoot.raml", project => {

      var expectedValue = "int32"
      var actualValue = project.rootASTUnit.rootNode.elements("types").head.attribute("format").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("NumberTypeDeclaration multipleOf"){
    runTest( "ASTTests/NumberTypeDeclaration/numberTypeDeclarationRoot.raml", project => {

      var expectedValue = "2"
      var length = project.rootASTUnit.rootNode.elements("types").head.attribute("multipleOf").get.value
      if (length == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }

  test("NumberTypeDeclaration enum"){
    runTest( "ASTTests/NumberTypeDeclaration/numberTypeDeclarationRoot.raml", project => {
      var expectedValue = 2
      var actualValue = project.rootASTUnit.rootNode.elements("types").head.attributes("enum").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }
}
