package org.mulesoft.high.level.test.OAS20.AST.editing

import org.mulesoft.high.level.test.OAS20.OAS20ASTEditingTest

class OAuth2 extends OAS20ASTEditingTest{

  test("OAuth2 flow editing. YAML"){
    runAttributeEditingTest( "SecurityDefinitionObject/SecurityDefinitionObject.yml", project => {
      project.rootASTUnit.rootNode.elements("securityDefinitions").head.attribute("flow")
    }, "password")
  }

  test("OAuth2 authorizationUrl editing. YAML"){
    runAttributeEditingTest( "SecurityDefinitionObject/SecurityDefinitionObject.yml", project => {
      project.rootASTUnit.rootNode.elements("securityDefinitions").head.attribute("authorizationUrl")
    }, "http://swagger.io/api/oauth/dialog")
  }

  test("OAuth2 tokenUrl editing. YAML"){
    runAttributeEditingTest( "SecurityDefinitionObject/SecurityDefinitionObject.yml", project => {
      project.rootASTUnit.rootNode.elements("securityDefinitions").head.attribute("tokenUrl")
    }, "http://swagger.io/api/oauth/dialog")
  }

//  test("OAuth2 scopes editing. YAML"){
//    runAttributeEditingTest( "SecurityDefinitionObject/SecurityDefinitionObject.yml", project => {
//      project.rootASTUnit.rootNode.elements("securityDefinitions").head.elements("scopes").head.attribute("name")
//    }, "admin")
//  }

  test("OAuth2 flow editing. JSON"){
    runAttributeEditingTest( "SecurityDefinitionObject/SecurityDefinitionObject.json", project => {
      project.rootASTUnit.rootNode.elements("securityDefinitions").head.attribute("flow")
    }, "password")
  }

  test("OAuth2 authorizationUrl editing. JSON"){
    runAttributeEditingTest( "SecurityDefinitionObject/SecurityDefinitionObject.json", project => {
      project.rootASTUnit.rootNode.elements("securityDefinitions").head.attribute("authorizationUrl")
    }, "http://swagger.io/api/oauth/dialog")
  }

  test("OAuth2 tokenUrl editing. JSON"){
    runAttributeEditingTest( "SecurityDefinitionObject/SecurityDefinitionObject.json", project => {
      project.rootASTUnit.rootNode.elements("securityDefinitions").head.attribute("tokenUrl")
    }, "http://swagger.io/api/oauth/dialog")
  }

//  test("OAuth2 scopes editing. JSON"){
//    runAttributeEditingTest( "SecurityDefinitionObject/SecurityDefinitionObject.json", project => {
//      project.rootASTUnit.rootNode.elements("securityDefinitions").head.elements("scopes").head.attribute("name")
//    }, "admin")
//  }
//
//  test("OAuth2 flow creation. YAML"){
//     var fp = "SwaggerObject/SwaggerObjectEmpty.yml"
//     parse(filePath(fp)).flatMap(project=>{
//      var apiNode = project.rootASTUnit.rootNode
//      var apiDef = apiNode.definition
//      var securityDefinitionsNode = apiNode.newChild(apiDef.property("securityDefinitions").get).flatMap(_.asElement).get
//      var securityDefinitionsDef = securityDefinitionsNode.definition
//      var paramKeyAttr = securityDefinitionsNode.newChild(securityDefinitionsDef.property("name").get).flatMap(_.asAttr).get
//      paramKeyAttr.setValue("Pet").map(_ => securityDefinitionsNode)
//      var typeAttr = securityDefinitionsNode.newChild(securityDefinitionsDef.property("type").get).flatMap(_.asAttr).get
//      typeAttr.setValue("oauth2").map(_ => securityDefinitionsNode)
//      runAttributeCreationTest1(project, project => {
//        Some(project.rootASTUnit.rootNode.elements("securityDefinitions").head)
//      },"flow", "password")
//    })
//  }
//
//  test("OAuth2 authorizationUrl creation. YAML"){
//     var fp = "SwaggerObject/SwaggerObjectEmpty.yml"
//    parse(filePath(fp)).flatMap(project=>{
//      var apiNode = project.rootASTUnit.rootNode
//      var apiDef = apiNode.definition
//      var securityDefinitionsNode = apiNode.newChild(apiDef.property("securityDefinitions").get).flatMap(_.asElement).get
//      var securityDefinitionsDef = securityDefinitionsNode.definition
//      var paramKeyAttr = securityDefinitionsNode.newChild(securityDefinitionsDef.property("name").get).flatMap(_.asAttr).get
//      paramKeyAttr.setValue("Pet").map(_ => securityDefinitionsNode)
//      var typeAttr = securityDefinitionsNode.newChild(securityDefinitionsDef.property("type").get).flatMap(_.asAttr).get
//      typeAttr.setValue("oauth2").map(_ => securityDefinitionsNode)
//      runAttributeCreationTest1(project, project => {
//        Some(project.rootASTUnit.rootNode.elements("securityDefinitions").head)
//      },"authorizationUrl", "http://swagger.io/api/oauth/dialog")
//    })
//  }
//
//  test("OAuth2 tokenUrl creation. YAML"){
//     var fp = "SwaggerObject/SwaggerObjectEmpty.yml"
//    parse(filePath(fp)).flatMap(project=>{
//      var apiNode = project.rootASTUnit.rootNode
//      var apiDef = apiNode.definition
//      var securityDefinitionsNode = apiNode.newChild(apiDef.property("securityDefinitions").get).flatMap(_.asElement).get
//      var securityDefinitionsDef = securityDefinitionsNode.definition
//      var paramKeyAttr = securityDefinitionsNode.newChild(securityDefinitionsDef.property("name").get).flatMap(_.asAttr).get
//      paramKeyAttr.setValue("Pet").map(_ => securityDefinitionsNode)
//      var typeAttr = securityDefinitionsNode.newChild(securityDefinitionsDef.property("type").get).flatMap(_.asAttr).get
//      typeAttr.setValue("oauth2").map(_ => securityDefinitionsNode)
//      runAttributeCreationTest1(project, project => {
//        Some(project.rootASTUnit.rootNode.elements("securityDefinitions").head)
//      },"tokenUrl", "http://swagger.io/api/oauth/dialog")
//    })
//  }
//
//  test("OAuth2 scopes creation. YAML"){
//     var fp = "SwaggerObject/SwaggerObjectEmpty.yml"
//    parse(filePath(fp)).flatMap(project=>{
//      var apiNode = project.rootASTUnit.rootNode
//      var apiDef = apiNode.definition
//      var securityDefinitionsNode = apiNode.newChild(apiDef.property("securityDefinitions").get).flatMap(_.asElement).get
//      var securityDefinitionsDef = securityDefinitionsNode.definition
//      var paramKeyAttr = securityDefinitionsNode.newChild(securityDefinitionsDef.property("name").get).flatMap(_.asAttr).get
//      paramKeyAttr.setValue("Pet").map(_ => securityDefinitionsNode)
//      var typeAttr = securityDefinitionsNode.newChild(securityDefinitionsDef.property("type").get).flatMap(_.asAttr).get
//      typeAttr.setValue("oauth2").map(_ => securityDefinitionsNode)
//      runAttributeCreationTest1(project, project => {
//        Some(project.rootASTUnit.rootNode.elements("securityDefinitions").head)
//      },"in", "header")
//    })
//
//    runAttributeEditingTest( "SecurityDefinitionObject/SecurityDefinitionObject.yml", project => {
//      project.rootASTUnit.rootNode.elements("securityDefinitions").head.elements("scopes").head.attribute("name")
//    }, "admin")
//  }
}
