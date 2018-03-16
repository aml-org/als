package org.mulesoft.high.level.test.RAML10.AST

import org.mulesoft.high.level.test.RAML10.RAML10ASTTest

class NumberTypeDeclarationBody extends RAML10ASTTest {

  test("NumberTypeDeclarationBody minimum") {
    runTest("ASTTests/NumberTypeDeclaration/numberTypeDeclarationBody.raml", project => {

      var expectedValue = "2"
      var actualValue = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("body").head.attribute("minimum").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("NumberTypeDeclarationBody maximum") {
    runTest("ASTTests/NumberTypeDeclaration/numberTypeDeclarationBody.raml", project => {

      var expectedValue = "6"
      var actualValue = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("body").head.attribute("maximum").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("NumberTypeDeclarationBody format") {
    runTest("ASTTests/NumberTypeDeclaration/numberTypeDeclarationBody.raml", project => {

      var expectedValue = "int32"
      var actualValue = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("body").head.attribute("format").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("NumberTypeDeclarationBody multipleOf"){
    runTest( "ASTTests/NumberTypeDeclaration/numberTypeDeclarationBody.raml", project => {

      var expectedValue = 2
      var length = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("body").head.attribute("multipleOf").get.value
      if (length == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }

  test("NumberTypeDeclarationBody enum"){
    runTest( "ASTTests/NumberTypeDeclaration/numberTypeDeclarationBody.raml", project => {
      var expectedValue = 2
      var actualValue = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("body").head.attributes("enum").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }
}
