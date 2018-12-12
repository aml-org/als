package org.mulesoft.high.level.test.RAML10.AST

import org.mulesoft.high.level.test.RAML10.RAML10ASTTest

class SecuritySchemePart extends RAML10ASTTest {

  test("SecuritySchemePart queryParameters") {
    runTest("ASTTests/SecurityScheme/securitySchemePart1.raml", project => {

      var expectedValue = 1
      var length = project.rootASTUnit.rootNode.elements("securitySchemes").head.elements("describedBy").head.elements("queryParameters").length
      if (length == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }

  test("SecuritySchemePart headers") {
    runTest("ASTTests/SecurityScheme/securitySchemePart1.raml", project => {

      var expectedValue = 1
      var length = project.rootASTUnit.rootNode.elements("securitySchemes").head.elements("describedBy").head.elements("headers").length
      if (length == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }

  test("SecuritySchemePart queryString") {
    runTest("ASTTests/SecurityScheme/securitySchemePart2.raml", project => {

      var expectedValue = 1
      var length = project.rootASTUnit.rootNode.elements("securitySchemes").head.elements("describedBy").head.elements("queryString").length
      if (length == expectedValue)
      succeed
      else
      fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }

  test("SecuritySchemePart responses") {
    runTest("ASTTests/SecurityScheme/securitySchemePart1.raml", project => {

      var expectedValue = 2
      var length = project.rootASTUnit.rootNode.elements("securitySchemes").head.elements("describedBy").head.elements("responses").length
      if (length == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }
}
