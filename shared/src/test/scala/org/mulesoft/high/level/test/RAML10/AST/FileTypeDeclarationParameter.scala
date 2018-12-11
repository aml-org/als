package org.mulesoft.high.level.test.RAML10.AST

import org.mulesoft.high.level.test.RAML10.RAML10ASTTest


class FileTypeDeclarationParameter extends RAML10ASTTest {

  test("FileTypeDeclarationParameter fileTypes"){
    runTest( "ASTTests/FileTypeDeclaration/fileTypeDeclarationParameter.raml", project => {

      var expectedValue = 3
      var length = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("queryParameters").head.attributes("fileTypes").length
      if (length == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }

  test("FileTypeDeclarationParameter minLength") {
    runTest("ASTTests/FileTypeDeclaration/fileTypeDeclarationParameter.raml", project => {

      var expectedValue = 100
      var actualValue = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("headers").head.attribute("minLength").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("FileTypeDeclarationParameter maxLength") {
    runTest("ASTTests/FileTypeDeclaration/fileTypeDeclarationParameter.raml", project => {

      var expectedValue = 1000
      var actualValue = project.rootASTUnit.rootNode.elements("resources").head.elements("uriParameters").head.attribute("maxLength").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }
}