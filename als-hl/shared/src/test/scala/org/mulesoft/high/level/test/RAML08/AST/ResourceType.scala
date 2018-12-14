package org.mulesoft.high.level.test.RAML08.AST

import org.mulesoft.high.level.test.RAML08.RAML08ASTTest

class ResourceType  extends RAML08ASTTest {

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

  test("ResourceType method name") {
    runTest("ASTTests/ResourceType/api.raml", project => {

      var expectedValue = "post"
      var length = project.rootASTUnit.rootNode.elements("resourceTypes").head.elements("methods").head.attribute("method").get.value
      if (length == Some(expectedValue))
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
/*

  test("ResourceType trait name") {
    runTest("ASTTests/ResourceType/api.raml", project => {

      var expectedValue = "trait"
      var actualValue = project.rootASTUnit.rootNode.elements("resourceTypes").head.elements("is").head.attribute("name").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }
*/

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
/*

  test("ResourceType type name"){
    runTest("ASTTests/ResourceType/api.raml", project => {

      var expectedValue = "bb"
      var actualValue = project.rootASTUnit.rootNode.elements("resourceTypes").head.elements("type").head.attribute("name").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }
*/

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
/*

  test("ResourceType securedBy value") {
    runTest("ASTTests/ResourceType/api.raml", project => {

      var expectedValue = "oauth"
      var actualValue = project.rootASTUnit.rootNode.elements("resourceTypes").head.elements("securedBy").head.attribute("name").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }
*/

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

  test("ResourceType uriParameter name") {
    runTest("ASTTests/ResourceType/api.raml", project => {

      var expectedValue = "id"
      var actualValue = project.rootASTUnit.rootNode.elements("resourceTypes").head.elements("uriParameters").head.attribute("name").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
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
