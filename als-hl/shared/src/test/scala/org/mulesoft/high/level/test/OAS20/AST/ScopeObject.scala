package org.mulesoft.high.level.test.OAS20.AST

import org.mulesoft.high.level.test.OAS20.OAS20ASTTest

class ScopeObject extends OAS20ASTTest{

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

  test("ScopeObject name"){
    runTest( "ASTTests/SecurityDefinitionObject/SecurityDefinitionObject.yml", project => {
      var expectedValue = "user"
      var actualValue = project.rootASTUnit.rootNode.elements("securityDefinitions").head.elements("scopes").head.elements("scopes").head.attribute("name").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ScopeObject description"){
    runTest( "ASTTests/SecurityDefinitionObject/SecurityDefinitionObject.yml", project => {
      var expectedValue = "Grants read/write access to profile info only."
      var actualValue = project.rootASTUnit.rootNode.elements("securityDefinitions").head.elements("scopes").head.elements("scopes").head.attribute("description").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }
}