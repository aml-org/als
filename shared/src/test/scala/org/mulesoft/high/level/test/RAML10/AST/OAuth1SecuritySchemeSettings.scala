package org.mulesoft.high.level.test.RAML10.AST

import org.mulesoft.high.level.test.RAML10.RAML10ASTTest

class OAuth1SecuritySchemeSettings extends RAML10ASTTest {

  test("OAuth1SecuritySchemeSettings authorizationUri") {
    runTest("ASTTests/OAuth1SecuritySchemeSettings/OAuth1SecuritySchemeSettings.raml", project => {

      var expectedValue = "uri"
      var length = project.rootASTUnit.rootNode.elements("securitySchemes").head.elements("settings").head.attribute("authorizationUri").get.value
      if (length == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }

  test("OAuth1SecuritySchemeSettings requestTokenUri") {
    runTest("ASTTests/OAuth1SecuritySchemeSettings/OAuth1SecuritySchemeSettings.raml", project => {

      var expectedValue = "uri"
      var length = project.rootASTUnit.rootNode.elements("securitySchemes").head.elements("settings").head.attribute("requestTokenUri").get.value
      if (length == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }

  test("OAuth1SecuritySchemeSettings tokenCredentialsUri") {
    runTest("ASTTests/OAuth1SecuritySchemeSettings/OAuth1SecuritySchemeSettings.raml", project => {

      var expectedValue = "uri"
      var length = project.rootASTUnit.rootNode.elements("securitySchemes").head.elements("settings").head.attribute("tokenCredentialsUri").get.value
      if (length == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }

  test("OAuth1SecuritySchemeSettings signatures") {
    runTest("ASTTests/OAuth1SecuritySchemeSettings/OAuth1SecuritySchemeSettings.raml", project => {

      var expectedValue = 3
      var length = project.rootASTUnit.rootNode.elements("securitySchemes").head.elements("settings").head.attributes("signatures").length
      if (length == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }
}

