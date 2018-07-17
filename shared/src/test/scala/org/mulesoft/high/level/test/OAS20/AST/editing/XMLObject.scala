package org.mulesoft.high.level.test.OAS20.AST.editing

import org.mulesoft.high.level.test.OAS20.OAS20ASTEditingTest

class XMLObject extends OAS20ASTEditingTest{
  test("XMLObject attribute editing. YAML"){
    runAttributeEditingTest( "SchemaObject/SchemaObject.yml", project => {
      project.rootASTUnit.rootNode.elements("definitions").head.elements("xml").head.attribute("attribute")
    }, false)
  }

  test("XMLObject wrapped editing. YAML"){
    runAttributeEditingTest( "SchemaObject/SchemaObject.yml", project => {
      project.rootASTUnit.rootNode.elements("definitions").head.elements("xml").head.attribute("wrapped")
    }, false)
  }

//  test("XMLObject name editing. YAML"){
//    runAttributeEditingTest( "SchemaObject/SchemaObject.yml", project => {
//      project.rootASTUnit.rootNode.elements("definitions").head.elements("xml").head.attribute("name")
//    }, "xmlname")
//  }

  test("XMLObject namespace editing. YAML"){
    runAttributeEditingTest( "SchemaObject/SchemaObject.yml", project => {
      project.rootASTUnit.rootNode.elements("definitions").head.elements("xml").head.attribute("namespace")
    }, "aero")
  }

  test("XMLObject prefix editing. YAML"){
    runAttributeEditingTest( "SchemaObject/SchemaObject.yml", project => {
      project.rootASTUnit.rootNode.elements("definitions").head.elements("xml").head.attribute("prefix")
    }, "aero")
  }

  test("XMLObject attribute editing. JSON"){
    runAttributeEditingTest( "SchemaObject/SchemaObject.json", project => {
      project.rootASTUnit.rootNode.elements("definitions").head.elements("xml").head.attribute("attribute")
    }, false)
  }

  test("XMLObject wrapped editing. JSON"){
    runAttributeEditingTest( "SchemaObject/SchemaObject.json", project => {
      project.rootASTUnit.rootNode.elements("definitions").head.elements("xml").head.attribute("wrapped")
    }, false)
  }

//  test("XMLObject name editing. JSON"){
//    runAttributeEditingTest( "SchemaObject/SchemaObject.json", project => {
//      project.rootASTUnit.rootNode.elements("definitions").head.elements("xml").head.attribute("name")
//    }, "xmlname")
//  }

  test("XMLObject namespace editing. JSON"){
    runAttributeEditingTest( "SchemaObject/SchemaObject.json", project => {
      project.rootASTUnit.rootNode.elements("definitions").head.elements("xml").head.attribute("namespace")
    }, "aero")
  }

  test("XMLObject prefix editing. JSON"){
    runAttributeEditingTest( "SchemaObject/SchemaObject.json", project => {
      project.rootASTUnit.rootNode.elements("definitions").head.elements("xml").head.attribute("prefix")
    }, "aero")
  }

//  test("XMLObject attribute creation. YAML"){
//    var fp = "SchemaObject/SchemaObjectEmptyDef.yml"
//    parse(filePath(fp)).flatMap(project=>{
//      var objectNode = project.rootASTUnit.rootNode.elements("definitions").head
//      var objectDef = objectNode.definition
//      var xmlNode = objectNode.newChild(objectDef.property("xml").get).flatMap(_.asElement).get
//      var xmlDef = xmlNode.definition
//      runAttributeCreationTest1(project, project => {
//        Some(project.rootASTUnit.rootNode.elements("definitions").head.elements("xml").head)
//      },"attribute", true)
//    })
//  }
//
//  test("XMLObject wrapped creation. YAML"){
//    var fp = "SchemaObject/SchemaObjectEmptyDef.yml"
//    parse(filePath(fp)).flatMap(project=>{
//      var objectNode = project.rootASTUnit.rootNode.elements("definitions").head
//      var objectDef = objectNode.definition
//      var xmlNode = objectNode.newChild(objectDef.property("xml").get).flatMap(_.asElement).get
//      var xmlDef = xmlNode.definition
//      runAttributeCreationTest1(project, project => {
//        Some(project.rootASTUnit.rootNode.elements("definitions").head.elements("xml").head)
//      },"wrapped", true)
//    })
//  }
//
//  test("XMLObject name creation. YAML"){
//    var fp = "SchemaObject/SchemaObjectEmptyDef.yml"
//    parse(filePath(fp)).flatMap(project=>{
//      var objectNode = project.rootASTUnit.rootNode.elements("definitions").head
//      var objectDef = objectNode.definition
//      var xmlNode = objectNode.newChild(objectDef.property("xml").get).flatMap(_.asElement).get
//      var xmlDef = xmlNode.definition
//      runAttributeCreationTest1(project, project => {
//        Some(project.rootASTUnit.rootNode.elements("definitions").head.elements("xml").head)
//      },"name", "xmlname")
//    })
//  }

  test("XMLObject namespace creation. YAML"){
    var fp = "SchemaObject/SchemaObjectEmptyDef.yml"
    parse(filePath(fp)).flatMap(project=>{
      var objectNode = project.rootASTUnit.rootNode.elements("definitions").head
      var objectDef = objectNode.definition
      var xmlNode = objectNode.newChild(objectDef.property("xml").get).flatMap(_.asElement).get
      var xmlDef = xmlNode.definition
      runAttributeCreationTest1(project, project => {
        Some(project.rootASTUnit.rootNode.elements("definitions").head.elements("xml").head)
      },"namespace", "aero")
    })
  }

  test("XMLObject prefix creation. YAML"){
    var fp = "SchemaObject/SchemaObjectEmptyDef.yml"
    parse(filePath(fp)).flatMap(project=>{
      var objectNode = project.rootASTUnit.rootNode.elements("definitions").head
      var objectDef = objectNode.definition
      var xmlNode = objectNode.newChild(objectDef.property("xml").get).flatMap(_.asElement).get
      var xmlDef = xmlNode.definition
      runAttributeCreationTest1(project, project => {
        Some(project.rootASTUnit.rootNode.elements("definitions").head.elements("xml").head)
      },"prefix", "aero")
    })
  }

//  test("XMLObject attribute creation. JSON"){
//    var fp = "SchemaObject/SchemaObjectEmptyDef.json"
//    parse(filePath(fp)).flatMap(project=>{
//      var objectNode = project.rootASTUnit.rootNode.elements("definitions").head
//      var objectDef = objectNode.definition
//      var xmlNode = objectNode.newChild(objectDef.property("xml").get).flatMap(_.asElement).get
//      var xmlDef = xmlNode.definition
//      runAttributeCreationTest1(project, project => {
//        Some(project.rootASTUnit.rootNode.elements("definitions").head.elements("xml").head)
//      },"attribute", true)
//    })
//  }
//
//  test("XMLObject wrapped creation. JSON"){
//    var fp = "SchemaObject/SchemaObjectEmptyDef.json"
//    parse(filePath(fp)).flatMap(project=>{
//      var objectNode = project.rootASTUnit.rootNode.elements("definitions").head
//      var objectDef = objectNode.definition
//      var xmlNode = objectNode.newChild(objectDef.property("xml").get).flatMap(_.asElement).get
//      var xmlDef = xmlNode.definition
//      runAttributeCreationTest1(project, project => {
//        Some(project.rootASTUnit.rootNode.elements("definitions").head.elements("xml").head)
//      },"wrapped", true)
//    })
//  }
//
//  test("XMLObject name creation. JSON"){
//    var fp = "SchemaObject/SchemaObjectEmptyDef.json"
//    parse(filePath(fp)).flatMap(project=>{
//      var objectNode = project.rootASTUnit.rootNode.elements("definitions").head
//      var objectDef = objectNode.definition
//      var xmlNode = objectNode.newChild(objectDef.property("xml").get).flatMap(_.asElement).get
//      var xmlDef = xmlNode.definition
//      runAttributeCreationTest1(project, project => {
//        Some(project.rootASTUnit.rootNode.elements("definitions").head.elements("xml").head)
//      },"name", "xmlname")
//    })
//  }

  test("XMLObject namespace creation. JSON"){
    var fp = "SchemaObject/SchemaObjectEmptyDef.json"
    parse(filePath(fp)).flatMap(project=>{
      var objectNode = project.rootASTUnit.rootNode.elements("definitions").head
      var objectDef = objectNode.definition
      var xmlNode = objectNode.newChild(objectDef.property("xml").get).flatMap(_.asElement).get
      var xmlDef = xmlNode.definition
      runAttributeCreationTest1(project, project => {
        Some(project.rootASTUnit.rootNode.elements("definitions").head.elements("xml").head)
      },"namespace", "aero")
    })
  }

  test("XMLObject prefix creation. JSON"){
    var fp = "SchemaObject/SchemaObjectEmptyDef.json"
    parse(filePath(fp)).flatMap(project=>{
      var objectNode = project.rootASTUnit.rootNode.elements("definitions").head
      var objectDef = objectNode.definition
      var xmlNode = objectNode.newChild(objectDef.property("xml").get).flatMap(_.asElement).get
      var xmlDef = xmlNode.definition
      runAttributeCreationTest1(project, project => {
        Some(project.rootASTUnit.rootNode.elements("definitions").head.elements("xml").head)
      },"prefix", "aero")
    })
  }
}
