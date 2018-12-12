package org.mulesoft.high.level.test.RAML08.AST

import org.mulesoft.high.level.test.RAML08.RAML08ASTTest
import org.mulesoft.typesystem.json.interfaces.JSONWrapper
import org.mulesoft.typesystem.json.interfaces.JSONWrapperKind.STRING

class TemplateRef extends RAML08ASTTest {
/*
  test("TemplateRef parameters") {
    runTest("ASTTests/TemplateRef/TemplateRef.raml", project => {

      var expectedValue = 1
      var length = project.rootASTUnit.rootNode.elements("resources").head.element("type").head.elements("parameters").length
      if (length == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }

  test("TemplateParameter name") {
    runTest("ASTTests/TemplateRef/TemplateRef.raml", project => {

      var expectedValue = "objectName"
      var length = project.rootASTUnit.rootNode.elements("resources").head.element("type").head.elements("parameters").head.attribute("name").get.value
      if (length == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }

  test("TemplateParameter value") {
    runTest("ASTTests/TemplateRef/TemplateRef.raml", project => {
      var expectedValue = "TestType"
      var parameterValue = project.rootASTUnit.rootNode.elements("resources").head.element("type").head.elements("parameters").head.attribute("value").get.value
      var actualValue = parameterValue match {
        case Some(x) => x match {
          case jw: JSONWrapper => jw.value(STRING).get
          case _=> fail(s"Expected value: $expectedValue, actual: ${parameterValue}")
        }
        case _=> fail(s"Unexpected value: ${parameterValue}")
      }

      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }*/
}