package org.mulesoft.high.level.test.RAML10.AST

import org.mulesoft.high.level.test.RAML10.RAML10ASTTest

class StringTypeDeclarationProperty  extends RAML10ASTTest {

  test("StringTypeDeclarationProperty pattern"){
    runTest( "ASTTests/StringTypeDeclaration/stringTypeDeclarationProperty.raml", project => {

      var expectedValue = 1
      var length = project.rootASTUnit.rootNode.elements("types").head.elements("properties").head.attributes("pattern").length
      if (length == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }

  test("StringTypeDeclarationProperty minLength") {
    runTest("ASTTests/StringTypeDeclaration/stringTypeDeclarationProperty.raml", project => {

      var expectedValue = 2
      var actualValue = project.rootASTUnit.rootNode.elements("types").head.elements("properties").head.attribute("minLength").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("StringTypeDeclarationProperty maxLength") {
    runTest("ASTTests/StringTypeDeclaration/stringTypeDeclarationProperty.raml", project => {

      var expectedValue = 6
      var actualValue = project.rootASTUnit.rootNode.elements("types").head.elements("properties").head.attribute("maxLength").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("StringTypeDeclarationProperty enum"){
    runTest( "ASTTests/StringTypeDeclaration/stringTypeDeclarationProperty.raml", project => {

      var expectedValue = 1
      var length = project.rootASTUnit.rootNode.elements("types").head.elements("properties").head.attributes("enum").length
      if (length == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }
}
