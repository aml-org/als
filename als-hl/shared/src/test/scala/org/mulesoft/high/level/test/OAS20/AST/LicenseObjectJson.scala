package org.mulesoft.high.level.test.OAS20.AST

import org.mulesoft.high.level.test.OAS20.OAS20ASTTest

class LicenseObjectJson extends OAS20ASTTest{

  test("LicenseObjectJson name"){
    runTest( "ASTTests/InfoObject/InfoObject.json", project => {
      var expectedValue = "Creative Commons 4.0 International"
      var actualValue = project.rootASTUnit.rootNode.elements("info").head.elements("license").head.attribute("name").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }

  test("LicenseObjectJson license"){
    runTest( "ASTTests/InfoObject/InfoObject.json", project => {
      var expectedValue = "http://creativecommons.org/licenses/by/4.0/"
      var actualValue = project.rootASTUnit.rootNode.elements("info").head.elements("license").head.attribute("url").get.value
      if (actualValue == Some(expectedValue))
        succeed
      else
        fail(s"Expected value: $expectedValue, actual: ${actualValue}")
    })
  }
}
