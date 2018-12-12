package org.mulesoft.high.level.test.RAML10.AST

import org.mulesoft.high.level.test.RAML10.RAML10ASTTest

class FileTypeDeclarationAnnotation extends RAML10ASTTest {

  test("FileTypeDeclarationAnnotation fileTypes"){
    runTest( "ASTTests/FileTypeDeclaration/fileTypeDeclarationAnnotation.raml", project => {

      var expectedValue = 3
      var length = project.rootASTUnit.rootNode.elements("annotationTypes").head.attributes("fileTypes").length
      if (length == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }

  test("FileTypeDeclarationAnnotation minLength") {
    runTest("ASTTests/FileTypeDeclaration/fileTypeDeclarationAnnotation.raml", project => {

      var expectedValue = 100
      var actualValue = project.rootASTUnit.rootNode.elements("annotationTypes").head.attribute("minLength").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("FileTypeDeclarationAnnotation maxLength") {
    runTest("ASTTests/FileTypeDeclaration/fileTypeDeclarationAnnotation.raml", project => {

      var expectedValue = 1000
      var actualValue = project.rootASTUnit.rootNode.elements("annotationTypes").head.attribute("maxLength").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }
}
