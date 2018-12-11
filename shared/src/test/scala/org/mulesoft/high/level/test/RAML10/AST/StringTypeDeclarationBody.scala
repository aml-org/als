package org.mulesoft.high.level.test.RAML10.AST

import org.mulesoft.high.level.test.RAML10.RAML10ASTTest

class StringTypeDeclarationBody  extends RAML10ASTTest {

  test("StringTypeDeclarationBody pattern"){
    runTest( "ASTTests/StringTypeDeclaration/stringTypeDeclarationBody.raml", project => {

      var expectedValue = 1
      var length = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("body").head.attributes("pattern").length
      if (length == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }

  test("StringTypeDeclarationBody minLength") {
    runTest("ASTTests/StringTypeDeclaration/stringTypeDeclarationBody.raml", project => {

      var expectedValue = 2
      var actualValue = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("body").head.attribute("minLength").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("StringTypeDeclarationBody maxLength") {
    runTest("ASTTests/StringTypeDeclaration/stringTypeDeclarationBody.raml", project => {

      var expectedValue = 6
      var actualValue = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("body").head.attribute("maxLength").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("StringTypeDeclarationBody enum"){
    runTest( "ASTTests/StringTypeDeclaration/stringTypeDeclarationBody.raml", project => {

      var expectedValue = 1
      var length = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("body").head.attributes("enum").length
      if (length == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }
}

