package org.mulesoft.high.level.test.RAML10.AST

import org.mulesoft.high.level.test.RAML10.RAML10ASTTest

class FileTypeDeclarationRoot extends RAML10ASTTest {

  test("FileTypeDeclarationRoot fileTypes"){
    runTest( "ASTTests/FileTypeDeclaration/fileTypeDeclarationRoot.raml", project => {

      var expectedValue = 3
      var length = project.rootASTUnit.rootNode.elements("types").head.attributes("fileTypes").length
      if (length == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }

  test("FileTypeDeclarationRoot minLength") {
    runTest("ASTTests/FileTypeDeclaration/fileTypeDeclarationRoot.raml", project => {

      var expectedValue = 100
      var actualValue = project.rootASTUnit.rootNode.elements("types").head.attribute("minLength").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("FileTypeDeclarationRoot maxLength") {
    runTest("ASTTests/FileTypeDeclaration/fileTypeDeclarationRoot.raml", project => {

      var expectedValue = 1000
      var actualValue = project.rootASTUnit.rootNode.elements("types").head.attribute("maxLength").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }
}
