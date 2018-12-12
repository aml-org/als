package org.mulesoft.high.level.test.RAML08.AST

import org.mulesoft.high.level.test.RAML08.RAML08ASTTest

class GlobalSchema extends RAML08ASTTest{
  test("GlobalSchema key") {
    runTest("ASTTests/Api/api.raml", project => {

      var expectedValue = "inputSchema"
      var actualValue = project.rootASTUnit.rootNode.elements("schemas").head.attribute("key").get.value.get
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }
}
