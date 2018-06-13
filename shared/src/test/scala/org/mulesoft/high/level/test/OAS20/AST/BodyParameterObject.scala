package org.mulesoft.high.level.test.OAS20.AST

import org.mulesoft.high.level.test.OAS20.OAS20ASTTest

class BodyParameterObject extends OAS20ASTTest{

  test("BodyParameterObject schema"){
    runTest( "ASTTests/BodyParameterObject/BodyParameterObject.yml", project => {
      var expectedValue = 1
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.elements("parameters").head.elements("schema").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }
  test("BodyParameterObject name"){
    runTest( "ASTTests/BodyParameterObject/BodyParameterObject.yml", project => {
      var expectedValue = "idHeader"
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.elements("parameters").head.attribute("name").get.value
      if (actualValue.contains(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue.getOrElse("null")}")
    })
  }
}
