package org.mulesoft.high.level.test.RAML10.AST

import org.mulesoft.high.level.test.RAML10.RAML10ASTTest

class TypeDeclaration extends RAML10ASTTest {

  test("TypeDeclaration name") {
    runTest("ASTTests/TypeDeclaration/typeDeclarationRoot.raml", project => {

      val expectedValue = "airplane"
      project.rootASTUnit.rootNode.elements("types").head.attribute("name") match {
        case Some(a) => a.value match {
          case Some(_) => succeed
          case _ => fail(s"Expected value: $expectedValue, actual: $a")
        }
        case _ => fail("'name' attribute not found")
      }
    })
  }

  test("TypeDeclaration displayName") {
    runTest("ASTTests/TypeDeclaration/typeDeclarationRoot.raml", project => {

      val expectedValue = "plane"
      project.rootASTUnit.rootNode.elements("types").head.attribute("displayName") match {
        case Some(a) => a.value match {
          case Some(_) => succeed
          case _ => fail(s"Expected value: $expectedValue, actual: ${a.value}")
        }
        case _ => fail("'displayName' attribute not found")
      }
    })
  }

  test("TypeDeclaration description") {
    runTest("ASTTests/TypeDeclaration/typeDeclarationRoot.raml", project => {

      val expectedValue = "none"
      project.rootASTUnit.rootNode.elements("types").head.attribute("description") match {
        case Some(a) => a.value match {
          case Some(_) => succeed
          case _ => fail(s"Expected value: $expectedValue, actual: ${a.value}")
        }
        case _ => fail("'displayName' attribute not found")
      }
    })
  }

  test("TypeDeclaration facets") {
    runTest("ASTTests/TypeDeclaration/typeDeclarationRoot.raml", project => {

      val expectedValue = 1
      val length = project.rootASTUnit.rootNode.elements("types").head.elements("facets").length
      if (length == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: $length")
    })
  }

  test("TypeDeclaration examples") {
    runTest("ASTTests/TypeDeclaration/typeDeclarationRoot.raml", project => {

      val expectedValue = 2
      val length = project.rootASTUnit.rootNode.elements("types").head.elements("examples").length
      if (length == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: $length")
    })
  }

  test("TypeDeclaration example") {
    runTest("ASTTests/TypeDeclaration/typeDeclarationRoot.raml", project => {

      val expectedValue = 1
      val length = project.rootASTUnit.rootNode.elements("types").head.elements("example").length
      if (length == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: $length")
    })
  }

  test("TypeDeclaration xml") {
    runTest("ASTTests/TypeDeclaration/typeDeclarationRoot.raml", project => {

      val expectedValue = 1
      val length = project.rootASTUnit.rootNode.elements("types").head.elements("xml").length
      if (length == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: $length")
    })
  }
}
