package org.mulesoft.high.level.test.RAML08.AST

import org.mulesoft.high.level.test.RAML08.RAML08ASTTest

class Parameter extends RAML08ASTTest{
  test("Parameter name") {
    runTest("ASTTests/Parameter/api.raml", project => {

      var expectedValue = "param"
      var actualValue = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("queryParameters").head.attribute("name").get.value.get
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Parameter displayName") {
    runTest("ASTTests/Parameter/api.raml", project => {

      var expectedValue = "parameter1"
      var actualValue = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("queryParameters").head.attribute("displayName").get.value.get
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Parameter description") {
    runTest("ASTTests/Parameter/api.raml", project => {

      var expectedValue = "parameter description"
      var actualValue = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("queryParameters").head.attribute("description").get.value.get
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }
/*

  test("Parameter default") {
    runTest("ASTTests/Parameter/api.raml", project => {

      var expectedValue = "abc"
      var actualValue = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("queryParameters").head.attribute("default").get.value.get
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }
*/

  test("Parameter required") {
    runTest("ASTTests/Parameter/api.raml", project => {

      var expectedValue = true
      var actualValue = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("queryParameters").head.attribute("required").get.value.get
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Parameter example") {
    runTest("ASTTests/Parameter/api.raml", project => {

      var expectedValue = "cba"
      var actualValue = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("queryParameters").head.attribute("example").get.value.get
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Parameter pattern") {
    runTest("ASTTests/Parameter/api.raml", project => {

      var expectedValue = "^*$"
      var actualValue = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("queryParameters").head.attribute("pattern").get.value.get
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Parameter minLength") {
    runTest("ASTTests/Parameter/api.raml", project => {

      var expectedValue = 2
      var actualValue = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("queryParameters").head.attribute("minLength").get.value.get
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Parameter maxLength") {
    runTest("ASTTests/Parameter/api.raml", project => {

      var expectedValue = 6
      var actualValue = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("queryParameters").head.attribute("maxLength").get.value.get
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Parameter enum") {
    runTest("ASTTests/Parameter/api.raml", project => {

      var expectedValue = 2
      var actualValue = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("queryParameters").head.attributes("enum").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }
}
