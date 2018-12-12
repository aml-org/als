package org.mulesoft.high.level.test.RAML10.AST

import org.mulesoft.high.level.test.RAML10.RAML10ASTTest

class ObjectTypeDeclaration extends RAML10ASTTest {

  test("ObjectTypeDeclaration properties"){
    runTest( "ASTTests/ObjectTypeDeclaration/objectTypeDeclarationRoot.raml", project => {

      var expectedValue = 2
      var length = project.rootASTUnit.rootNode.elements("types").head.elements("properties").length
      if (length == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }

  test("ObjectTypeDeclaration minProperties") {
    runTest("ASTTests/ObjectTypeDeclaration/objectTypeDeclarationRoot.raml", project => {

      var expectedValue = 1
      var actualValue = project.rootASTUnit.rootNode.elements("types").head.attribute("minProperties").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ObjectTypeDeclaration maxProperties") {
    runTest("ASTTests/ObjectTypeDeclaration/objectTypeDeclarationRoot.raml", project => {

      var expectedValue = 2
      var actualValue = project.rootASTUnit.rootNode.elements("types").head.attribute("maxProperties").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ObjectTypeDeclaration additionalProperties") {
    runTest("ASTTests/ObjectTypeDeclaration/objectTypeDeclarationRoot.raml", project => {

      var expectedValue = false
      var actualValue = project.rootASTUnit.rootNode.elements("types").head.attribute("additionalProperties").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ObjectTypeDeclaration discriminator"){
    runTest( "ASTTests/ObjectTypeDeclaration/objectTypeDeclarationRoot.raml", project => {

      var expectedValue = "count"
      project.rootASTUnit.rootNode.elements("types").head.attribute("discriminator") match {
        case Some(a) => a.value match {
          case Some("count") => succeed
          case _ => fail(s"Expected value: $expectedValue, actual: ${a.value}")
        }
        case _ => fail("'discriminator' attribute not found")
      }
    })
  }

  test("ObjectTypeDeclaration discriminatorValue") {
    runTest("ASTTests/ObjectTypeDeclaration/objectTypeDeclarationRoot.raml", project => {

      val expectedValue = "d2"
      val actualValue = project.rootASTUnit.rootNode.elements("types").head.attribute("discriminatorValue").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }
}
