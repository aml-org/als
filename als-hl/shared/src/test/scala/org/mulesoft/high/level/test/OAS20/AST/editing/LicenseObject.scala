package org.mulesoft.high.level.test.OAS20.AST.editing

import org.mulesoft.high.level.test.OAS20.OAS20ASTEditingTest

class LicenseObject extends OAS20ASTEditingTest{

  test("LicenseObject name editing YAML"){
    runAttributeEditingTest( "InfoObject/InfoObject.yml", project => {
      project.rootASTUnit.rootNode.elements("info").head.elements("license").head.attribute("name")
    }, "CC")
  }

  test("LicenseObject url editing YAML"){
    runAttributeEditingTest( "InfoObject/InfoObject.yml", project => {
      project.rootASTUnit.rootNode.elements("info").head.elements("license").head.attribute("url")
    }, "https://example.com/licence")
  }

  test("LicenseObject name editing JSON"){
    runAttributeEditingTest( "InfoObject/InfoObject.json", project => {
      project.rootASTUnit.rootNode.elements("info").head.elements("license").head.attribute("name")
    }, "CC")
  }

  test("LicenseObject url editing JSON"){
    runAttributeEditingTest( "InfoObject/InfoObject.json", project => {
      project.rootASTUnit.rootNode.elements("info").head.elements("license").head.attribute("url")
    }, "https://example.com/licence")
  }

  test("LicenseObject name creation YAML"){
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

  test("LicenseObject url creation YAML"){
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
      },"url","https://example.com/licence")
    })
  }

  test("LicenseObject name creation JSON"){
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

  test("LicenseObject url creation JSON"){
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
      },"url","https://example.com/licence")
    })
  }
}
