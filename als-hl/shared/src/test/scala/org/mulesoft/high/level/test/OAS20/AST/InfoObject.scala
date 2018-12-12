package org.mulesoft.high.level.test.OAS20.AST

import org.mulesoft.high.level.test.OAS20.OAS20ASTTest

class InfoObject extends OAS20ASTTest{

  test("InfoObject title"){
    runTest( "ASTTests/InfoObject/InfoObject.yml", project => {
      var expectedValue = "Swagger"
      var actualValue = project.rootASTUnit.rootNode.elements("info").head.attribute("title").get.value
      if (actualValue == Some(expectedValue))
      succeed
      else
      fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("InfoObject version"){
    runTest( "ASTTests/InfoObject/InfoObject.yml", project => {
      var expectedValue = "1.0.9-abcd"
      var actualValue = project.rootASTUnit.rootNode.elements("info").head.attribute("version").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("InfoObject description"){
    runTest( "ASTTests/InfoObject/InfoObject.yml", project => {
      var expectedValue = "A sample specification"
      var actualValue = project.rootASTUnit.rootNode.elements("info").head.attribute("description").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("InfoObject termsOfService"){
    runTest( "ASTTests/InfoObject/InfoObject.yml", project => {
      var expectedValue = "http://swagger.io/terms/"
      var actualValue = project.rootASTUnit.rootNode.elements("info").head.attribute("termsOfService").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("InfoObject license"){
    runTest( "ASTTests/InfoObject/InfoObject.yml", project => {
      var expectedValue = 1
      var actualValue = project.rootASTUnit.rootNode.elements("info").head.elements("license").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("InfoObject contact"){
    runTest( "ASTTests/InfoObject/InfoObject.yml", project => {
      var expectedValue = 1
      var actualValue = project.rootASTUnit.rootNode.elements("info").head.elements("contact").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }
}
