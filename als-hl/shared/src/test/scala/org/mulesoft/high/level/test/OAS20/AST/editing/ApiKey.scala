package org.mulesoft.high.level.test.OAS20.AST.editing

import org.mulesoft.high.level.test.OAS20.OAS20ASTEditingTest

class ApiKey extends OAS20ASTEditingTest{
  test("ApiKey 'name' editing. YAML"){
    runAttributeEditingTest("SecurityDefinitionObject/ApiKey.yml", project => {
      project.rootASTUnit.rootNode.elements("securityDefinitions").head.attribute("name")
    }, "api_key")
  }

  test("ApiKey 'in' editing. YAML"){
    runAttributeEditingTest("SecurityDefinitionObject/ApiKey.yml", project => {
      project.rootASTUnit.rootNode.elements("securityDefinitions").head.attribute("in")
    }, "query")
  }

  test("ApiKey 'name' editing. JSON"){
    runAttributeEditingTest("SecurityDefinitionObject/ApiKey.json", project => {
      project.rootASTUnit.rootNode.elements("securityDefinitions").head.attribute("name")
    }, "api_key")
  }

  test("ApiKey 'in' editing. JSON"){
    runAttributeEditingTest("SecurityDefinitionObject/ApiKey.json", project => {
      project.rootASTUnit.rootNode.elements("securityDefinitions").head.attribute("in")
    }, "query")
  }

  test("ApiKey 'name' creation. YAML"){
    var fp = "SwaggerObject/SwaggerObjectEmpty.yml"
    parse(filePath(fp)).flatMap(project=>{
      var apiNode = project.rootASTUnit.rootNode
      var apiDef = apiNode.definition
      var securityDefinitionsNode = apiNode.newChild(apiDef.property("securityDefinitions").get).flatMap(_.asElement).get
      var securityDefinitionsDef = securityDefinitionsNode.definition
      runAttributeCreationTest1(project, project => {
        Some(project.rootASTUnit.rootNode.elements("securityDefinitions").head)
      },"name", "apiKey")
    })
  }

//  test("ApiKey 'in' creation. YAML"){
//    var fp = "SwaggerObject/SwaggerObjectEmpty.yml"
//    parse(filePath(fp)).flatMap(project=>{
//      var apiNode = project.rootASTUnit.rootNode
//      var apiDef = apiNode.definition
//      var securityDefinitionsNode = apiNode.newChild(apiDef.property("securityDefinitions").get).flatMap(_.asElement).get
//      var securityDefinitionsDef = securityDefinitionsNode.definition
//      var paramKeyAttr = securityDefinitionsNode.newChild(securityDefinitionsDef.property("name").get).flatMap(_.asAttr).get
//      paramKeyAttr.setValue("Pet").map(_ => securityDefinitionsNode)
//      var typeAttr = securityDefinitionsNode.newChild(securityDefinitionsDef.property("type").get).flatMap(_.asAttr).get
//      typeAttr.setValue("apiKey").map(_ => securityDefinitionsNode)
//      runAttributeCreationTest1(project, project => {
//        Some(project.rootASTUnit.rootNode.elements("securityDefinitions").head)
//      },"in", "header")
//    })
//  }

  test("ApiKey 'name' creation. JSON"){
    var fp = "SwaggerObject/SwaggerObjectEmpty.json"
    parse(filePath(fp)).flatMap(project=>{
      var apiNode = project.rootASTUnit.rootNode
      var apiDef = apiNode.definition
      var securityDefinitionsNode = apiNode.newChild(apiDef.property("securityDefinitions").get).flatMap(_.asElement).get
      var securityDefinitionsDef = securityDefinitionsNode.definition
      runAttributeCreationTest1(project, project => {
        Some(project.rootASTUnit.rootNode.elements("securityDefinitions").head)
      },"name", "apiKey")
    })
  }

//  test("ApiKey 'in' creation. JSON"){
//    var fp = "SwaggerObject/SwaggerObjectEmpty.json"
//    parse(filePath(fp)).flatMap(project=>{
//      var apiNode = project.rootASTUnit.rootNode
//      var apiDef = apiNode.definition
//      var securityDefinitionsNode = apiNode.newChild(apiDef.property("securityDefinitions").get).flatMap(_.asElement).get
//      var securityDefinitionsDef = securityDefinitionsNode.definition
//      var paramKeyAttr = securityDefinitionsNode.newChild(securityDefinitionsDef.property("name").get).flatMap(_.asAttr).get
//      paramKeyAttr.setValue("Pet").map(_ => securityDefinitionsNode)
//      var typeAttr = securityDefinitionsNode.newChild(securityDefinitionsDef.property("type").get).flatMap(_.asAttr).get
//      typeAttr.setValue("apiKey").map(_ => securityDefinitionsNode)
//      runAttributeCreationTest1(project, project => {
//        Some(project.rootASTUnit.rootNode.elements("securityDefinitions").head)
//      },"in", "header")
//    })
//  }
}
