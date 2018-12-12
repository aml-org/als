package org.mulesoft.high.level.test.RAML10.AST.editing

import org.mulesoft.high.level.test.RAML10.RAML10ASTEditingTest

class Overlay extends RAML10ASTEditingTest{
  test("Overlay usage editing"){
    runAttributeEditingTest( "Overlay/overlay.raml", project => {
      project.rootASTUnit.rootNode.attribute("usage")
    }, "abc")
  }

//  test("Overlay extends editing"){
//    runAttributeEditingTest( "Overlay/overlay.raml", project => {
//      project.rootASTUnit.rootNode.attribute("extends")
//    }, "api2.raml")
//  }
}
