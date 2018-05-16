package org.mulesoft.high.level.test.OAS20.AST.editing

import org.mulesoft.high.level.test.OAS20.OAS20ASTEditingTest

class ResponseObject extends OAS20ASTEditingTest{

    test("Response Definition Object 'key' editing. YAML"){
        runAttributeEditingTest("ResponseObject/ResponseObject.yml", project => {
            project.rootASTUnit.rootNode.elements("responses").head.attribute("key")
        }, "updatedResponseKeyValue")
    }

    test("Response Definition Object 'key' editing. JSON"){
        runAttributeEditingTest("ResponseObject/ResponseObject.json", project => {
            project.rootASTUnit.rootNode.elements("responses").head.attribute("key")
        }, "updatedResponseKeyValue")
    }

    test("Response Object 'code' editing. YAML"){
        runAttributeEditingTest("ResponseObject/ResponseObject.yml", project => {
            project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.element("responses").get.elements("responses").head.attribute("code")
        }, "201")
    }

    test("Response Object 'code' editing. JSON"){
        runAttributeEditingTest("ResponseObject/ResponseObject.json", project => {
            project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.element("responses").get.elements("responses").head.attribute("code")
        }, "201")
    }
}
