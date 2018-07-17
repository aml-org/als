package org.mulesoft.high.level.test.OAS20.AST.editing

import org.mulesoft.high.level.test.OAS20.OAS20ASTEditingTest

class InfoObject extends OAS20ASTEditingTest{

  test("InfoObject title editing YAML"){
    runAttributeEditingTest( "InfoObject/InfoObject.yml", project => {
      project.rootASTUnit.rootNode.elements("info").head.attribute("title")
    }, "SWGR")
  }

  test("InfoObject version editing YAML"){
    runAttributeEditingTest( "InfoObject/InfoObject.yml", project => {
      project.rootASTUnit.rootNode.elements("info").head.attribute("version")
    }, "v2")
  }

  test("InfoObject description editing YAML"){
    runAttributeEditingTest( "InfoObject/InfoObject.yml", project => {
      project.rootASTUnit.rootNode.elements("info").head.attribute("description")
    }, "dscrpn")
  }

  test("InfoObject termsOfService editing YAML"){
    runAttributeEditingTest( "InfoObject/InfoObject.yml", project => {
      project.rootASTUnit.rootNode.elements("info").head.attribute("termsOfService")
    }, "https://example.com/tos")
  }

  test("InfoObject license editing YAML"){
    runAttributeEditingTest( "InfoObject/InfoObject.yml", project => {
      project.rootASTUnit.rootNode.elements("info").head.elements("license").head.attribute("name")
    }, "CC")
  }

  test("InfoObject contact editing YAML"){
    runAttributeEditingTest( "InfoObject/InfoObject.yml", project => {
      project.rootASTUnit.rootNode.elements("info").head.elements("contact").head.attribute("name")
    }, "SAT")
  }

  test("InfoObject title editing JSON"){
    runAttributeEditingTest( "InfoObject/InfoObject.json", project => {
      project.rootASTUnit.rootNode.elements("info").head.attribute("title")
    }, "SWGR")
  }

  test("InfoObject version editing JSON"){
    runAttributeEditingTest( "InfoObject/InfoObject.json", project => {
      project.rootASTUnit.rootNode.elements("info").head.attribute("version")
    }, "v2")
  }

  test("InfoObject description editing JSON"){
    runAttributeEditingTest( "InfoObject/InfoObject.json", project => {
      project.rootASTUnit.rootNode.elements("info").head.attribute("description")
    }, "dscrpn")
  }

  test("InfoObject termsOfService editing JSON"){
    runAttributeEditingTest( "InfoObject/InfoObject.json", project => {
      project.rootASTUnit.rootNode.elements("info").head.attribute("termsOfService")
    }, "https://example.com/tos")
  }

  test("InfoObject license editing JSON"){
    runAttributeEditingTest( "InfoObject/InfoObject.json", project => {
      project.rootASTUnit.rootNode.elements("info").head.elements("license").head.attribute("name")
    }, "CC")
  }

  test("InfoObject contact editing JSON"){
    runAttributeEditingTest( "InfoObject/InfoObject.json", project => {
      project.rootASTUnit.rootNode.elements("info").head.elements("contact").head.attribute("name")
    }, "SAT")
  }

  test("InfoObject title creation YAML"){
    var fp = "SwaggerObject/SwaggerObjectEmpty.yml"
    parse(filePath(fp)).flatMap(project=>{
      var apiNode = project.rootASTUnit.rootNode
      var apiDef = apiNode.definition
      var infoNode = apiNode.newChild(apiDef.property("info").get).flatMap(_.asElement).get
      var infoDef = infoNode.definition
      runAttributeCreationTest1(project, project => {
        Some(project.rootASTUnit.rootNode.elements("info").head)
      },"title","SWGR")
    })
  }

  test("InfoObject version creation YAML"){
    var fp = "SwaggerObject/SwaggerObjectEmpty.yml"
    parse(filePath(fp)).flatMap(project=>{
      var apiNode = project.rootASTUnit.rootNode
      var apiDef = apiNode.definition
      var infoNode = apiNode.newChild(apiDef.property("info").get).flatMap(_.asElement).get
      var infoDef = infoNode.definition
      runAttributeCreationTest1(project, project => {
        Some(project.rootASTUnit.rootNode.elements("info").head)
      },"version","v2")
    })
  }

  test("InfoObject description creation YAML"){
    var fp = "SwaggerObject/SwaggerObjectEmpty.yml"
    parse(filePath(fp)).flatMap(project=>{
      var apiNode = project.rootASTUnit.rootNode
      var apiDef = apiNode.definition
      var infoNode = apiNode.newChild(apiDef.property("info").get).flatMap(_.asElement).get
      var infoDef = infoNode.definition
      runAttributeCreationTest1(project, project => {
        Some(project.rootASTUnit.rootNode.elements("info").head)
      },"description","dscrptn")
    })
  }

  test("InfoObject termsOfService creation YAML"){
    var fp = "SwaggerObject/SwaggerObjectEmpty.yml"
    parse(filePath(fp)).flatMap(project=>{
      var apiNode = project.rootASTUnit.rootNode
      var apiDef = apiNode.definition
      var infoNode = apiNode.newChild(apiDef.property("info").get).flatMap(_.asElement).get
      var infoDef = infoNode.definition
      runAttributeCreationTest1(project, project => {
        Some(project.rootASTUnit.rootNode.elements("info").head)
      },"termsOfService","https://example.com/tos")
    })
  }

  test("InfoObject license creation YAML"){
    var fp = "SwaggerObject/SwaggerObjectEmpty.yml"
    parse(filePath(fp)).flatMap(project=>{
      var apiNode = project.rootASTUnit.rootNode
      var apiDef = apiNode.definition
      var infoNode = apiNode.newChild(apiDef.property("info").get).flatMap(_.asElement).get
      var infoDef = infoNode.definition
      var licenseNode = infoNode.newChild(infoDef.property("license").get).flatMap(_.asElement).get
      var licenseDef = licenseNode.definition
      runAttributeCreationTest1(project, project => {
        Some(project.rootASTUnit.rootNode.elements("info").head.elements("license").head)
      },"name","CC")
    })
  }

  test("InfoObject contact creation YAML"){
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

  test("InfoObject title creation JSON"){
    var fp = "SwaggerObject/SwaggerObjectEmpty.json"
    parse(filePath(fp)).flatMap(project=>{
      var apiNode = project.rootASTUnit.rootNode
      var apiDef = apiNode.definition
      var infoNode = apiNode.newChild(apiDef.property("info").get).flatMap(_.asElement).get
      var infoDef = infoNode.definition
      runAttributeCreationTest1(project, project => {
        Some(project.rootASTUnit.rootNode.elements("info").head)
      },"title","SWGR")
    })
  }

  test("InfoObject version creation JSON"){
    var fp = "SwaggerObject/SwaggerObjectEmpty.json"
    parse(filePath(fp)).flatMap(project=>{
      var apiNode = project.rootASTUnit.rootNode
      var apiDef = apiNode.definition
      var infoNode = apiNode.newChild(apiDef.property("info").get).flatMap(_.asElement).get
      var infoDef = infoNode.definition
      runAttributeCreationTest1(project, project => {
        Some(project.rootASTUnit.rootNode.elements("info").head)
      },"version","v2")
    })
  }

  test("InfoObject description creation JSON"){
    var fp = "SwaggerObject/SwaggerObjectEmpty.json"
    parse(filePath(fp)).flatMap(project=>{
      var apiNode = project.rootASTUnit.rootNode
      var apiDef = apiNode.definition
      var infoNode = apiNode.newChild(apiDef.property("info").get).flatMap(_.asElement).get
      var infoDef = infoNode.definition
      runAttributeCreationTest1(project, project => {
        Some(project.rootASTUnit.rootNode.elements("info").head)
      },"description","dscrptn")
    })
  }

  test("InfoObject termsOfService creation JSON"){
    var fp = "SwaggerObject/SwaggerObjectEmpty.json"
    parse(filePath(fp)).flatMap(project=>{
      var apiNode = project.rootASTUnit.rootNode
      var apiDef = apiNode.definition
      var infoNode = apiNode.newChild(apiDef.property("info").get).flatMap(_.asElement).get
      var infoDef = infoNode.definition
      runAttributeCreationTest1(project, project => {
        Some(project.rootASTUnit.rootNode.elements("info").head)
      },"termsOfService","https://example.com/tos")
    })
  }

  test("InfoObject license creation JSON"){
    var fp = "SwaggerObject/SwaggerObjectEmpty.json"
    parse(filePath(fp)).flatMap(project=>{
      var apiNode = project.rootASTUnit.rootNode
      var apiDef = apiNode.definition
      var infoNode = apiNode.newChild(apiDef.property("info").get).flatMap(_.asElement).get
      var infoDef = infoNode.definition
      var licenseNode = infoNode.newChild(infoDef.property("license").get).flatMap(_.asElement).get
      var licenseDef = licenseNode.definition
      runAttributeCreationTest1(project, project => {
        Some(project.rootASTUnit.rootNode.elements("info").head.elements("license").head)
      },"name","CC")
    })
  }

  test("InfoObject contact creation JSON"){
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
}
