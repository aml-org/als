package org.mulesoft.high.level.test.RAML10.AST.editing

import org.mulesoft.high.level.test.RAML10.RAML10ASTEditingTest

class Library extends RAML10ASTEditingTest{

  test("Library usage editing"){
    runAttributeEditingTest( "Library/Library.raml", project => {
      project.rootASTUnit.rootNode.attribute("usage")
    }, "pl")
  }

  test("Library usage creation"){
    runAttributeCreationTest("Library/LibraryEmpty.raml", project => {
      Option(project.rootASTUnit.rootNode)
    }, "usage", "lp")
  }
}
