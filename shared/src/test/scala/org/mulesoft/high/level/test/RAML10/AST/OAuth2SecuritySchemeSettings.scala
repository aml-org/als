package org.mulesoft.high.level.test.RAML10.AST

import org.mulesoft.high.level.test.RAML10.RAML10ASTTest

class OAuth2SecuritySchemeSettings  extends RAML10ASTTest {

  test("OAuth2SecuritySchemeSettings accessTokenUri") {
    runTest("ASTTests/OAuth2SecuritySchemeSettings/OAuth2SecuritySchemeSettings.raml", project => {

      var expectedValue = "uri"
      var length = project.rootASTUnit.rootNode.elements("securitySchemes").head.elements("settings").head.attribute("accessTokenUri").get.value
      if (length == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }

  test("OAuth2SecuritySchemeSettings authorizationUri") {
    runTest("ASTTests/OAuth2SecuritySchemeSettings/OAuth2SecuritySchemeSettings.raml", project => {

      var expectedValue = "uri"
      var length = project.rootASTUnit.rootNode.elements("securitySchemes").head.elements("settings").head.attribute("authorizationUri").get.value
      if (length == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }

  test("OAuth2SecuritySchemeSettings scopes") {
    runTest("ASTTests/OAuth2SecuritySchemeSettings/OAuth2SecuritySchemeSettings.raml", project => {

      var expectedValue = 1
      var length = project.rootASTUnit.rootNode.elements("securitySchemes").head.elements("settings").head.elements("scopes").length
      if (length == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }

  test("OAuth2SecuritySchemeSettings scope name") {
    runTest("ASTTests/OAuth2SecuritySchemeSettings/OAuth2SecuritySchemeSettings.raml", project => {

      var expectedValue = "ADMINISTRATOR"
      var actualValue = project.rootASTUnit.rootNode.elements("securitySchemes").head.elements("settings").head.elements("scopes").head.attribute("name").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("OAuth2SecuritySchemeSettings authorizationGrants") {
    runTest("ASTTests/OAuth2SecuritySchemeSettings/OAuth2SecuritySchemeSettings.raml", project => {

      var expectedValue = 2
      var length = project.rootASTUnit.rootNode.elements("securitySchemes").head.elements("settings").head.attributes("authorizationGrants").length
      if (length == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }
}
