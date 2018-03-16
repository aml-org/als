package org.mulesoft.high.level.test.RAML10.AST

import org.mulesoft.high.level.test.RAML10.RAML10ASTTest

class StringTypeDeclarationAnnotation  extends RAML10ASTTest {

  test("StringTypeDeclarationAnnotation pattern"){
    runTest( "ASTTests/StringTypeDeclaration/stringTypeDeclarationAnnotation.raml", project => {

      var expectedValue = 1
      var length = project.rootASTUnit.rootNode.elements("annotationTypes").head.attributes("pattern").length
      if (length == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }

  test("StringTypeDeclarationAnnotation minLength") {
    runTest("ASTTests/StringTypeDeclaration/stringTypeDeclarationAnnotation.raml", project => {

      var expectedValue = 2
      var actualValue = project.rootASTUnit.rootNode.elements("annotationTypes").head.attribute("minLength").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("StringTypeDeclarationAnnotation maxLength") {
    runTest("ASTTests/StringTypeDeclaration/stringTypeDeclarationAnnotation.raml", project => {

      var expectedValue = 6
      var actualValue = project.rootASTUnit.rootNode.elements("annotationTypes").head.attribute("maxLength").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("StringTypeDeclarationAnnotation enum"){
    runTest( "ASTTests/StringTypeDeclaration/stringTypeDeclarationAnnotation.raml", project => {

      var expectedValue = 1
      var length = project.rootASTUnit.rootNode.elements("annotationTypes").head.attributes("enum").length
      if (length == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }
}
