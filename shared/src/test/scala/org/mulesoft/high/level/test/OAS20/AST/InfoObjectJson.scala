package org.mulesoft.high.level.test.OAS20.AST

import org.mulesoft.high.level.test.OAS20.OAS20ASTTest

class InfoObjectJson extends OAS20ASTTest{

  test("InfoObjectJson title"){
    runTest( "ASTTests/InfoObject/InfoObject.json", project => {
      var expectedValue = "Swagger"
      var actualValue = project.rootASTUnit.rootNode.elements("info").head.attribute("title").get.value
      if (actualValue == Some(expectedValue))
      succeed
      else
      fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("InfoObjectJson version"){
    runTest( "ASTTests/InfoObject/InfoObject.json", project => {
      var expectedValue = "1.0.9-abcd"
      var actualValue = project.rootASTUnit.rootNode.elements("info").head.attribute("version").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("InfoObjectJson description"){
    runTest( "ASTTests/InfoObject/InfoObject.json", project => {
      var expectedValue = "A sample specification"
      var actualValue = project.rootASTUnit.rootNode.elements("info").head.attribute("description").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("InfoObjectJson termsOfService"){
    runTest( "ASTTests/InfoObject/InfoObject.json", project => {
      var expectedValue = "http://swagger.io/terms/"
      var actualValue = project.rootASTUnit.rootNode.elements("info").head.attribute("termsOfService").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("InfoObjectJson license"){
    runTest( "ASTTests/InfoObject/InfoObject.json", project => {
      var expectedValue = 1
      var actualValue = project.rootASTUnit.rootNode.elements("info").head.elements("license").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("InfoObjectJson contact"){
    runTest( "ASTTests/InfoObject/InfoObject.json", project => {
      var expectedValue = 1
      var actualValue = project.rootASTUnit.rootNode.elements("info").head.elements("contact").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }
}
