package org.mulesoft.high.level.test.OAS20.AST

import org.mulesoft.high.level.test.OAS20.OAS20ASTTest

class PathItemObject extends OAS20ASTTest{

  test("PathItemObject path"){
    runTest( "ASTTests/PathObject/PathObject.yml", project => {
      var expectedValue = "/pets/{id}"
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.attribute("path").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("PathItemObject operations"){
    runTest( "ASTTests/PathObject/PathObject.yml", project => {
      var expectedValue = 1
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("PathItemObject parameters"){
    runTest( "ASTTests/PathObject/PathObject.yml", project => {
      var expectedValue = 2
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("parameters").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }
}
