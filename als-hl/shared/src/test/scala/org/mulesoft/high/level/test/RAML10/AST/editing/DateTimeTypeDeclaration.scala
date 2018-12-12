package org.mulesoft.high.level.test.RAML10.AST.editing

import org.mulesoft.high.level.test.RAML10.RAML10ASTEditingTest

class DateTimeTypeDeclaration extends RAML10ASTEditingTest{
  test("DateTimeTypeDeclaration format editing") {
    runAttributeEditingTest("DateTimeTypeDeclaration/api.raml", project => {
      project.rootASTUnit.rootNode.elements("types").head.attribute("format")
    }, "rfc3339")
  }

//  test("DateTimeTypeDeclaration format creating 1") {
//    var fp = "Api/api_empty.raml"
//    parse(filePath(fp)).flatMap(project=>{
//      var apiNode = project.rootASTUnit.rootNode
//      var apiDef = apiNode.definition
//      var typeNode = apiNode.newChild(apiDef.property("types").get).flatMap(_.asElement).get
//      var typeDef = typeNode.definition
//      var typeTypeAttr = typeNode.newChild(typeDef.property("type").get).flatMap(_.asAttr).get
//      typeTypeAttr.setValue("datetime").map(_ => typeNode)
//      runAttributeCreationTest1(project, project => {
//        Some(project.rootASTUnit.rootNode.elements("types").head)
//      },"format","rfc3339")
//    })
//  }

  test("DateTimeTypeDeclaration format creating 2") {
    runAttributeCreationTest("DateTimeTypeDeclaration/apiEmpty.raml", project => {
      Some(project.rootASTUnit.rootNode.elements("types").head)
    },"format","rfc3339")
  }
}
