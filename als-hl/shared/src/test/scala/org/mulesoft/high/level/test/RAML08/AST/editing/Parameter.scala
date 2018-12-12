package org.mulesoft.high.level.test.RAML08.AST.editing

import org.mulesoft.high.level.test.RAML08.RAML08ASTEditingTest

class Parameter extends RAML08ASTEditingTest {

    test("Parameter 'type' editing 1") {
        runAttributeEditingTest("Parameter/parameter1.raml", project => {
            Option(project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("queryParameters").head.attribute("type").get)
        }, "boolean")
    }

    test("Parameter 'type' editing 2") {
        runAttributeEditingTest("Parameter/parameter1.raml", project => {
            Option(project.rootASTUnit.rootNode.elements("resources").head.elements("methods")(1).elements("queryParameters").head.attribute("type").get)
        }, "string")
    }

//    test("Parameter 'required' editing 1") {
//        runAttributeCreationTest("Parameter/parameter1.raml", project => {
//            Option(project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("queryParameters").head)
//        }, "repeat", true)
//    }
//
//    test("Parameter 'required' editing 2") {
//        runAttributeCreationTest("Parameter/parameter1.raml", project => {
//            Option(project.rootASTUnit.rootNode.elements("resources").head.elements("methods")(1).elements("queryParameters").head)
//        }, "repeat", false)
//    }
}
