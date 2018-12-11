package org.mulesoft.high.level.test.RAML10.AST

import org.mulesoft.high.level.test.RAML10.RAML10ASTTest


class TraitRef extends RAML10ASTTest {

  test("TraitRef trait") {
    runTest("ASTTests/TraitRef/TraitRef.raml", project => {

      var expectedValue = 1
      var length = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("is").head.elements("trait").length
      if (length == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }
}
