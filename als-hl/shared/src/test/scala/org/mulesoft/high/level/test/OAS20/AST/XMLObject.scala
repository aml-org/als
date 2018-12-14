package org.mulesoft.high.level.test.OAS20.AST

import org.mulesoft.high.level.test.OAS20.OAS20ASTTest

class XMLObject extends OAS20ASTTest{

  test("XMLObject attribute"){
    runTest( "ASTTests/SchemaObject/SchemaObject.yml", project => {

      var expectedValue = true
      var length = project.rootASTUnit.rootNode.elements("definitions").head.elements("xml").head.attribute("attribute").get.value
      if (length == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }

  test("XMLObject wrapped"){
    runTest( "ASTTests/SchemaObject/SchemaObject.yml", project => {

      var expectedValue = true
      var length = project.rootASTUnit.rootNode.elements("definitions").head.elements("xml").head.attribute("wrapped").get.value
      if (length == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }

  test("XMLObject name"){
    runTest( "ASTTests/SchemaObject/SchemaObject.yml", project => {

      var expectedValue = "fullname"
      var length = project.rootASTUnit.rootNode.elements("definitions").head.elements("xml").head.attribute("name").get.value
      if (length == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }

  test("XMLObject namespace"){
    runTest( "ASTTests/SchemaObject/SchemaObject.yml", project => {

      var expectedValue = "air"
      var length = project.rootASTUnit.rootNode.elements("definitions").head.elements("xml").head.attribute("namespace").get.value
      if (length == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }

  test("XMLObject prefix"){
    runTest( "ASTTests/SchemaObject/SchemaObject.yml", project => {

      var expectedValue = "air"
      var length = project.rootASTUnit.rootNode.elements("definitions").head.elements("xml").head.attribute("prefix").get.value
      if (length == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${length}")
    })
  }
}
