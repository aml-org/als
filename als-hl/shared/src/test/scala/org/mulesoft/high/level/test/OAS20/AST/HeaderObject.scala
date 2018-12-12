package org.mulesoft.high.level.test.OAS20.AST

import org.mulesoft.high.level.test.OAS20.OAS20ASTTest

class HeaderObject extends OAS20ASTTest{

  test("HeaderObject name"){
    runTest( "ASTTests/HeaderObject/HeaderObject.yml", project => {
      var expectedValue = "X-Rate-Limit-Limit"
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.elements("responses").head.elements("responses").head.elements("headers").head.attribute("name").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("HeaderObject description"){
    runTest( "ASTTests/HeaderObject/HeaderObject.yml", project => {
      var expectedValue = "The number of allowed requests in the current period"
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.elements("responses").head.elements("responses").head.elements("headers").head.attribute("description").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }
}
