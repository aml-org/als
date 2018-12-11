package org.mulesoft.high.level.test.RAML10.AST

import org.mulesoft.high.level.test.RAML10.RAML10ASTTest

class DateTimeTypeDeclaration extends RAML10ASTTest {

  test("DateTimeTypeDeclaration format") {
    runTest("ASTTests/DateTimeTypeDeclaration/api.raml", project => {

      var expectedValue = "rfc3339"
      var actualValue = project.rootASTUnit.rootNode.elements("types").head.attribute("format").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("DateTimeTypeDeclarationAnnotation format") {
    runTest("ASTTests/DateTimeTypeDeclaration/api.raml", project => {

      var expectedValue = "rfc3339"
      var actualValue = project.rootASTUnit.rootNode.elements("annotationTypes").head.attribute("format").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("DateTimeTypeDeclarationBody format") {
    runTest("ASTTests/DateTimeTypeDeclaration/api.raml", project => {

      var expectedValue = "rfc3339"
      var actualValue = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("body").head.attribute("format").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("DateTimeTypeDeclarationHeader format") {
    runTest("ASTTests/DateTimeTypeDeclaration/api.raml", project => {

      var expectedValue = "rfc3339"
      var actualValue = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("headers").head.attribute("format").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("DateTimeTypeDeclarationUri format") {
    runTest("ASTTests/DateTimeTypeDeclaration/api.raml", project => {

      var expectedValue = "rfc3339"
      var actualValue = project.rootASTUnit.rootNode.elements("resources").head.elements("uriParameters").head.attribute("format").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("DateTimeTypeDeclarationProperty format") {
    runTest("ASTTests/DateTimeTypeDeclaration/apiProperty.raml", project => {

      var expectedValue = "rfc3339"
      var actualValue = project.rootASTUnit.rootNode.elements("types").head.elements("properties").head.attribute("format").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }
}
