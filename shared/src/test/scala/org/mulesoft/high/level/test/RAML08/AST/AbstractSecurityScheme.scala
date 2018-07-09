package org.mulesoft.high.level.test.RAML08.AST

import org.mulesoft.high.level.test.RAML08.RAML08ASTTest

class AbstractSecurityScheme extends RAML08ASTTest {

  test("AbstractSecurityScheme name") {
    runTest("ASTTests/SecurityScheme/abstractSecurityScheme.raml", project => {

      var expectedValue = "oauth"
      project.rootASTUnit.rootNode.elements("securitySchemes").head.attribute("name") match {
        case Some(a) => a.value match {
          case Some("oauth") => succeed
          case _ => fail(s"Expected value: $expectedValue, actual: ${a.value}")
        }
        case _ => fail("'name' attribute not found")
      }
    })
  }

  test("AbstractSecurityScheme type") {
    runTest("ASTTests/SecurityScheme/abstractSecurityScheme.raml", project => {

      var expectedValue = "OAuth 2.0"
      project.rootASTUnit.rootNode.elements("securitySchemes").head.attribute("type") match {
        case Some(a) => a.value match {
          case Some("OAuth 2.0") => succeed
          case _ => fail(s"Expected value: $expectedValue, actual: ${a.value}")
        }
        case _ => fail("'type' attribute not found")
      }
    })
  }

  test("AbstractSecurityScheme description") {
    runTest("ASTTests/SecurityScheme/abstractSecurityScheme.raml", project => {

      var expectedValue = "oauth2"
      project.rootASTUnit.rootNode.elements("securitySchemes").head.attribute("description") match {
        case Some(a) => a.value match {
          case Some("oauth2") => succeed
          case _ => fail(s"Expected value: $expectedValue, actual: ${a.value}")
        }
        case _ => fail("'description' attribute not found")
      }
    })
  }

  test("AbstractSecurityScheme describedBy") {
    runTest("ASTTests/SecurityScheme/abstractSecurityScheme.raml", project => {

      var expectedValue = 1
      var length = project.rootASTUnit.rootNode.elements("securitySchemes").head.elements("describedBy").length
      if (length == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }

  test("AbstractSecurityScheme settings") {
    runTest("ASTTests/SecurityScheme/abstractSecurityScheme.raml", project => {

      var expectedValue = 1
      var length = project.rootASTUnit.rootNode.elements("securitySchemes").head.elements("settings").length
      if (length == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }
}
