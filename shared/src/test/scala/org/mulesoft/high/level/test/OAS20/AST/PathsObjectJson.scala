package org.mulesoft.high.level.test.OAS20.AST

import org.mulesoft.high.level.test.OAS20.OAS20ASTTest

class PathsObjectJson extends OAS20ASTTest{

  test("PathsObjectJson paths"){
    runTest( "ASTTests/PathObject/PathObject.json", project => {
      var expectedValue = 1
      var actualValue = project.rootASTUnit.rootNode.elements("paths").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }
}