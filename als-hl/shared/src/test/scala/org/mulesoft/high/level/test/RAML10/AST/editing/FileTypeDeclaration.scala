package org.mulesoft.high.level.test.RAML10.AST.editing

import org.mulesoft.high.level.test.RAML10.RAML10ASTEditingTest

class FileTypeDeclaration extends RAML10ASTEditingTest{
//  test("FileTypeDeclaration fileTypes editing"){
//    runAttributeEditingTest( "FileTypeDeclaration/fileTypeDeclarationRoot.raml", project => {
//      project.rootASTUnit.rootNode.elements("types").head.attributes("fileTypes").headOption
//    }, "rar")
//  }

  test("FileTypeDeclaration minLength editing") {
    runAttributeEditingTest("FileTypeDeclaration/fileTypeDeclarationRoot.raml", project => {
      project.rootASTUnit.rootNode.elements("types").head.attribute("minLength")
    }, 500)
  }

  test("FileTypeDeclaration maxLength editing") {
    runAttributeEditingTest("FileTypeDeclaration/fileTypeDeclarationRoot.raml", project => {
      project.rootASTUnit.rootNode.elements("types").head.attribute("maxLength")
    }, 2000)
  }

//  test("FileTypeDeclaration fileTypes creating"){
//    var fp = "FileTypeDeclaration/fileTypeDeclarationRootEmpty.raml"
//    parse(filePath(fp)).flatMap(project=>{
//      var typeNode = project.rootASTUnit.rootNode.elements("types").head
//      var typeDef = typeNode.definition
//      var fileTypeNode = typeNode.newChild(typeDef.property("fileTypes").get).flatMap(_.asAttr).get
//      var fileTypeDef = fileTypeNode.definition
//      runAttributeCreationTest1(project, project => {
//        Some(project.rootASTUnit.rootNode.elements("types").head)
//      },"fileTypes","rar")
//    })
//  }

  test("FileTypeDeclaration minLength creating") {
    runAttributeCreationTest("FileTypeDeclaration/fileTypeDeclarationRootEmpty.raml", project => {
      Option(project.rootASTUnit.rootNode.elements("types").head)
    }, "minLength", 500)
  }

  test("FileTypeDeclaration maxLength creating") {
    runAttributeCreationTest("FileTypeDeclaration/fileTypeDeclarationRootEmpty.raml", project => {
      Option(project.rootASTUnit.rootNode.elements("types").head)
    }, "maxLength", 2000)
  }
}
