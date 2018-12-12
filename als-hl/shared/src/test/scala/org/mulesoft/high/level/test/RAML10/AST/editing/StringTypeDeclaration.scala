package org.mulesoft.high.level.test.RAML10.AST.editing

import org.mulesoft.high.level.test.RAML10.RAML10ASTEditingTest

class StringTypeDeclaration extends RAML10ASTEditingTest{

  test("StringTypeDeclaration pattern"){
    runAttributeEditingTest( "StringTypeDeclaration/stringTypeDeclarationRoot.raml", project => {
      project.rootASTUnit.rootNode.elements("types").head.attribute("pattern")
    }, "^cba$")
  }

  test("StringTypeDeclaration minLength") {
    runAttributeEditingTest("StringTypeDeclaration/stringTypeDeclarationRoot.raml", project => {
      project.rootASTUnit.rootNode.elements("types").head.attribute("minLength")
    }, 1)
  }

  test("StringTypeDeclaration maxLength") {
    runAttributeEditingTest("StringTypeDeclaration/stringTypeDeclarationRoot.raml", project => {
      project.rootASTUnit.rootNode.elements("types").head.attribute("maxLength")
    }, 6)
  }

//  test("StringTypeDeclaration enum"){
//    runAttributeEditingTest( "StringTypeDeclaration/stringTypeDeclarationRoot.raml", project => {
//      project.rootASTUnit.rootNode.elements("types").head.attribute("enum")
//    }, "cba")
//  }
}
