package org.mulesoft.high.level.test.OAS20.AST.editing

import org.mulesoft.high.level.test.OAS20.OAS20ASTEditingTest

class OperationObject extends OAS20ASTEditingTest{

    test("Operation Object 'method' editing. YAML"){
        runAttributeEditingTest("OperationObject/OperationObject.yml", project => {
            project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.attribute("method")
        }, "options")
    }

    test("Operation Object 'method' editing. JSON"){
        runAttributeEditingTest("OperationObject/OperationObject.json", project => {
            project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.attribute("method")
        }, "options")
    }
}
