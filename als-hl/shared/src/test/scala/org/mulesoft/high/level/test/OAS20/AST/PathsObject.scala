package org.mulesoft.high.level.test.OAS20.AST

import org.mulesoft.high.level.test.OAS20.OAS20ASTTest

class PathsObject extends OAS20ASTTest{

  test("PathsObject paths"){
    runTest( "ASTTests/PathObject/PathObject.yml", project => {
      var expectedValue = 1
      var actualValue = project.rootASTUnit.rootNode.elements("paths").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }
}