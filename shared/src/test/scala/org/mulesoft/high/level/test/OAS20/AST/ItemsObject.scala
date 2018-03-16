package org.mulesoft.high.level.test.OAS20.AST

import org.mulesoft.high.level.test.OAS20.OAS20ASTTest
import org.mulesoft.typesystem.json.interfaces.{JSONWrapper, JSONWrapperKind}
import org.mulesoft.typesystem.json.interfaces.JSONWrapperKind._

class ItemsObject extends OAS20ASTTest{

  test("ItemsObject type"){
    runTest( "ASTTests/ItemsObject/ItemsObject.yml", project => {
      var expectedValue = "integer"
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("parameters").head.attribute("type").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ItemsObject format"){
    runTest( "ASTTests/ItemsObject/ItemsObject.yml", project => {
      var expectedValue = "int64"
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("parameters").head.attribute("format").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ItemsObject default"){
    runTest( "ASTTests/ItemsObject/ItemsObject3.yml", project => {
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

  test("ItemsObject maximum"){
    runTest( "ASTTests/ItemsObject/ItemsObject.yml", project => {
      var expectedValue = 2222
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("parameters").head.attribute("maximum").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ItemsObject exclusiveMaximum"){
    runTest( "ASTTests/ItemsObject/ItemsObject.yml", project => {
      var expectedValue = 1
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("parameters").head.attributes("exclusiveMaximum").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ItemsObject minimum"){
    runTest( "ASTTests/ItemsObject/ItemsObject.yml", project => {
      var expectedValue = 22
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("parameters").head.attribute("minimum").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ItemsObject exclusiveMinimum"){
    runTest( "ASTTests/ItemsObject/ItemsObject.yml", project => {
      var expectedValue = 1
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("parameters").head.attributes("exclusiveMinimum").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ItemsObject maxLength"){
    runTest( "ASTTests/ItemsObject/ItemsObject.yml", project => {
      var expectedValue = 4
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("parameters").head.attribute("maxLength").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ItemsObject minLength"){
    runTest( "ASTTests/ItemsObject/ItemsObject.yml", project => {
      var expectedValue = 1
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("parameters").head.attribute("minLength").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ItemsObject pattern"){
    runTest( "ASTTests/ItemsObject/ItemsObject3.yml", project => {
      var expectedValue = "a-zA-Z"
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("parameters").head.attribute("pattern").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ItemsObject maxItems"){
    runTest( "ASTTests/ItemsObject/ItemsObject2.yml", project => {
      var expectedValue = 4
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("parameters").head.attribute("maxItems").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ItemsObject minItems"){
    runTest( "ASTTests/ItemsObject/ItemsObject2.yml", project => {
      var expectedValue = 1
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("parameters").head.attribute("minItems").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ItemsObject uniqueItems"){
    runTest( "ASTTests/ItemsObject/ItemsObject2.yml", project => {
      var expectedValue = true
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("parameters").head.attribute("uniqueItems").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ItemsObject enum"){
    runTest( "ASTTests/ItemsObject/ItemsObject3.yml", project => {
      var expectedValue = 2
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("parameters").head.attributes("enum").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ItemsObject multipleOf"){
    runTest( "ASTTests/ItemsObject/ItemsObject.yml", project => {
      var expectedValue = 2
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("parameters").head.attribute("multipleOf").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ItemsObject example"){
    runTest( "ASTTests/ItemsObject/ItemsObject.yml", project => {
      var expectedValue = 1
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("parameters").head.elements("example").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ItemsObject items"){
    runTest( "ASTTests/ItemsObject/ItemsObject2.yml", project => {
      var expectedValue = 1
      var actualValue = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.elements("parameters").head.elements("items").length
      if (actualValue == expectedValue)
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }
}
