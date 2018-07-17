package org.mulesoft.high.level.test.RAML10.AST.editing

import org.mulesoft.high.level.test.RAML10.RAML10ASTEditingTest

class ArrayTypeDeclaration extends RAML10ASTEditingTest{
//  test("ArrayTypeDeclaration items editing") {
//    runAttributeEditingTest("ArrayTypeDeclaration/arrayTypeDeclarationRoot.raml", project => {
//      project.rootASTUnit.rootNode.elements("types").head.elements("items").head.attribute("name")
//    }, "number")
//  }

  test("ArrayTypeDeclaration uniqueItems editing") {
    runAttributeEditingTest("ArrayTypeDeclaration/arrayTypeDeclarationRoot.raml", project => {
      project.rootASTUnit.rootNode.elements("types").head.attribute("uniqueItems")
    }, true)
  }

  test("ArrayTypeDeclaration minItems editing") {
    runAttributeEditingTest("ArrayTypeDeclaration/arrayTypeDeclarationRoot.raml", project => {
      project.rootASTUnit.rootNode.elements("types").head.attribute("minItems")
    }, 1)
  }

  test("ArrayTypeDeclaration maxItems editing"){
    runAttributeEditingTest( "ArrayTypeDeclaration/arrayTypeDeclarationRoot.raml", project => {
      project.rootASTUnit.rootNode.elements("types").head.attribute("maxItems")
    }, 8)
  }
}
