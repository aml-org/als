package org.mulesoft.high.level.test.OAS20.AST

import org.mulesoft.high.level.test.OAS20.OAS20ASTTest

class ApiKeyJson extends OAS20ASTTest{

  test("ApiKeyJson name"){
    runTest( "ASTTests/ApiKey/ApiKey.json", project => {
      var expectedValue = "api_key"
      var actualValue = project.rootASTUnit.rootNode.elements("securityDefinitions").head.attribute("name").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ApiKeyJson in"){
    runTest( "ASTTests/ApiKey/ApiKey.json", project => {
      var expectedValue = "query"
      var actualValue = project.rootASTUnit.rootNode.elements("securityDefinitions").head.attribute("in").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }
}
