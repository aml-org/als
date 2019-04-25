package org.mulesoft.high.level.test.OAS20.AST.editing

import org.mulesoft.high.level.test.OAS20.OAS20ASTEditingTest

class ExternalDocumentationObject extends OAS20ASTEditingTest {

  test("ExternalDocumentationObject url editing YAML") {
    runAttributeEditingTest("SwaggerObject/SwaggerObject.yml", project => {
      project.rootASTUnit.rootNode.elements("externalDocs").head.attribute("url")
    }, "https://swagger.com")
  }

  test("ExternalDocumentationObject description editing YAML") {
    runAttributeEditingTest("SwaggerObject/SwaggerObject.yml", project => {
      project.rootASTUnit.rootNode.elements("externalDocs").head.attribute("description")
    }, "text here")
  }

  test("ExternalDocumentationObject url editing JSON") {
    runAttributeEditingTest("SwaggerObject/SwaggerObject.json", project => {
      project.rootASTUnit.rootNode.elements("externalDocs").head.attribute("url")
    }, "https://swagger.com")
  }

  test("ExternalDocumentationObject description editing JSON") {
    runAttributeEditingTest("SwaggerObject/SwaggerObject.json", project => {
      project.rootASTUnit.rootNode.elements("externalDocs").head.attribute("description")
    }, "text here")
  }

  test("ExternalDocumentationObject url creation YAML") {
    var fp = "SwaggerObject/SwaggerObjectEmpty.yml"
    parse(filePath(fp)).flatMap(project => {
      var apiNode  = project.rootASTUnit.rootNode
      var apiDef   = apiNode.definition
      var infoNode = apiNode.newChild(apiDef.property("externalDocs").get).flatMap(_.asElement).get
      var infoDef  = infoNode.definition
      runAttributeCreationTest1(project, project => {
        Some(project.rootASTUnit.rootNode.elements("externalDocs").head)
      }, "url", "https://swagger.com")
    })
  }

  test("ExternalDocumentationObject description creation YAML") {
    var fp = "SwaggerObject/SwaggerObjectEmpty.yml"
    parse(filePath(fp)).flatMap(project => {
      var apiNode  = project.rootASTUnit.rootNode
      var apiDef   = apiNode.definition
      var infoNode = apiNode.newChild(apiDef.property("externalDocs").get).flatMap(_.asElement).get
      var infoDef  = infoNode.definition
      runAttributeCreationTest1(project, project => {
        Some(project.rootASTUnit.rootNode.elements("externalDocs").head)
      }, "description", "description text")
    })
  }

  test("ExternalDocumentationObject edding description creation YAML") {
    runAttributeEditingTest("SwaggerObject/SwaggerObject.yml", project => {
      project.rootASTUnit.rootNode.elements("externalDocs").head.attribute("description")
    }, "text here")
  }

  test("ExternalDocumentationObject url creation JSON") {
    var fp = "SwaggerObject/SwaggerObjectEmpty.json"
    parse(filePath(fp)).flatMap(project => {
      var apiNode  = project.rootASTUnit.rootNode
      var apiDef   = apiNode.definition
      var infoNode = apiNode.newChild(apiDef.property("externalDocs").get).flatMap(_.asElement).get
      var infoDef  = infoNode.definition
      runAttributeCreationTest1(project, project => {
        Some(project.rootASTUnit.rootNode.elements("externalDocs").head)
      }, "url", "https://swagger.com")
    })
  }

  test("ExternalDocumentationObject description creation JSON") {
    var fp = "SwaggerObject/SwaggerObjectEmpty.json"
    parse(filePath(fp)).flatMap(project => {
      var apiNode  = project.rootASTUnit.rootNode
      var apiDef   = apiNode.definition
      var infoNode = apiNode.newChild(apiDef.property("externalDocs").get).flatMap(_.asElement).get
      var infoDef  = infoNode.definition
      runAttributeCreationTest1(project, project => {
        Some(project.rootASTUnit.rootNode.elements("externalDocs").head)
      }, "description", "description text")
    })
  }
}
