package org.mulesoft.high.level.test.OAS20.AST

import org.mulesoft.high.level.test.OAS20.OAS20ASTTest

class ResponseObjectJson extends OAS20ASTTest{

  test("ResponsesObjectJson responses"){
    runTest( "ASTTests/OperationObject/OperationObject.json", project => {
      var expectedValue = 1
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.elements("responses").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ResponseJson code"){
    runTest( "ASTTests/OperationObject/OperationObject.json", project => {
      var expectedValue = "200"
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.elements("responses").head.elements("responses").head.attribute("code").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ResponseObjectJson description"){
    runTest( "ASTTests/OperationObject/OperationObject.json", project => {
      var expectedValue = "a pet to be returned"
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.elements("responses").head.elements("responses").head.attribute("description").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ResponseObjectJson schema"){
    runTest( "ASTTests/OperationObject/OperationObject.json", project => {
      var expectedValue = 1
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.elements("responses").head.elements("responses").head.elements("schema").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ResponseObjectJson headers"){
    runTest( "ASTTests/OperationObject/OperationObject.json", project => {
      var expectedValue = 3
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.elements("responses").head.elements("responses").head.elements("headers").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ResponseObjectJson example"){
    runTest( "ASTTests/OperationObject/OperationObject.json", project => {
      var expectedValue = 1
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.elements("responses").head.elements("responses").head.elements("example").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }
}
