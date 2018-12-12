package org.mulesoft.high.level.test.RAML10.AST

import org.mulesoft.high.level.test.RAML10.RAML10ASTTest

class BooleanTypeDeclaration extends RAML10ASTTest {

  test("BooleanTypeDeclaration enum") {
    runTest("ASTTests/BooleanTypeDeclaration/api.raml", project => {

      var expectedValue = 2
      var actualValue = project.rootASTUnit.rootNode.elements("types").head.attributes("enum").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("BooleanTypeDeclarationAnnotation enum") {
    runTest("ASTTests/BooleanTypeDeclaration/api.raml", project => {

      var expectedValue = 2
      var actualValue = project.rootASTUnit.rootNode.elements("annotationTypes").head.attributes("enum").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("BooleanTypeDeclarationBody enum") {
    runTest("ASTTests/BooleanTypeDeclaration/api.raml", project => {

      var expectedValue = 2
      var actualValue = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("body").head.attributes("enum").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("BooleanTypeDeclarationHeader enum") {
    runTest("ASTTests/BooleanTypeDeclaration/api.raml", project => {

      var expectedValue = 2
      var actualValue = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("headers").head.attributes("enum").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("BooleanTypeDeclarationUri enum") {
    runTest("ASTTests/BooleanTypeDeclaration/api.raml", project => {

      var expectedValue = 2
      var actualValue = project.rootASTUnit.rootNode.elements("resources").head.elements("uriParameters").head.attributes("enum").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("BooleanTypeDeclarationProperty enum") {
    runTest("ASTTests/BooleanTypeDeclaration/apiProperty.raml", project => {

      var expectedValue = 2
      var actualValue = project.rootASTUnit.rootNode.elements("types").head.elements("properties").head.attributes("enum").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }
}

