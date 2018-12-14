package org.mulesoft.high.level.test.OAS20.AST

import org.mulesoft.high.level.test.OAS20.OAS20ASTTest

class OAuth2Json extends OAS20ASTTest{

  test("ScopeObjectJson flow"){
    runTest( "ASTTests/SecurityDefinitionObject/SecurityDefinitionObject.json", project => {
      var expectedValue = "implicit"
      var actualValue = project.rootASTUnit.rootNode.elements("securityDefinitions").head.attribute("flow").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ScopeObjectJson authorizationUrl"){
    runTest( "ASTTests/SecurityDefinitionObject/SecurityDefinitionObject.json", project => {
      var expectedValue = "http://petstore.swagger.io/oauth/dialog"
      var actualValue = project.rootASTUnit.rootNode.elements("securityDefinitions").head.attribute("authorizationUrl").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ScopeObjectJson tokenUrl"){
    runTest( "ASTTests/SecurityDefinitionObject/SecurityDefinitionObject.json", project => {
      var expectedValue = "http://petstore.swagger.io/oauth/dialog2"
      var actualValue = project.rootASTUnit.rootNode.elements("securityDefinitions").head.attribute("tokenUrl").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ScopesObjectJson scopes"){
    runTest( "ASTTests/SecurityDefinitionObject/SecurityDefinitionObject.json", project => {
      var expectedValue = 1
      var actualValue = project.rootASTUnit.rootNode.elements("securityDefinitions").head.elements("scopes").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }
}
