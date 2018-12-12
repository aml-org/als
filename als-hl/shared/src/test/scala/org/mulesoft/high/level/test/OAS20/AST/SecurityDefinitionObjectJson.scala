package org.mulesoft.high.level.test.OAS20.AST

import org.mulesoft.high.level.test.OAS20.OAS20ASTTest

class SecurityDefinitionObjectJson extends OAS20ASTTest{

  test("SecurityDefinitionObjectJson name"){
    runTest( "ASTTests/SecurityDefinitionObject/SecurityDefinitionObject.json", project => {
      var expectedValue = "petstoreImplicit"
      var actualValue = project.rootASTUnit.rootNode.elements("securityDefinitions").head.attribute("name").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("SecurityDefinitionObjectJson type"){
    runTest( "ASTTests/SecurityDefinitionObject/SecurityDefinitionObject.json", project => {
      var expectedValue = "OAuth 2.0"
      var actualValue = project.rootASTUnit.rootNode.elements("securityDefinitions").head.attribute("type").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("SecurityDefinitionObjectJson description"){
    runTest( "ASTTests/SecurityDefinitionObject/SecurityDefinitionObject.json", project => {
      var expectedValue = "security"
      var actualValue = project.rootASTUnit.rootNode.elements("securityDefinitions").head.attribute("description").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }
}