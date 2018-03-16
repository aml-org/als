package org.mulesoft.high.level.test.OAS20.AST

import org.mulesoft.high.level.test.OAS20.OAS20ASTTest

class SchemaObject extends OAS20ASTTest{

  test("SchemaObject name"){
    runTest( "ASTTests/SchemaObject/SchemaObject.yml", project => {
      var expectedValue = "Pet"
      var actualValue = project.rootASTUnit.rootNode.elements("definitions").head.attribute("name").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("SchemaObject title"){
    runTest( "ASTTests/SchemaObject/SchemaObject.yml", project => {
      var expectedValue = "text"
      var actualValue = project.rootASTUnit.rootNode.elements("definitions").head.attribute("title").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("SchemaObject description"){
    runTest( "ASTTests/SchemaObject/SchemaObject.yml", project => {
      var expectedValue = "text"
      var actualValue = project.rootASTUnit.rootNode.elements("definitions").head.attribute("description").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("SchemaObject properties"){
    runTest( "ASTTests/SchemaObject/SchemaObject.yml", project => {
      var expectedValue = 1
      var actualValue = project.rootASTUnit.rootNode.elements("definitions").head.elements("properties").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("SchemaObject discriminator"){
    runTest( "ASTTests/SchemaObject/SchemaObject.yml", project => {
      var expectedValue = 1
      var actualValue = project.rootASTUnit.rootNode.elements("definitions").head.attributes("discriminator").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("SchemaObject xml"){
    runTest( "ASTTests/SchemaObject/SchemaObject.yml", project => {
      var expectedValue = 1
      var actualValue = project.rootASTUnit.rootNode.elements("definitions").head.elements("xml").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("SchemaObject externalDocs"){
    runTest( "ASTTests/SchemaObject/SchemaObject.yml", project => {
      var expectedValue = 1
      var actualValue = project.rootASTUnit.rootNode.elements("definitions").head.elements("externalDocs").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("SchemaObject allOf"){
    runTest( "ASTTests/SchemaObject/SchemaObject.yml", project => {
      var expectedValue = 1
      var actualValue = project.rootASTUnit.rootNode.elements("definitions").head.elements("allOf").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("SchemaObject required"){
    runTest( "ASTTests/SchemaObject/SchemaObject.yml", project => {
      var expectedValue = true
      var actualValue = project.rootASTUnit.rootNode.elements("definitions").head.elements("properties").head.attribute("required").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }
}