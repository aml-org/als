package org.mulesoft.high.level.test.RAML10.AST

import org.mulesoft.high.level.test.RAML10.RAML10ASTTest

class StringTypeDeclaration extends RAML10ASTTest {

  test("StringTypeDeclaration pattern"){
    runTest( "ASTTests/StringTypeDeclaration/stringTypeDeclarationRoot.raml", project => {

      var expectedValue = 1
      var length = project.rootASTUnit.rootNode.elements("types").head.attributes("pattern").length
      if (length == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }

  test("StringTypeDeclaration minLength") {
    runTest("ASTTests/StringTypeDeclaration/stringTypeDeclarationRoot.raml", project => {

      var expectedValue = 2
      var actualValue = project.rootASTUnit.rootNode.elements("types").head.attribute("minLength").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("StringTypeDeclaration maxLength") {
    runTest("ASTTests/StringTypeDeclaration/stringTypeDeclarationRoot.raml", project => {

      var expectedValue = 6
      var actualValue = project.rootASTUnit.rootNode.elements("types").head.attribute("maxLength").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("StringTypeDeclaration enum"){
    runTest( "ASTTests/StringTypeDeclaration/stringTypeDeclarationRoot.raml", project => {

      var expectedValue = 1
      var length = project.rootASTUnit.rootNode.elements("types").head.attributes("enum").length
      if (length == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }
}
