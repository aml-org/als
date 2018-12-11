package org.mulesoft.high.level.test.RAML10.AST.editing

import org.mulesoft.high.level.test.RAML10.RAML10ASTEditingTest

class XMLFacetInfo extends RAML10ASTEditingTest{

  test("XMLFacetInfo attribute editing"){
    runAttributeEditingTest( "XMLFacetInfo/XMLFacetInfo.raml", project => {
      project.rootASTUnit.rootNode.elements("types").head.elements("xml").head.attribute("attribute")
    }, false)
  }

  test("XMLFacetInfo wrapped editing"){
    runAttributeEditingTest( "XMLFacetInfo/XMLFacetInfo.raml", project => {
      project.rootASTUnit.rootNode.elements("types").head.elements("xml").head.attribute("wrapped")
    }, false)
  }

//  test("XMLFacetInfo name editing"){
//    runAttributeEditingTest( "XMLFacetInfo/XMLFacetInfo.raml", project => {
//      project.rootASTUnit.rootNode.elements("types").head.elements("xml").head.attribute("name")
//    }, "fn")
//  }

  test("XMLFacetInfo namespace editing"){
    runAttributeEditingTest( "XMLFacetInfo/XMLFacetInfo.raml", project => {
      project.rootASTUnit.rootNode.elements("types").head.elements("xml").head.attribute("namespace")
    }, "plane")
  }

  test("XMLFacetInfo prefix editing"){
    runAttributeEditingTest( "XMLFacetInfo/XMLFacetInfo.raml", project => {
      project.rootASTUnit.rootNode.elements("types").head.elements("xml").head.attribute("prefix")
    }, "plane")
  }
}
