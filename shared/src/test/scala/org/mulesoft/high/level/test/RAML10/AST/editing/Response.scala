package org.mulesoft.high.level.test.RAML10.AST.editing

import org.mulesoft.high.level.test.RAML10.RAML10ASTEditingTest

class Response extends RAML10ASTEditingTest {

    test("Response 'code' editing") {
        runAttributeEditingTest("Response/response_code.raml", project => {
            project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("responses")(1).attribute("code")
        }, "404")
    }

}
