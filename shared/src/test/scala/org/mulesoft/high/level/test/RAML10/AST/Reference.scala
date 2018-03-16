package org.mulesoft.high.level.test.RAML10.AST

import org.mulesoft.high.level.test.RAML10.RAML10ASTTest

class Reference extends RAML10ASTTest {

  test("Reference name") {
    runTest("ASTTests/Reference/Reference.raml", project => {

      var expectedValue = "abc"
      var length = project.rootASTUnit.rootNode.elements("resources").head.elements("annotations").head.attribute("name").get.value
      if (length == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }
}
