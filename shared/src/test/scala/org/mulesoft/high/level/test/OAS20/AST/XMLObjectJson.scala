package org.mulesoft.high.level.test.OAS20.AST

import org.mulesoft.high.level.test.OAS20.OAS20ASTTest

class XMLObjectJson extends OAS20ASTTest{

  test("XMLObjectJson attribute"){
    runTest( "ASTTests/SchemaObject/SchemaObject.json", project => {

      var expectedValue = true
      var length = project.rootASTUnit.rootNode.elements("definitions").head.elements("xml").head.attribute("attribute").get.value
      if (length == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }

  test("XMLObjectJson wrapped"){
    runTest( "ASTTests/SchemaObject/SchemaObject.json", project => {

      var expectedValue = true
      var length = project.rootASTUnit.rootNode.elements("definitions").head.elements("xml").head.attribute("wrapped").get.value
      if (length == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }

  test("XMLObjectJson name"){
    runTest( "ASTTests/SchemaObject/SchemaObject.json", project => {

      var expectedValue = "fullname"
      var length = project.rootASTUnit.rootNode.elements("definitions").head.elements("xml").head.attribute("name").get.value
      if (length == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }

  test("XMLObjectJson namespace"){
    runTest( "ASTTests/SchemaObject/SchemaObject.json", project => {

      var expectedValue = "air"
      var length = project.rootASTUnit.rootNode.elements("definitions").head.elements("xml").head.attribute("namespace").get.value
      if (length == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }

  test("XMLObjectJson prefix"){
    runTest( "ASTTests/SchemaObject/SchemaObject.json", project => {

      var expectedValue = "air"
      var length = project.rootASTUnit.rootNode.elements("definitions").head.elements("xml").head.attribute("prefix").get.value
      if (length == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }
}
