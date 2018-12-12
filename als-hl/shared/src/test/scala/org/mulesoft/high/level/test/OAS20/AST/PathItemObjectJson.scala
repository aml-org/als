package org.mulesoft.high.level.test.OAS20.AST

import org.mulesoft.high.level.test.OAS20.OAS20ASTTest

class PathItemObjectJson extends OAS20ASTTest{

  test("PathItemObjectJson path"){
    runTest( "ASTTests/PathObject/PathObject.json", project => {
      var expectedValue = "/pets/{id}"
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.attribute("path").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("PathItemObjectJson operations"){
    runTest( "ASTTests/PathObject/PathObject.json", project => {
      var expectedValue = 1
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("PathItemObjectJson parameters"){
    runTest( "ASTTests/PathObject/PathObject.json", project => {
      var expectedValue = 2
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("parameters").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }
}
