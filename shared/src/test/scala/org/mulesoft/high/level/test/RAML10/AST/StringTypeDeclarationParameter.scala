package org.mulesoft.high.level.test.RAML10.AST

import org.mulesoft.high.level.test.RAML10.RAML10ASTTest

class StringTypeDeclarationParameter  extends RAML10ASTTest {

  test("StringTypeDeclarationParameter pattern"){
    runTest( "ASTTests/StringTypeDeclaration/stringTypeDeclarationParameter.raml", project => {

      var expectedValue = 1
      var length = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("queryParameters").head.attributes("pattern").length
      if (length == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }

  test("StringTypeDeclarationParameter minLength") {
    runTest("ASTTests/StringTypeDeclaration/stringTypeDeclarationParameter.raml", project => {

      var expectedValue = 2
      var actualValue = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("headers").head.attribute("minLength").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("StringTypeDeclarationParameter maxLength") {
    runTest("ASTTests/StringTypeDeclaration/stringTypeDeclarationParameter.raml", project => {

      var expectedValue = 6
      var actualValue = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("headers").head.attribute("maxLength").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("StringTypeDeclarationParameter enum"){
    runTest( "ASTTests/StringTypeDeclaration/stringTypeDeclarationParameter.raml", project => {

      var expectedValue = 1
      var length = project.rootASTUnit.rootNode.elements("resources").head.elements("uriParameters").head.attributes("enum").length
      if (length == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }
}
