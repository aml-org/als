package org.mulesoft.high.level.test.RAML10.AST

import org.mulesoft.high.level.test.RAML10.RAML10ASTTest

class Trait extends RAML10ASTTest {

  test("Trait name") {
    runTest("ASTTests/Trait/api.raml", project => {

      var expectedValue = "TestTrait"
      var actualValue = project.rootASTUnit.rootNode.elements("traits").head.attribute("name").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Trait parameters") {
    runTest("ASTTests/Trait/api.raml", project => {

      var expectedValue = 1
      var actualValue = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("is").head.elements("parameters").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Trait parameter name") {
    runTest("ASTTests/Trait/api.raml", project => {

      var expectedValue = "limitDefault"
      var actualValue = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("is").head.elements("parameters").head.attribute("name").get.value.get
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Trait queryParameters") {
    runTest("ASTTests/Trait/api.raml", project => {

      var expectedValue = 1
      var actualValue = project.rootASTUnit.rootNode.elements("traits").head.elements("queryParameters").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Trait queryParameter name") {
    runTest("ASTTests/Trait/api.raml", project => {

      var expectedValue = "limit"
      var actualValue = project.rootASTUnit.rootNode.elements("traits").head.elements("queryParameters").head.attribute("name").get.value.get
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Trait headers") {
    runTest("ASTTests/Trait/api.raml", project => {

      var expectedValue = 1
      var actualValue = project.rootASTUnit.rootNode.elements("traits").head.elements("headers").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Trait header name") {
    runTest("ASTTests/Trait/api.raml", project => {

      var expectedValue = "If-Match"
      var actualValue = project.rootASTUnit.rootNode.elements("traits").head.elements("headers").head.attribute("name").get.value.get
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Trait queryString"){
    runTest( "ASTTests/Trait/api2.raml", project => {

      var expectedValue = 1
      var actualValue = project.rootASTUnit.rootNode.elements("traits").head.elements("queryString").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Trait responses") {
    runTest("ASTTests/Trait/api.raml", project => {

      var expectedValue = 3
      var actualValue = project.rootASTUnit.rootNode.elements("traits").head.elements("responses").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Trait body") {
    runTest("ASTTests/Trait/api.raml", project => {

      var expectedValue = 1
      var actualValue = project.rootASTUnit.rootNode.elements("traits").head.elements("body").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Trait protocols") {
    runTest("ASTTests/Trait/api.raml", project => {

      var expectedValue = 2
      var actualValue = project.rootASTUnit.rootNode.elements("traits").head.attributes("protocols").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Trait is") {
    runTest("ASTTests/Trait/api.raml", project => {

      var expectedValue = 1
      var actualValue = project.rootASTUnit.rootNode.elements("traits").head.elements("is").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Trait is value") {
    runTest("ASTTests/Trait/api.raml", project => {

      var expectedValue = "TR"
      var actualValue = project.rootASTUnit.rootNode.elements("traits").head.elements("is").head.attribute("name").get.value.get
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Trait securedBy") {
    runTest("ASTTests/Trait/api.raml", project => {

      var expectedValue = 1
      var actualValue = project.rootASTUnit.rootNode.elements("traits").head.elements("securedBy").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Trait securedBy value") {
    runTest("ASTTests/Trait/api.raml", project => {

      var expectedValue = "oauth"
      var actualValue = project.rootASTUnit.rootNode.elements("traits").head.elements("securedBy").head.attribute("name").get.value.get
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Trait description"){
    runTest( "ASTTests/Trait/api.raml", project => {

      var expectedValue = "trait"
      var actualValue = project.rootASTUnit.rootNode.elements("traits").head.attribute("description").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("Trait displayName"){
    runTest( "ASTTests/Trait/api.raml", project => {

      var expectedValue = "dn"
      var actualValue = project.rootASTUnit.rootNode.elements("traits").head.attribute("displayName").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

//  test("Trait usage"){
//    runTest( "ASTTests/Trait/api.raml", project => {
//
//      var expectedValue = "usage"
//      var actualValue = project.rootASTUnit.rootNode.elements("traits").head.attribute("usage").get.value
//      if (actualValue == Some(expectedValue))
//        succeed
//      else
//        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
//    })
//  }
}
