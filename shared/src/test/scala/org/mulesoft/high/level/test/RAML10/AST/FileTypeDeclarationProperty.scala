package org.mulesoft.high.level.test.RAML10.AST

import org.mulesoft.high.level.test.RAML10.RAML10ASTTest

class FileTypeDeclarationProperty extends RAML10ASTTest {

  test("FileTypeDeclarationProperty fileTypes"){
    runTest( "ASTTests/FileTypeDeclaration/fileTypeDeclarationProperty.raml", project => {

      var expectedValue = 3
      var length = project.rootASTUnit.rootNode.elements("types").head.elements("properties").head.attributes("fileTypes").length
      if (length == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }

  test("FileTypeDeclarationProperty minLength") {
    runTest("ASTTests/FileTypeDeclaration/fileTypeDeclarationProperty.raml", project => {

      var expectedValue = 100
      var actualValue = project.rootASTUnit.rootNode.elements("types").head.elements("properties").head.attribute("minLength").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("FileTypeDeclarationProperty maxLength") {
    runTest("ASTTests/FileTypeDeclaration/fileTypeDeclarationProperty.raml", project => {

      var expectedValue = 1000
      var actualValue = project.rootASTUnit.rootNode.elements("types").head.elements("properties").head.attribute("maxLength").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }
}