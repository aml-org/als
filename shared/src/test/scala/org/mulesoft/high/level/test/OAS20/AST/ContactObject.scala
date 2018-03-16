package org.mulesoft.high.level.test.OAS20.AST

import org.mulesoft.high.level.test.OAS20.OAS20ASTTest

class ContactObject extends OAS20ASTTest{

  test("ContactObject name"){
    runTest( "ASTTests/InfoObject/InfoObject.yml", project => {
      var expectedValue = "Swagger API Team"
      var actualValue = project.rootASTUnit.rootNode.elements("info").head.elements("contact").head.attribute("name").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ContactObject url"){
    runTest( "ASTTests/InfoObject/InfoObject.yml", project => {
      var expectedValue = "http://swagger.io"
      var actualValue = project.rootASTUnit.rootNode.elements("info").head.elements("contact").head.attribute("url").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("ContactObject email"){
    runTest( "ASTTests/InfoObject/InfoObject.yml", project => {
      var expectedValue = "example@somehost.com"
      var actualValue = project.rootASTUnit.rootNode.elements("info").head.elements("contact").head.attribute("email").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }
}
