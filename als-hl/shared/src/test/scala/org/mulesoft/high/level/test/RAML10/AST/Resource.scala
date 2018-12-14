package org.mulesoft.high.level.test.RAML10.AST

import org.mulesoft.high.level.test.RAML10.RAML10ASTTest

class Resource extends RAML10ASTTest {

  test("Resource methods") {
    runTest("ASTTests/Resource/methods.raml", project => {

      var expectedValue = 3
      var length = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").length
      if (length == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }

  test("Resource is") {
    runTest("ASTTests/Resource/is.raml", project => {

      var expectedValue = 2
      var length = project.rootASTUnit.rootNode.elements("resources").head.elements("is").length
      if (length == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }

  test("Resource type"){
    runTest( "ASTTests/Resource/type.raml", project => {

      var expectedValue = "base"
      project.rootASTUnit.rootNode.elements("resources").head.element("type") match {
        case Some(a) => a match {
          case expectedValue => succeed
          case _ => fail(s"Expected value: $expectedValue, actual: ${a}")
        }
        case _ => fail("'type' attribute not found")
      }
    })
  }

  test("Resource description"){
    runTest( "ASTTests/Resource/description.raml", project => {

      var expectedValue = "test"
      project.rootASTUnit.rootNode.elements("resources").head.attribute("description") match {
        case Some(a) => a.value match {
          case Some(expectedValue) => succeed
          case _ => fail(s"Expected value: $expectedValue, actual: ${a.value}")
        }
        case _ => fail("'description' attribute not found")
      }
    })
  }

  test("Resource securedBy") {
    runTest("ASTTests/Resource/secured_by.raml", project => {

      var expectedValue = 1
      var length = project.rootASTUnit.rootNode.elements("resources").head.elements("securedBy").length
      if (length == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }

  test("Resource uriParameters") {
    runTest("ASTTests/Resource/uri_parameters.raml", project => {

      var expectedValue = 2
      var length = project.rootASTUnit.rootNode.elements("resources").head.elements("uriParameters").length
      if (length == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }

  test("Resource relativeUri"){
    runTest( "ASTTests/Resource/relative_uri.raml", project => {

      var expectedValue = "resource"
      project.rootASTUnit.rootNode.elements("resources").head.attribute("relativeUri") match {
        case Some(a) => a.value match {
          case Some(expectedValue) => succeed
          case _ => fail(s"Expected value: $expectedValue, actual: ${a.value}")
        }
        case _ => fail("'relativeUri' attribute not found")
      }
    })
  }

  test("Resource displayName"){
    runTest( "ASTTests/Resource/display_name.raml", project => {

      var expectedValue = "root"
      project.rootASTUnit.rootNode.elements("resources").head.attribute("displayName") match {
        case Some(a) => a.value match {
          case Some(expectedValue) => succeed
          case _ => fail(s"Expected value: $expectedValue, actual: ${a.value}")
        }
        case _ => fail("'displayName' attribute not found")
      }
    })
  }

  test("Resource resources") {
    runTest("ASTTests/Resource/resources.raml", project => {

      var expectedValue = 3
      var length = project.rootASTUnit.rootNode.elements("resources").head.elements("resources").length
      if (length == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }
}
