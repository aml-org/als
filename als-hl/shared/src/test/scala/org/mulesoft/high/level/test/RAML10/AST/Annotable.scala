package org.mulesoft.high.level.test.RAML10.AST

import org.mulesoft.high.level.test.RAML10.RAML10ASTTest

class Annotable extends RAML10ASTTest {

  test("Annotable annotations") {
    runTest("ASTTests/Annotable/Annotable.raml", project => {

      var expectedValue = 3
      var length = project.rootASTUnit.rootNode.elements("resources").head.elements("annotations").length
      if (length == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }
}
