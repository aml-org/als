package org.mulesoft.high.level.test.OAS20.AST

import org.mulesoft.high.level.test.OAS20.OAS20ASTTest

class ScopeObjectJson extends OAS20ASTTest{

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

  test("ScopeObjectJson name"){
    runTest( "ASTTests/SecurityDefinitionObject/SecurityDefinitionObject.json", project => {
      var expectedValue = "user"
      var actualValue = project.rootASTUnit.rootNode.elements("securityDefinitions").head.elements("scopes").head.elements("scopes").head.attribute("name").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ScopeObjectJson description"){
    runTest( "ASTTests/SecurityDefinitionObject/SecurityDefinitionObject.json", project => {
      var expectedValue = "Grants read/write access to profile info only."
      var actualValue = project.rootASTUnit.rootNode.elements("securityDefinitions").head.elements("scopes").head.elements("scopes").head.attribute("description").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }
}