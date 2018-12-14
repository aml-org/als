package org.mulesoft.high.level.test.OAS20.AST

import org.mulesoft.high.level.test.OAS20.OAS20ASTTest

class BodyParameterObjectJson extends OAS20ASTTest{

  test("BodyParameterObjectJson schema"){
    runTest( "ASTTests/BodyParameterObject/BodyParameterObject.json", project => {
      var expectedValue = 1
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.elements("parameters").head.elements("schema").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }
  test("BodyParameterObjectJson name"){
    runTest( "ASTTests/BodyParameterObject/BodyParameterObject.json", project => {
      var expectedValue = "idHeader"
        var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.elements("parameters").head.attribute("name").get.value
        if (actualValue.contains(expectedValue))
          succeed
        else
          fail(s"Expected value: $expectedValue, actual: ${actualValue.getOrElse("null")}")
    })
  }
}
