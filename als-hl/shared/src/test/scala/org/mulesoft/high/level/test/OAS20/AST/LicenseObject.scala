package org.mulesoft.high.level.test.OAS20.AST

import org.mulesoft.high.level.test.OAS20.OAS20ASTTest

class LicenseObject extends OAS20ASTTest{

  test("LicenseObject name"){
    runTest( "ASTTests/InfoObject/InfoObject.yml", project => {
      var expectedValue = "Creative Commons"
      var actualValue = project.rootASTUnit.rootNode.elements("info").head.elements("license").head.attribute("name").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("LicenseObject license"){
    runTest( "ASTTests/InfoObject/InfoObject.yml", project => {
      var expectedValue = "http://creativecommons.org/licenses/by/4.0/"
      var actualValue = project.rootASTUnit.rootNode.elements("info").head.elements("license").head.attribute("url").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }
}
