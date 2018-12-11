package org.mulesoft.high.level.test.RAML10.AST

import org.mulesoft.high.level.test.RAML10.RAML10ASTTest

class FileTypeDeclarationBody extends RAML10ASTTest {

  test("FileTypeDeclarationBody fileTypes"){
    runTest( "ASTTests/FileTypeDeclaration/fileTypeDeclarationBody.raml", project => {

      var expectedValue = 3
      var length = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("body").head.attributes("fileTypes").length
      if (length == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }

  test("FileTypeDeclarationBody minLength") {
    runTest("ASTTests/FileTypeDeclaration/fileTypeDeclarationBody.raml", project => {

      var expectedValue = 100
      var actualValue = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("body").head.attribute("minLength").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("FileTypeDeclarationBody maxLength") {
    runTest("ASTTests/FileTypeDeclaration/fileTypeDeclarationBody.raml", project => {

      var expectedValue = 1000
      var actualValue = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("body").head.attribute("maxLength").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }
}
