package org.mulesoft.high.level.test.OAS20.AST

import org.mulesoft.high.level.test.OAS20.OAS20ASTTest

class ParameterObjectJson extends OAS20ASTTest{

  test("ParameterObjectJson name"){
    runTest( "ASTTests/OperationObject/OperationObject.json", project => {
      var expectedValue = "id"
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("parameters").head.attribute("name").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ParameterObjectJson description"){
    runTest( "ASTTests/OperationObject/OperationObject.json", project => {
      var expectedValue = "ID of pet to use"
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("parameters").head.attribute("description").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ParameterObjectJson in"){
    runTest( "ASTTests/OperationObject/OperationObject.json", project => {
      var expectedValue = "path"
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("parameters").head.attribute("in").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ParameterObjectJson required"){
    runTest( "ASTTests/OperationObject/OperationObject.json", project => {
      var expectedValue = true
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("parameters").head.attribute("required").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }
}
