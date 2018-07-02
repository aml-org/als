package org.mulesoft.high.level.test.RAML10.AST

import org.mulesoft.high.level.test.RAML10.RAML10ASTTest

class NumberTypeDeclarationParameter extends RAML10ASTTest {

  test("NumberTypeDeclarationParameter minimum") {
    runTest("ASTTests/NumberTypeDeclaration/numberTypeDeclarationParameter.raml", project => {

      var expectedValue = "2"
      var actualValue = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("headers").head.attribute("minimum").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("NumberTypeDeclarationParameter maximum") {
    runTest("ASTTests/NumberTypeDeclaration/numberTypeDeclarationParameter.raml", project => {

      var expectedValue = "6"
      var actualValue = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("headers").head.attribute("maximum").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("NumberTypeDeclarationParameter format") {
    runTest("ASTTests/NumberTypeDeclaration/numberTypeDeclarationParameter.raml", project => {

      var expectedValue = "int32"
      var actualValue = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("queryParameters").head.attribute("format").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("NumberTypeDeclarationParameter multipleOf"){
    runTest( "ASTTests/NumberTypeDeclaration/numberTypeDeclarationParameter.raml", project => {

      var expectedValue = "2"
      var length = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("queryParameters").head.attribute("multipleOf").get.value
      if (length == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }

  test("NumberTypeDeclarationParameter enum"){
    runTest( "ASTTests/NumberTypeDeclaration/numberTypeDeclarationParameter.raml", project => {
      var expectedValue = 2
      var actualValue = project.rootASTUnit.rootNode.elements("resources").head.elements("uriParameters").head.attributes("enum").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }
}
