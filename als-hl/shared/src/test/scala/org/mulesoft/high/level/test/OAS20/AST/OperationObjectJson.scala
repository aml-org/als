package org.mulesoft.high.level.test.OAS20.AST

import org.mulesoft.high.level.test.OAS20.OAS20ASTTest

class OperationObjectJson extends OAS20ASTTest{

  test("OperationObjectJson summary"){
    runTest( "ASTTests/OperationObject/OperationObject.json", project => {
      var expectedValue = "Find pet by ID"
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.attribute("summary").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("OperationObjectJson description"){
    runTest( "ASTTests/OperationObject/OperationObject.json", project => {
      var expectedValue = "Returns a pet based on ID"
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.attribute("description").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("OperationObjectJson externalDocs"){
    runTest( "ASTTests/OperationObject/OperationObject.json", project => {
      var expectedValue = 1
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.elements("externalDocs").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("OperationObjectJson operationId"){
    runTest( "ASTTests/OperationObject/OperationObject.json", project => {
      var expectedValue = "getPetsById"
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.attribute("operationId").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("OperationObjectJson method"){
    runTest( "ASTTests/OperationObject/OperationObject.json", project => {
      var expectedValue = "post"
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.attribute("method").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("OperationObjectJson consumes"){
    runTest( "ASTTests/OperationObject/OperationObject.json", project => {
      var expectedValue = 1
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.attributes("consumes").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("OperationObjectJson produces"){
    runTest( "ASTTests/OperationObject/OperationObject.json", project => {
      var expectedValue = 2
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.attributes("produces").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("OperationObjectJson parameters"){
    runTest( "ASTTests/OperationObject/OperationObject.json", project => {
      var expectedValue = 2
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.elements("parameters").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("OperationObjectJson responses"){
    runTest( "ASTTests/OperationObject/OperationObject.json", project => {
      var expectedValue = 1
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.elements("responses").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("OperationObjectJson schemes"){
    runTest( "ASTTests/OperationObject/OperationObject.json", project => {
      var expectedValue = 2
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.attributes("schemes").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("OperationObjectJson deprecated"){
    runTest( "ASTTests/OperationObject/OperationObject.json", project => {
      var expectedValue = true
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.attribute("deprecated").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("OperationObjectJson security"){
    runTest( "ASTTests/OperationObject/OperationObject.json", project => {
      var expectedValue = 2
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.elements("security").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }
}
