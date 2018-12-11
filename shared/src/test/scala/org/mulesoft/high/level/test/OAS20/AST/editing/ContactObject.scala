package org.mulesoft.high.level.test.OAS20.AST.editing

import org.mulesoft.high.level.test.OAS20.OAS20ASTEditingTest

class ContactObject extends OAS20ASTEditingTest{

  test("ContactObject name editing YAML"){
     runAttributeEditingTest( "InfoObject/InfoObject.yml", project => {
      project.rootASTUnit.rootNode.elements("info").head.elements("contact").head.attribute("name")
    }, "SAT")
  }

  test("ContactObject url editing YAML"){
     runAttributeEditingTest( "InfoObject/InfoObject.yml", project => {
      project.rootASTUnit.rootNode.elements("info").head.elements("contact").head.attribute("url")
    }, "https://example.com")
  }

  test("ContactObject email editing YAML"){
     runAttributeEditingTest( "InfoObject/InfoObject.yml", project => {
      project.rootASTUnit.rootNode.elements("info").head.elements("contact").head.attribute("email")
    }, "email@swagger.io")
  }

  test("ContactObject name editing JSON"){
     runAttributeEditingTest( "InfoObject/InfoObject.json", project => {
      project.rootASTUnit.rootNode.elements("info").head.elements("contact").head.attribute("name")
    }, "SAT")
  }

  test("ContactObject url editing JSON"){
     runAttributeEditingTest( "InfoObject/InfoObject.json", project => {
      project.rootASTUnit.rootNode.elements("info").head.elements("contact").head.attribute("url")
    }, "https://example.com")
  }

  test("ContactObject email editing JSON"){
     runAttributeEditingTest( "InfoObject/InfoObject.json", project => {
      project.rootASTUnit.rootNode.elements("info").head.elements("contact").head.attribute("email")
    }, "email@swagger.io")
  }

  test("ContactObject name creation YAML"){
    var fp = "SwaggerObject/SwaggerObjectEmpty.yml"
    parse(filePath(fp)).flatMap(project=>{
      var apiNode = project.rootASTUnit.rootNode
      var apiDef = apiNode.definition
      var infoNode = apiNode.newChild(apiDef.property("info").get).flatMap(_.asElement).get
      var infoDef = infoNode.definition
      var contactNode = infoNode.newChild(infoDef.property("contact").get).flatMap(_.asElement).get
      var contactDef = contactNode.definition
      runAttributeCreationTest1(project, project => {
        Some(project.rootASTUnit.rootNode.elements("info").head.elements("contact").head)
      },"name","SAT")
    })
  }

  test("ContactObject url creation YAML"){
    var fp = "SwaggerObject/SwaggerObjectEmpty.yml"
    parse(filePath(fp)).flatMap(project=>{
      var apiNode = project.rootASTUnit.rootNode
      var apiDef = apiNode.definition
      var infoNode = apiNode.newChild(apiDef.property("info").get).flatMap(_.asElement).get
      var infoDef = infoNode.definition
      var contactNode = infoNode.newChild(infoDef.property("contact").get).flatMap(_.asElement).get
      var contactDef = contactNode.definition
      runAttributeCreationTest1(project, project => {
        Some(project.rootASTUnit.rootNode.elements("info").head.elements("contact").head)
      },"url","https://example.com")
    })
  }

  test("ContactObject email creation YAML"){
    var fp = "SwaggerObject/SwaggerObjectEmpty.yml"
    parse(filePath(fp)).flatMap(project=>{
      var apiNode = project.rootASTUnit.rootNode
      var apiDef = apiNode.definition
      var infoNode = apiNode.newChild(apiDef.property("info").get).flatMap(_.asElement).get
      var infoDef = infoNode.definition
      var contactNode = infoNode.newChild(infoDef.property("contact").get).flatMap(_.asElement).get
      var contactDef = contactNode.definition
      runAttributeCreationTest1(project, project => {
        Some(project.rootASTUnit.rootNode.elements("info").head.elements("contact").head)
      },"email","email@swagger.io")
    })
  }

  test("ContactObject name creation JSON"){
    var fp = "SwaggerObject/SwaggerObjectEmpty.json"
    parse(filePath(fp)).flatMap(project=>{
      var apiNode = project.rootASTUnit.rootNode
      var apiDef = apiNode.definition
      var infoNode = apiNode.newChild(apiDef.property("info").get).flatMap(_.asElement).get
      var infoDef = infoNode.definition
      var contactNode = infoNode.newChild(infoDef.property("contact").get).flatMap(_.asElement).get
      var contactDef = contactNode.definition
      runAttributeCreationTest1(project, project => {
        Some(project.rootASTUnit.rootNode.elements("info").head.elements("contact").head)
      },"name","SAT")
    })
  }

  test("ContactObject url creation JSON"){
    var fp = "SwaggerObject/SwaggerObjectEmpty.json"
    parse(filePath(fp)).flatMap(project=>{
      var apiNode = project.rootASTUnit.rootNode
      var apiDef = apiNode.definition
      var infoNode = apiNode.newChild(apiDef.property("info").get).flatMap(_.asElement).get
      var infoDef = infoNode.definition
      var contactNode = infoNode.newChild(infoDef.property("contact").get).flatMap(_.asElement).get
      var contactDef = contactNode.definition
      runAttributeCreationTest1(project, project => {
        Some(project.rootASTUnit.rootNode.elements("info").head.elements("contact").head)
      },"url","https://example.com")
    })
  }

  test("ContactObject email creation JSON"){
    var fp = "SwaggerObject/SwaggerObjectEmpty.json"
    parse(filePath(fp)).flatMap(project=>{
      var apiNode = project.rootASTUnit.rootNode
      var apiDef = apiNode.definition
      var infoNode = apiNode.newChild(apiDef.property("info").get).flatMap(_.asElement).get
      var infoDef = infoNode.definition
      var contactNode = infoNode.newChild(infoDef.property("contact").get).flatMap(_.asElement).get
      var contactDef = contactNode.definition
      runAttributeCreationTest1(project, project => {
        Some(project.rootASTUnit.rootNode.elements("info").head.elements("contact").head)
      },"email","email@swagger.io")
    })
  }
}
