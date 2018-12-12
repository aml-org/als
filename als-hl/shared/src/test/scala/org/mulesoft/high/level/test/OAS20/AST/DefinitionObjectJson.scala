package org.mulesoft.high.level.test.OAS20.AST

import org.mulesoft.high.level.test.OAS20.OAS20ASTTest

class DefinitionObjectJson extends OAS20ASTTest{
  test("DefinitionObjectJson name"){
    runTest( "ASTTests/SwaggerObject/SwaggerObject.json", project => {
      var expectedValue = "Pet"
      var actualValue = project.rootASTUnit.rootNode.elements("definitions").head.attribute("name").get.value
      if (actualValue.contains(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }
}
