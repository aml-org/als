package org.mulesoft.high.level.test.RAML10.AST

import org.mulesoft.high.level.test.RAML10.RAML10ASTTest

class SecuritySchemeRef extends RAML10ASTTest {

  test("SecuritySchemeRef securityScheme") {
    runTest("ASTTests/SecuritySchemeRef/SecuritySchemeRef.raml", project => {

      var expectedValue = 1
      var length = project.rootASTUnit.rootNode.elements("resources").head.elements("securedBy").head.elements("securityScheme").length
      if (length == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }

  test("SecuritySchemeRef settings") {
    runTest("ASTTests/SecuritySchemeRef/SecuritySchemeRef.raml", project => {

      var expectedValue = 1
      var length = project.rootASTUnit.rootNode.elements("resources").head.elements("securedBy").head.elements("settings").length
      if (length == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }
}
