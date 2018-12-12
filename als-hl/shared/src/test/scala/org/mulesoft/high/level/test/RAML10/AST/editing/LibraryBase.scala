package org.mulesoft.high.level.test.RAML10.AST.editing

import amf.core.metamodel.domain.templates.AbstractDeclarationModel
import amf.core.model.domain.ObjectNode
import org.mulesoft.high.level.test.RAML10.RAML10ASTEditingTest

class LibraryBase extends RAML10ASTEditingTest{

  test("LibraryBase types editing") {
    runAttributeEditingTest("LibraryBase/api.raml", project => {
      project.rootASTUnit.rootNode.elements("types").head.attribute("name")
    }, "tp1")
  }

  test("LibraryBase type creation") {
    var fp = "Api/api_empty.raml"
    parse(filePath(fp)).flatMap(project=>{
      var apiNode = project.rootASTUnit.rootNode
      var apiDef = apiNode.definition
      var typeNode = apiNode.newChild(apiDef.property("types").get).flatMap(_.asElement).get
      runAttributeCreationTest1(project, project => {
        Some(project.rootASTUnit.rootNode.elements("types").head)
      },"name","type1")
    })
  }

  test("LibraryBase traits editing") {
    runAttributeEditingTest("LibraryBase/api.raml", project => {
      project.rootASTUnit.rootNode.elements("traits").head.attribute("name")
    }, "main")
  }

  test("LibraryBase trait creation") {
    var fp = "Api/api_empty.raml"
    parse(filePath(fp)).flatMap(project=>{
      var apiNode = project.rootASTUnit.rootNode
      var apiDef = apiNode.definition
      var traitNode = apiNode.newChild(apiDef.property("traits").get).flatMap(_.asElement).get

      traitNode.amfNode.fields.setWithoutId(AbstractDeclarationModel.DataNode,ObjectNode())

      var traitDef = traitNode.definition

      runAttributeCreationTest1(project, project => {
        Some(project.rootASTUnit.rootNode.elements("traits").head)
      },"name","trait1")
    })
  }

  test("LibraryBase resourceTypes editing") {
    runAttributeEditingTest("LibraryBase/api.raml", project => {
      project.rootASTUnit.rootNode.elements("resourceTypes").head.attribute("name")
    }, "rt")
  }

  test("LibraryBase resourceType creation") {
    var fp = "Api/api_empty.raml"
    parse(filePath(fp)).flatMap(project=>{
      var apiNode = project.rootASTUnit.rootNode
      var apiDef = apiNode.definition
      var resourceTypeNode = apiNode.newChild(apiDef.property("resourceTypes").get).flatMap(_.asElement).get

      resourceTypeNode.amfNode.fields.setWithoutId(AbstractDeclarationModel.DataNode,ObjectNode())

      var traitDef = resourceTypeNode.definition

      runAttributeCreationTest1(project, project => {
        Some(project.rootASTUnit.rootNode.elements("resourceTypes").head)
      },"name","resourceType1")
    })
  }

  test("LibraryBase annotationTypes editing") {
    runAttributeEditingTest("LibraryBase/api.raml", project => {
      project.rootASTUnit.rootNode.elements("annotationTypes").head.attribute("name")
    }, "iH")
  }

  test("LibraryBase annotationType creation") {
    var fp = "Api/api_empty.raml"
    parse(filePath(fp)).flatMap(project=>{
      var apiNode = project.rootASTUnit.rootNode
      var apiDef = apiNode.definition
      var annotationTypeNode = apiNode.newChild(apiDef.property("annotationTypes").get).flatMap(_.asElement).get

      annotationTypeNode.amfNode.fields.setWithoutId(AbstractDeclarationModel.DataNode,ObjectNode())

      var annotationDef = annotationTypeNode.definition

      runAttributeCreationTest1(project, project => {
        Some(project.rootASTUnit.rootNode.elements("annotationTypes").head)
      },"name","annotationType1")
    })
  }

  test("LibraryBase securitySchemes editing") {
    runAttributeEditingTest("LibraryBase/api.raml", project => {
      project.rootASTUnit.rootNode.elements("securitySchemes").head.attribute("name")
    },"oa2")
  }

  test("LibraryBase securityScheme creation") {
    var fp = "Api/api_empty.raml"
    parse(filePath(fp)).flatMap(project=>{
      var apiNode = project.rootASTUnit.rootNode
      var apiDef = apiNode.definition
      var securitySchemeNode = apiNode.newChild(apiDef.property("securitySchemes").get).flatMap(_.asElement).get

      securitySchemeNode.amfNode.fields.setWithoutId(AbstractDeclarationModel.DataNode,ObjectNode())

      var securitySchemeDef = securitySchemeNode.definition

      runAttributeCreationTest1(project, project => {
        Some(project.rootASTUnit.rootNode.elements("securitySchemes").head)
      },"name","oauth2")
    })
  }
}
