package org.mulesoft.high.level.test.OAS20.AST

import org.mulesoft.high.level.test.OAS20.OAS20ASTTest

class OAuth2 extends OAS20ASTTest{

  test("ScopeObject flow"){
    runTest( "ASTTests/SecurityDefinitionObject/SecurityDefinitionObject.yml", project => {
      var expectedValue = "implicit"
      var actualValue = project.rootASTUnit.rootNode.elements("securityDefinitions").head.attribute("flow").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ScopeObject authorizationUrl"){
    runTest( "ASTTests/SecurityDefinitionObject/SecurityDefinitionObject.yml", project => {
      var expectedValue = "http://petstore.swagger.io/oauth/dialog"
      var actualValue = project.rootASTUnit.rootNode.elements("securityDefinitions").head.attribute("authorizationUrl").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ScopeObject tokenUrl"){
    runTest( "ASTTests/SecurityDefinitionObject/SecurityDefinitionObject.yml", project => {
      var expectedValue = "http://petstore.swagger.io/oauth/dialog2"
      var actualValue = project.rootASTUnit.rootNode.elements("securityDefinitions").head.attribute("tokenUrl").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ScopesObject scopes"){
    runTest( "ASTTests/SecurityDefinitionObject/SecurityDefinitionObject.yml", project => {
      var expectedValue = 1
      var actualValue = project.rootASTUnit.rootNode.elements("securityDefinitions").head.elements("scopes").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }
}
