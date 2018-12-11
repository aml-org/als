package org.mulesoft.high.level.test.RAML10.AST.editing

import org.mulesoft.high.level.test.RAML10.RAML10ASTEditingTest

class OAuth1SecuritySchemeSettings extends RAML10ASTEditingTest{

  test("OAuth1SecuritySchemeSettings authorizationUri editing") {
    runAttributeEditingTest("OAuth1SecuritySchemeSettings/OAuth1SecuritySchemeSettings.raml", project => {
      project.rootASTUnit.rootNode.elements("securitySchemes").head.elements("settings").head.attribute("authorizationUri")
    }, "url")
  }

  test("OAuth1SecuritySchemeSettings requestTokenUri editing") {
    runAttributeEditingTest("OAuth1SecuritySchemeSettings/OAuth1SecuritySchemeSettings.raml", project => {
      project.rootASTUnit.rootNode.elements("securitySchemes").head.elements("settings").head.attribute("requestTokenUri")
    }, "url")
  }

  test("OAuth1SecuritySchemeSettings tokenCredentialsUri editing") {
    runAttributeEditingTest("OAuth1SecuritySchemeSettings/OAuth1SecuritySchemeSettings.raml", project => {
      project.rootASTUnit.rootNode.elements("securitySchemes").head.elements("settings").head.attribute("tokenCredentialsUri")
    }, "url")
  }

//  test("OAuth1SecuritySchemeSettings signatures editing") {
//    runAttributeEditingTest("OAuth1SecuritySchemeSettings/OAuth1SecuritySchemeSettings.raml", project => {
//      project.rootASTUnit.rootNode.elements("securitySchemes").head.elements("settings").head.attributes("signatures").head.asAttr
//    }, "TEXT")
//  }

  //

  test("OAuth1SecuritySchemeSettings authorizationUri creating") {
    runAttributeCreationTest("OAuth1SecuritySchemeSettings/OAuth1SecuritySchemeSettingsEmpty.raml", project => {
      Some(project.rootASTUnit.rootNode.elements("securitySchemes").head.elements("settings").head)
    },"authorizationUri", "url")
  }

  test("OAuth1SecuritySchemeSettings requestTokenUri creating") {
    runAttributeCreationTest("OAuth1SecuritySchemeSettings/OAuth1SecuritySchemeSettingsEmpty.raml", project => {
      Some(project.rootASTUnit.rootNode.elements("securitySchemes").head.elements("settings").head)
    },"requestTokenUri", "url")
  }

  test("OAuth1SecuritySchemeSettings tokenCredentialsUri creating") {
    runAttributeCreationTest("OAuth1SecuritySchemeSettings/OAuth1SecuritySchemeSettingsEmpty.raml", project => {
      Some(project.rootASTUnit.rootNode.elements("securitySchemes").head.elements("settings").head)
    }, "tokenCredentialsUri", "url")
  }

//  test("OAuth1SecuritySchemeSettings signatures creating") {
//    runAttributeCreationTest("OAuth1SecuritySchemeSettings/OAuth1SecuritySchemeSettingsEmpty.raml", project => {
//      Some(project.rootASTUnit.rootNode.elements("securitySchemes").head.elements("settings").head)
//    }, "signatures", "TEXT")
//  }
}
