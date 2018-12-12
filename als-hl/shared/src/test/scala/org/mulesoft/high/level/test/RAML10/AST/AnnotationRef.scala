package org.mulesoft.high.level.test.RAML10.AST

import org.mulesoft.high.level.test.RAML10.RAML10ASTTest
import org.mulesoft.typesystem.json.interfaces.JSONWrapper
import org.mulesoft.typesystem.json.interfaces.JSONWrapperKind._

class AnnotationRef extends RAML10ASTTest {

  test("AnnotationRef name") {
    runTest("ASTTests/AnnotationRef/AnnotationRef.raml", project => {

      var expectedValue = "abc"
      var actualValue = project.rootASTUnit.rootNode.elements("resources").head.elements("annotations").head.attribute("name").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("AnnotationRef annotation") {
    runTest("ASTTests/AnnotationRef/AnnotationRef.raml", project => {

      var expectedValue = 1
      var actualValue = project.rootASTUnit.rootNode.elements("resources").head.element("annotations").head.elements("annotation").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("AnnotationRef value") {
    runTest("ASTTests/AnnotationRef/AnnotationRef.raml", project => {

      var expectedValue = 4
      var annotationValue = project.rootASTUnit.rootNode.elements("resources").head.element("annotations").head.attribute("value").get.value
      var actualValue = annotationValue match {
        case Some(x) => x match {
          case jw: JSONWrapper => jw.propertyValue("length", NUMBER).get
          case _=> fail(s"Expected value: $expectedValue, actual: ${annotationValue}")
        }
        case _=> fail(s"Unexpected value: ${annotationValue}")
      }

      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }
}
