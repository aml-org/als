package org.mulesoft.high.level.test.RAML10.AST

import org.mulesoft.high.level.test.RAML10.RAML10ASTTest

class ResourceTypeRef extends RAML10ASTTest {

  test("ResourceTypeRef resourceType") {
    runTest("ASTTests/ResourceType/api.raml", project => {

      var expectedValue = 1
      var length = project.rootASTUnit.rootNode.elements("resources").head.element("type").head.elements("resourceType").length
      if (length == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }
}