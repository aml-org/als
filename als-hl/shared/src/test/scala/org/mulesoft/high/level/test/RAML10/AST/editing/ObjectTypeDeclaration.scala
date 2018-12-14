package org.mulesoft.high.level.test.RAML10.AST.editing

import org.mulesoft.high.level.test.RAML10.RAML10ASTEditingTest

class ObjectTypeDeclaration extends RAML10ASTEditingTest{

  test("ObjectTypeDeclaration properties editing"){
    runAttributeEditingTest( "ObjectTypeDeclaration/objectTypeDeclarationRoot.raml", project => {
      project.rootASTUnit.rootNode.elements("types").head.elements("properties").head.attribute("name")
    }, "plane")
  }

  test("ObjectTypeDeclaration minProperties editing") {
    runAttributeEditingTest("ObjectTypeDeclaration/objectTypeDeclarationRoot.raml", project => {
      project.rootASTUnit.rootNode.elements("types").head.attribute("minProperties")
    }, 0)
  }

  test("ObjectTypeDeclaration maxProperties editing") {
    runAttributeEditingTest("ObjectTypeDeclaration/objectTypeDeclarationRoot.raml", project => {
      project.rootASTUnit.rootNode.elements("types").head.attribute("maxProperties")

    }, 5)
  }

  test("ObjectTypeDeclaration additionalProperties editing") {
    runAttributeEditingTest("ObjectTypeDeclaration/objectTypeDeclarationRoot.raml", project => {
      project.rootASTUnit.rootNode.elements("types").head.attribute("additionalProperties")

    }, true)
  }

  test("ObjectTypeDeclaration discriminator editing"){
    runAttributeEditingTest( "ObjectTypeDeclaration/objectTypeDeclarationRoot.raml", project => {
      project.rootASTUnit.rootNode.elements("types").head.attribute("discriminator")
    }, "length")
  }

  test("ObjectTypeDeclaration discriminatorValue editing") {
    runAttributeEditingTest("ObjectTypeDeclaration/objectTypeDeclarationRoot.raml", project => {
      project.rootASTUnit.rootNode.elements("types").head.attribute("discriminatorValue")
    }, "abc")
  }

//  test("ObjectTypeDeclaration properties creating"){
//    var fp = "ObjectTypeDeclaration/objectTypeDeclarationRootEmpty.raml"
//    parse(filePath(fp)).flatMap(project=> {
//      var apiNode = project.rootASTUnit.rootNode
//      var apiDef = apiNode.definition
//      var typeNode = apiNode.newChild(apiDef.property("types").get).flatMap(_.asElement).get
//      var typeDef = typeNode.definition
//      var typeNameAttr = typeNode.newChild(typeDef.property("name").get).flatMap(_.asAttr).get
//      typeNameAttr.setValue("engine").map(_ => typeNode)
//    }).flatMap(typeNode=>{
//      var typeDef = typeNode.definition
//      var project = typeNode.astUnit.project
//      var propertyNode = typeNode.newChild(typeDef.property("properties").get).flatMap(_.asElement).get
//      var propertyDef = propertyNode.definition
//      runAttributeCreationTest1(project, project => {
//        Some(project.rootASTUnit.rootNode.elements("types").head.elements("properties").head)
//      },"name","string")
//    })
//  }

//  test("ObjectTypeDeclaration properties creating"){
//    var fp = "ObjectTypeDeclaration/objectTypeDeclarationRootEmpty.raml"
//    parse(filePath(fp)).flatMap(project=> {
//      var apiNode = project.rootASTUnit.rootNode
//      var apiDef = apiNode.definition
//      var typeNode = apiNode.newChild(apiDef.property("types").get).flatMap(_.asElement).get
//      var typeDef = typeNode.definition
//      var typeNameAttr = typeNode.newChild(typeDef.property("name").get).flatMap(_.asAttr).get
//      typeNameAttr.setValue("engine").map(_ => typeNode)
//    }).flatMap(typeNode=>{
//      var typeDef = typeNode.definition
//      var project = typeNode.astUnit.project
//      var propertyNode = typeNode.newChild(typeDef.property("properties").get).flatMap(_.asElement).get
//      runAttributeCreationTest1(project, project => {
//        Some(project.rootASTUnit.rootNode.elements("types").head.elements("properties").head)
//      },"name","string")
//    })
//  }

  test("ObjectTypeDeclaration minProperties creating") {
    var fp = "Api/api_empty.raml"
    parse(filePath(fp)).flatMap(project=> {
      var apiNode = project.rootASTUnit.rootNode
      var apiDef = apiNode.definition
      var typeNode = apiNode.newChild(apiDef.property("types").get).flatMap(_.asElement).get
      runAttributeCreationTest("ObjectTypeDeclaration/objectTypeDeclarationRootEmpty.raml", project => {
        Option(project.rootASTUnit.rootNode.elements("types").head)
      }, "minProperties", 2)
    })
  }

  test("ObjectTypeDeclaration maxProperties creating") {
    runAttributeCreationTest( "ObjectTypeDeclaration/objectTypeDeclarationRootEmpty.raml", project => {
            Option(project.rootASTUnit.rootNode.elements("types").head)
        }, "maxProperties",  5)
  }

//  test("ObjectTypeDeclaration additionalProperties creating") {
//    runAttributeCreationTest( "ObjectTypeDeclaration/objectTypeDeclarationRootEmpty.raml", project => {
//            Option(project.rootASTUnit.rootNode.elements("types").head)
//        }, "additionalProperties", true)
//  }

  test("ObjectTypeDeclaration discriminator creating"){
    runAttributeCreationTest( "ObjectTypeDeclaration/objectTypeDeclarationRootEmpty.raml", project => {
            Option(project.rootASTUnit.rootNode.elements("types").head)
        }, "discriminator",  "length")
  }

  test("ObjectTypeDeclaration discriminatorValue creating") {
    runAttributeCreationTest( "ObjectTypeDeclaration/objectTypeDeclarationRootEmpty.raml", project => {
            Option(project.rootASTUnit.rootNode.elements("types").head)
        }, "discriminatorValue",  "abc")
  }
}
