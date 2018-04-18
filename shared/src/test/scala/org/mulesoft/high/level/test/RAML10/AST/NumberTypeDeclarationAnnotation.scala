package org.mulesoft.high.level.test.RAML10.AST

import org.mulesoft.high.level.test.RAML10.RAML10ASTTest

class NumberTypeDeclarationAnnotation extends RAML10ASTTest {

  test("NumberTypeDeclarationAnnotation minimum") {
    runTest("ASTTests/NumberTypeDeclaration/numberTypeDeclarationAnnotation.raml", project => {

      var expectedValue = "2"
      var actualValue = project.rootASTUnit.rootNode.elements("annotationTypes").head.attribute("minimum").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("NumberTypeDeclarationAnnotation maximum") {
    runTest("ASTTests/NumberTypeDeclaration/numberTypeDeclarationAnnotation.raml", project => {

      var expectedValue = "6"
      var actualValue = project.rootASTUnit.rootNode.elements("annotationTypes").head.attribute("maximum").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("NumberTypeDeclarationAnnotation format") {
    runTest("ASTTests/NumberTypeDeclaration/numberTypeDeclarationAnnotation.raml", project => {

      var expectedValue = "int32"
      var actualValue = project.rootASTUnit.rootNode.elements("annotationTypes").head.attribute("format").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("NumberTypeDeclarationAnnotation multipleOf"){
    runTest( "ASTTests/NumberTypeDeclaration/numberTypeDeclarationAnnotation.raml", project => {

      var expectedValue = 2
      var length = project.rootASTUnit.rootNode.elements("annotationTypes").head.attribute("multipleOf").get.value.get
      if (length == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }

  test("NumberTypeDeclarationAnnotation enum"){
    runTest( "ASTTests/NumberTypeDeclaration/numberTypeDeclarationAnnotation.raml", project => {
      var expectedValue = 2
      var actualValue = project.rootASTUnit.rootNode.elements("annotationTypes").head.attributes("enum").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }
}
