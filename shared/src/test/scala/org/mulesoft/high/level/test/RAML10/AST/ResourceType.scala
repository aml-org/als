package org.mulesoft.high.level.test.RAML10.AST

import org.mulesoft.high.level.test.RAML10.RAML10ASTTest

class ResourceType  extends RAML10ASTTest {

  test("ResourceType name") {
    runTest("ASTTests/ResourceType/api.raml", project => {

      var expectedValue = "TestResorceType"
      project.rootASTUnit.rootNode.elements("resourceTypes").head.attribute("name") match {
        case Some(a) => a.value match {
          case Some("TestResorceType") => succeed
          case _ => fail(s"Expected value: $expectedValue, actual: ${a.value}")
        }
        case _ => fail("'name' attribute not found")
      }
    })
  }

  test("ResourceType parameters") {
    runTest("ASTTests/ResourceType/api.raml", project => {

      var expectedValue = 1
      var length = project.rootASTUnit.rootNode.elements("resourceTypes").head.attributes("parameters").length
      if (length == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }

  test("ResourceType parameter name") {
    runTest("ASTTests/ResourceType/api.raml", project => {

      var expectedValue = "objectName"
      var length = project.rootASTUnit.rootNode.elements("resourceTypes").head.attributes("parameters").head.value
      if (length == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }

  test("ResourceType methods") {
    runTest("ASTTests/ResourceType/api.raml", project => {

      var expectedValue = 1
      var length = project.rootASTUnit.rootNode.elements("resourceTypes").head.elements("methods").length
      if (length == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }

  test("ResourceType is") {
    runTest("ASTTests/ResourceType/api.raml", project => {

      var expectedValue = 1
      var length = project.rootASTUnit.rootNode.elements("resourceTypes").head.elements("is").length
      if (length == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }

  test("ResourceType type"){
    runTest("ASTTests/ResourceType/api.raml", project => {

      var expectedValue = 1
      var actualValue = project.rootASTUnit.rootNode.elements("resourceTypes").head.elements("type").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ResourceType description"){
    runTest("ASTTests/ResourceType/api.raml", project => {

      var expectedValue = "RT"
      var actualValue = project.rootASTUnit.rootNode.elements("resourceTypes").head.attribute("description").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ResourceType securedBy") {
    runTest("ASTTests/ResourceType/api.raml", project => {

      var expectedValue = 1
      var length = project.rootASTUnit.rootNode.elements("resourceTypes").head.elements("securedBy").length
      if (length == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }

  test("ResourceType uriParameters") {
    runTest("ASTTests/ResourceType/api.raml", project => {

      var expectedValue = 1
      var length = project.rootASTUnit.rootNode.elements("resourceTypes").head.elements("uriParameters").length
      if (length == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }

  test("ResourceType displayName"){
    runTest("ASTTests/ResourceType/api.raml", project => {

      var expectedValue = "TRT"
      var actualValue = project.rootASTUnit.rootNode.elements("resourceTypes").head.attribute("displayName").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

//  test("ResourceType usage") {
//    runTest("ASTTests/ResourceType/api2.raml", project => {
//
//      var expectedValue = "usage"
//      var actualValue = project.rootASTUnit.rootNode.attribute("usage").get.value
//      if (actualValue == Some(expectedValue))
//        succeed
//      else
//        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
//    })
//  }
}
