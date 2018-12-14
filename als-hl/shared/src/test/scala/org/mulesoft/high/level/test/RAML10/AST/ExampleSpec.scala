package org.mulesoft.high.level.test.RAML10.AST

import org.mulesoft.high.level.test.RAML10.RAML10ASTTest

class ExampleSpec extends RAML10ASTTest {
  test("ExampleSpec value"){
    runTest( "ASTTests/ExampleSpec/ExampleSpec.raml", project => {

      var expectedValue = 1
      var actualValue = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("queryString").head.elements("examples").head.attributes("value").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ExampleSpec strict"){
    runTest( "ASTTests/ExampleSpec/ExampleSpec.raml", project => {

      var expectedValue = true
      var actualValue = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("queryString").head.elements("examples").head.attribute("strict").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ExampleSpec name"){
    runTest( "ASTTests/ExampleSpec/ExampleSpec.raml", project => {

      var expectedValue = "first"
      var actualValue = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("queryString").head.elements("examples").head.attribute("name").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ExampleSpec displayName"){
    runTest( "ASTTests/ExampleSpec/ExampleSpec.raml", project => {

      var expectedValue = "abc"
      var actualValue = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("queryString").head.elements("examples").head.attribute("displayName").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ExampleSpec description"){
    runTest( "ASTTests/ExampleSpec/ExampleSpec.raml", project => {

      var expectedValue = "abc"
      var actualValue = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("queryString").head.elements("examples").head.attribute("description").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }
}
