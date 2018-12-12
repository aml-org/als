package org.mulesoft.high.level.test.RAML10.AST.editing

import org.mulesoft.high.level.test.RAML10.RAML10ASTEditingTest

class DocumentationItem extends RAML10ASTEditingTest{

  test("DocumentationItem title editing") {
    runAttributeEditingTest("DocumentationItem/api.raml", project => {
      project.rootASTUnit.rootNode.elements("documentation").head.attribute("title")
    }, "A")
  }

  test("DocumentationItem content editing") {
    runAttributeEditingTest("DocumentationItem/api.raml", project => {
      project.rootASTUnit.rootNode.elements("documentation").head.attribute("content")
    }, "B")
  }

  test("DocumentationItem title creation") {
//    runAttributeEditingTest("DocumentationItem/apiCreation.raml", project => {
//      project.rootASTUnit.rootNode.elements("documentation").head.attribute("title")
//    }, "A")
    var fp = "Api/api_empty.raml"
    parse(filePath(fp)).flatMap(project=>{
      var apiNode = project.rootASTUnit.rootNode
      var apiDef = apiNode.definition
      var typeNode = apiNode.newChild(apiDef.property("documentation").get).flatMap(_.asElement).get
      runAttributeCreationTest1(project, project => {
        Some(project.rootASTUnit.rootNode.elements("documentation").head)
      },"title","Title")
    })
  }

  test("DocumentationItem content creation") {
    var fp = "Api/api_empty.raml"
    parse(filePath(fp)).flatMap(project=>{
      var apiNode = project.rootASTUnit.rootNode
      var apiDef = apiNode.definition
      var typeNode = apiNode.newChild(apiDef.property("documentation").get).flatMap(_.asElement).get
      runAttributeCreationTest1(project, project => {
        Some(project.rootASTUnit.rootNode.elements("documentation").head)
      },"content","Content")
    })
  }
}
