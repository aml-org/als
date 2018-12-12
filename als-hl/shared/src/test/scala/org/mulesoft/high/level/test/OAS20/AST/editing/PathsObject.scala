package org.mulesoft.high.level.test.OAS20.AST.editing

import org.mulesoft.high.level.test.OAS20.OAS20ASTEditingTest

class PathsObject extends OAS20ASTEditingTest{
  test("PathsObject paths editing YAML") {
    runAttributeEditingTest("PathObject/PathObject.yml", project => {
      project.rootASTUnit.rootNode.elements("paths").head.elements("paths").head.attribute("path")
    }, "/pets")
  }

  test("PathsObject paths editing JSON") {
    runAttributeEditingTest("PathObject/PathObject.json", project => {
      project.rootASTUnit.rootNode.elements("paths").head.elements("paths").head.attribute("path")
    }, "/pets")
  }

  test("PathsObject paths creation YAML") {
    var fp = "SwaggerObject/SwaggerObjectEmpty.yml"
    parse(filePath(fp)).flatMap(project=>{
      var apiNode = project.rootASTUnit.rootNode
      var apiDef = apiNode.definition
      var paths1Node = apiNode.newChild(apiDef.property("paths").get).flatMap(_.asElement).get
      var paths1Def = paths1Node.definition
      var paths2Node = paths1Node.newChild(paths1Def.property("paths").get).flatMap(_.asElement).get
      var paths2Def = paths2Node.definition
      runAttributeCreationTest1(project, project => {
        Some(project.rootASTUnit.rootNode.elements("paths").head.elements("paths").head)
      },"path","/pets")
    })
  }

  test("PathsObject paths creation JSON") {
    var fp = "SwaggerObject/SwaggerObjectEmpty.json"
    parse(filePath(fp)).flatMap(project=>{
      var apiNode = project.rootASTUnit.rootNode
      var apiDef = apiNode.definition
      var paths1Node = apiNode.newChild(apiDef.property("paths").get).flatMap(_.asElement).get
      var paths1Def = paths1Node.definition
      var paths2Node = paths1Node.newChild(paths1Def.property("paths").get).flatMap(_.asElement).get
      var paths2Def = paths2Node.definition
      runAttributeCreationTest1(project, project => {
        Some(project.rootASTUnit.rootNode.elements("paths").head.elements("paths").head)
      },"path","/pets")
    })
  }
}
