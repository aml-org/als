package org.mulesoft.high.level.test.OAS20.AST

import org.mulesoft.high.level.test.OAS20.OAS20ASTTest

class ExternalDocumentationObjectJson extends OAS20ASTTest{

  test("ExternalDocumentationObjectJson url"){
    runTest( "ASTTests/SwaggerObject/SwaggerObject.json", project => {
      var expectedValue = "https://swagger.io"
      var actualValue = project.rootASTUnit.rootNode.elements("externalDocs").head.attribute("url").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ExternalDocumentationObjectJson description"){
    runTest( "ASTTests/SwaggerObject/SwaggerObject.json", project => {
      var expectedValue = "Find more info here"
      var actualValue = project.rootASTUnit.rootNode.elements("externalDocs").head.attribute("description").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }
}
