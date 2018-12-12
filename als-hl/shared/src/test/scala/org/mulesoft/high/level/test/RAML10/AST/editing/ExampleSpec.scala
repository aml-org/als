package org.mulesoft.high.level.test.RAML10.AST.editing

import org.mulesoft.high.level.test.RAML10.RAML10ASTEditingTest

class ExampleSpec extends RAML10ASTEditingTest{
//  test("ExampleSpec value editing"){
//    runAttributeEditingTest( "ExampleSpec/ExampleSpec.raml", project => {
//      project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("queryString").head.elements("examples").head.attribute("value")
//    }, "rts")
//  }

  test("ExampleSpec strict editing"){
    runAttributeEditingTest( "ExampleSpec/ExampleSpec.raml", project => {
      project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("queryString").head.elements("examples").head.attribute("strict")
    }, false)
  }

  test("ExampleSpec name editing"){
    runAttributeEditingTest( "ExampleSpec/ExampleSpec.raml", project => {
      project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("queryString").head.elements("examples").head.attribute("name")
    }, "second")
  }

  test("ExampleSpec displayName editing"){
    runAttributeEditingTest( "ExampleSpec/ExampleSpec.raml", project => {
      project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("queryString").head.elements("examples").head.attribute("displayName")
    }, "dn")
  }

  test("ExampleSpec description editing"){
    runAttributeEditingTest( "ExampleSpec/ExampleSpec.raml", project => {
      project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("queryString").head.elements("examples").head.attribute("description")
    }, "dtn")
  }
}
