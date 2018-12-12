package org.mulesoft.high.level.test.RAML10.AST

import org.mulesoft.high.level.test.RAML10.RAML10ASTTest

class DocumentationItem extends RAML10ASTTest {

  test("DocumentationItem title") {
    runTest("ASTTests/DocumentationItem/api.raml", project => {

      var expectedValue = "Title"
      project.rootASTUnit.rootNode.elements("documentation").head.attribute("title") match {
        case Some(a) => a.value match {
          case Some("Title") => succeed
          case _ => fail(s"Expected value: $expectedValue, actual: ${a.value}")
        }
        case _ => fail("'name' attribute not found")
      }
    })
  }

  test("DocumentationItem content") {
    runTest("ASTTests/DocumentationItem/api.raml", project => {

      var expectedValue = "Content"
      project.rootASTUnit.rootNode.elements("documentation").head.attribute("content") match {
        case Some(a) => a.value match {
          case Some("Content") => succeed
          case _ => fail(s"Expected value: $expectedValue, actual: ${a.value}")
        }
        case _ => fail("'name' attribute not found")
      }
    })
  }
}
