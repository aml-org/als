package org.mulesoft.high.level.test.RAML10.AST

import org.mulesoft.high.level.test.RAML10.RAML10ASTTest

class Library  extends RAML10ASTTest {

  test("Library usage"){
    runTest( "ASTTests/Library/Library.raml", project => {

      var expectedValue = "usage"
      var actualValue = project.rootASTUnit.rootNode.attribute("usage").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }
}
