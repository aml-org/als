package org.mulesoft.high.level.test.OAS20.AST.editing

import org.mulesoft.high.level.test.OAS20.OAS20ASTEditingTest

class ParameterObject extends OAS20ASTEditingTest{

    test("Parameter Definition Object 'key' editing. YAML"){
        runAttributeEditingTest("ParameterObject/ParameterObject.yml", project => {
            project.rootASTUnit.rootNode.elements("parameters").head.attribute("key")
        }, "updatedParameterKeyValue")
    }

    test("Parameter Definition Object 'key' editing. JSON"){
        runAttributeEditingTest("ParameterObject/ParameterObject.json", project => {
            project.rootASTUnit.rootNode.elements("parameters").head.attribute("key")
        }, "updatedParameterKeyValue")
    }

    test("Parameter Definition Object 'in' editing ('query'->'path'). YAML"){
        runAttributeEditingTest("ParameterObject/ParameterObject.yml", project => {
            project.rootASTUnit.rootNode.elements("parameters").head.attribute("in")
        }, "path")
    }

    test("Parameter Definition Object 'in' editing ('query'->'path'). JSON"){
        runAttributeEditingTest("ParameterObject/ParameterObject.json", project => {
            project.rootASTUnit.rootNode.elements("parameters").head.attribute("in")
        }, "path")
    }

    test("Parameter Definition Object 'in' editing ('query'->'header'). YAML"){
        runAttributeEditingTest("ParameterObject/ParameterObject.yml", project => {
            project.rootASTUnit.rootNode.elements("parameters").head.attribute("in")
        }, "header")
    }

    test("Parameter Definition Object 'in' editing ('query'->'header'). JSON"){
        runAttributeEditingTest("ParameterObject/ParameterObject.json", project => {
            project.rootASTUnit.rootNode.elements("parameters").head.attribute("in")
        }, "header")
    }

    test("Parameter Definition Object 'in' editing ('query'->'body'). YAML"){
        runAttributeEditingTest("ParameterObject/ParameterObject.yml", project => {
            project.rootASTUnit.rootNode.elements("parameters").head.attribute("in")
        }, "body")
    }

    test("Parameter Definition Object 'in' editing ('query'->'body'). JSON"){
        runAttributeEditingTest("ParameterObject/ParameterObject.json", project => {
            project.rootASTUnit.rootNode.elements("parameters").head.attribute("in")
        }, "body")
    }

    test("Parameter Definition Object 'in' editing ('query'->'formData'). YAML"){
        runAttributeEditingTest("ParameterObject/ParameterObject.yml", project => {
            project.rootASTUnit.rootNode.elements("parameters").head.attribute("in")
        }, "formData")
    }

    test("Parameter Definition Object 'in' editing ('query'->'formData'). JSON"){
        runAttributeEditingTest("ParameterObject/ParameterObject.json", project => {
            project.rootASTUnit.rootNode.elements("parameters").head.attribute("in")
        }, "formData")
    }

    test("Parameter Definition Object 'in' editing ('body'->'query'). YAML"){
        runAttributeEditingTest("ParameterObject/ParameterObject.yml", project => {
            project.rootASTUnit.rootNode.elements("parameters")(1).attribute("in")
        }, "query")
    }

    test("Parameter Definition Object 'in' editing ('body'->'query'). JSON"){
        runAttributeEditingTest("ParameterObject/ParameterObject.json", project => {
            project.rootASTUnit.rootNode.elements("parameters")(1).attribute("in")
        }, "query")
    }

    test("Parameter Object (located in path item) 'in' editing ('query'->'path'). YAML"){
        runAttributeEditingTest("ParameterObject/ParameterObject.yml", project => {
            project.rootASTUnit.rootNode.elements("paths").head.elements("paths").head.elements("parameters").head.attribute("in")
        }, "path")
    }

    test("Parameter Object (located in path item) 'in' editing ('query'->'path'). JSON"){
        runAttributeEditingTest("ParameterObject/ParameterObject.json", project => {
            project.rootASTUnit.rootNode.elements("paths").head.elements("paths").head.elements("parameters").head.attribute("in")
        }, "path")
    }

    test("Parameter Object (located in path item) 'in' editing ('query'->'header'). YAML"){
        runAttributeEditingTest("ParameterObject/ParameterObject.yml", project => {
            project.rootASTUnit.rootNode.elements("paths").head.elements("paths").head.elements("parameters").head.attribute("in")
        }, "header")
    }

    test("Parameter Object (located in path item) 'in' editing ('query'->'header'). JSON"){
        runAttributeEditingTest("ParameterObject/ParameterObject.json", project => {
            project.rootASTUnit.rootNode.elements("paths").head.elements("paths").head.elements("parameters").head.attribute("in")
        }, "header")
    }

    test("Parameter Definition Object (located in path item) 'in' editing ('query'->'body'). YAML"){
        runAttributeEditingTest("ParameterObject/ParameterObject.yml", project => {
            project.rootASTUnit.rootNode.elements("paths").head.elements("paths").head.elements("parameters").head.attribute("in")
        }, "body")
    }

    test("Parameter Definition Object (located in path item) 'in' editing ('query'->'body'). JSON"){
        runAttributeEditingTest("ParameterObject/ParameterObject.json", project => {
            project.rootASTUnit.rootNode.elements("paths").head.elements("paths").head.elements("parameters").head.attribute("in")
        }, "body")
    }

    test("Parameter Definition Object (located in path item) 'in' editing ('query'->'formData'). YAML"){
        runAttributeEditingTest("ParameterObject/ParameterObject.yml", project => {
            project.rootASTUnit.rootNode.elements("paths").head.elements("paths").head.elements("parameters").head.attribute("in")
        }, "formData")
    }

    test("Parameter Definition Object (located in path item) 'in' editing ('query'->'formData'). JSON"){
        runAttributeEditingTest("ParameterObject/ParameterObject.json", project => {
            project.rootASTUnit.rootNode.elements("paths").head.elements("paths").head.elements("parameters").head.attribute("in")
        }, "formData")
    }
}
