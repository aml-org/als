package org.mulesoft.high.level.test.OAS20.AST

import org.mulesoft.high.level.test.OAS20.OAS20ASTTest
import org.mulesoft.typesystem.json.interfaces.{JSONWrapper, JSONWrapperKind}
import org.mulesoft.typesystem.json.interfaces.JSONWrapperKind._

class ItemsObjectJson extends OAS20ASTTest{

  test("ItemsObjectJson type"){
    runTest( "ASTTests/ItemsObject/ItemsObject.json", project => {
      var expectedValue = "integer"
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("parameters").head.attribute("type").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ItemsObjectJson format"){
    runTest( "ASTTests/ItemsObject/ItemsObject.json", project => {
      var expectedValue = "int64"
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("parameters").head.attribute("format").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ItemsObjectJson default"){
    runTest( "ASTTests/ItemsObject/ItemsObject3.json", project => {
      var expectedValue = "str"
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("parameters").head.attribute("default").get.value.get
        actualValue match {
            case jw: JSONWrapper =>
                jw.value(STRING) match {
                    case Some(expectedValue) => succeed
                    case _ => fail(s"Expected value: $expectedValue, no actual value")
                }
            case _ => fail(s"Expected value: $expectedValue, actual: ${actualValue}")
        }


    })
  }

  test("ItemsObjectJson maximum"){
    runTest( "ASTTests/ItemsObject/ItemsObject.json", project => {
      var expectedValue = 2222
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("parameters").head.attribute("maximum").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ItemsObjectJson exclusiveMaximum"){
    runTest( "ASTTests/ItemsObject/ItemsObject.json", project => {
      var expectedValue = 1
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("parameters").head.attributes("exclusiveMaximum").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ItemsObjectJson minimum"){
    runTest( "ASTTests/ItemsObject/ItemsObject.json", project => {
      var expectedValue = 22
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("parameters").head.attribute("minimum").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ItemsObjectJson exclusiveMinimum"){
    runTest( "ASTTests/ItemsObject/ItemsObject.json", project => {
      var expectedValue = 1
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("parameters").head.attributes("exclusiveMinimum").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ItemsObjectJson maxLength"){
    runTest( "ASTTests/ItemsObject/ItemsObject.json", project => {
      var expectedValue = 4
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("parameters").head.attribute("maxLength").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ItemsObjectJson minLength"){
    runTest( "ASTTests/ItemsObject/ItemsObject.json", project => {
      var expectedValue = 1
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("parameters").head.attribute("minLength").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ItemsObjectJson pattern"){
    runTest( "ASTTests/ItemsObject/ItemsObject3.json", project => {
      var expectedValue = "a-zA-Z"
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("parameters").head.attribute("pattern").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ItemsObjectJson maxItems"){
    runTest( "ASTTests/ItemsObject/ItemsObject2.json", project => {
      var expectedValue = 4
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("parameters").head.attribute("maxItems").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ItemsObjectJson minItems"){
    runTest( "ASTTests/ItemsObject/ItemsObject2.json", project => {
      var expectedValue = 1
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("parameters").head.attribute("minItems").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ItemsObjectJson uniqueItems"){
    runTest( "ASTTests/ItemsObject/ItemsObject2.json", project => {
      var expectedValue = true
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("parameters").head.attribute("uniqueItems").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ItemsObjectJson enum"){
    runTest( "ASTTests/ItemsObject/ItemsObject3.json", project => {
      var expectedValue = 2
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("parameters").head.attributes("enum").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ItemsObjectJson multipleOf"){
    runTest( "ASTTests/ItemsObject/ItemsObject.json", project => {
      var expectedValue = 2
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("parameters").head.attribute("multipleOf").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ItemsObjectJson example"){
    runTest( "ASTTests/ItemsObject/ItemsObject.json", project => {
      var expectedValue = 1
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("parameters").head.elements("example").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ItemsObjectJson items"){
    runTest( "ASTTests/ItemsObject/ItemsObject2.json", project => {
      var expectedValue = 1
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.elements("parameters").head.elements("items").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }
}
