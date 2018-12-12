package org.mulesoft.high.level.test.OAS20.AST

import org.mulesoft.high.level.test.OAS20.OAS20ASTTest

class SchemaObjectJson extends OAS20ASTTest{

  test("SchemaObjectJson name"){
    runTest( "ASTTests/SchemaObject/SchemaObject.json", project => {
      var expectedValue = "Pet"
      var actualValue = project.rootASTUnit.rootNode.elements("definitions").head.attribute("name").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("SchemaObjectJson title"){
    runTest( "ASTTests/SchemaObject/SchemaObject.json", project => {
      var expectedValue = "text"
      var actualValue = project.rootASTUnit.rootNode.elements("definitions").head.attribute("title").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("SchemaObjectJson description"){
    runTest( "ASTTests/SchemaObject/SchemaObject.json", project => {
      var expectedValue = "text"
      var actualValue = project.rootASTUnit.rootNode.elements("definitions").head.attribute("description").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("SchemaObjectJson properties"){
    runTest( "ASTTests/SchemaObject/SchemaObject.json", project => {
      var expectedValue = 1
      var actualValue = project.rootASTUnit.rootNode.elements("definitions").head.elements("properties").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("SchemaObjectJson discriminator"){
    runTest( "ASTTests/SchemaObject/SchemaObject.json", project => {
      var expectedValue = 1
      var actualValue = project.rootASTUnit.rootNode.elements("definitions").head.attributes("discriminator").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("SchemaObjectJson xml"){
    runTest( "ASTTests/SchemaObject/SchemaObject.json", project => {
      var expectedValue = 1
      var actualValue = project.rootASTUnit.rootNode.elements("definitions").head.elements("xml").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("SchemaObjectJson externalDocs"){
    runTest( "ASTTests/SchemaObject/SchemaObject.json", project => {
      var expectedValue = 1
      var actualValue = project.rootASTUnit.rootNode.elements("definitions").head.elements("externalDocs").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

//  test("SchemaObjectJson allOf"){
//    runTest( "ASTTests/SchemaObject/SchemaObject.json", project => {
//      var expectedValue = 1
//      var actualValue = project.rootASTUnit.rootNode.elements("definitions").head.elements("allOf").length
//      if (actualValue == expectedValue)
//        succeed
//      else
//        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
//    })
//  }

  test("SchemaObjectJson required"){
    runTest( "ASTTests/SchemaObject/SchemaObject.json", project => {
      var expectedValue = true
      var actualValue = project.rootASTUnit.rootNode.elements("definitions").head.elements("properties").head.attribute("required").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

//  test("SchemaObjectJson additionalProperties boolean"){
//    runTest( "ASTTests/SchemaObject/SchemaObject.json", project => {
//      var expectedValue = true
//      var actualValue = project.rootASTUnit.rootNode.elements("definitions").head.elements("properties").head.attribute("additionalProperties").get.value
//      if (actualValue == expectedValue)
//        succeed
//      else
//        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
//    })
//  }
//
//  test("SchemaObjectJson additionalProperties Schema"){
//    runTest( "ASTTests/SchemaObject/SchemaObject.json", project => {
//      var expectedValue = true
//      var actualValue = project.rootASTUnit.rootNode.elements("definitions").head.elements("properties").head.attribute("additionalProperties").get.value
//      if (actualValue == expectedValue)
//        succeed
//      else
//        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
//    })
//  }
}