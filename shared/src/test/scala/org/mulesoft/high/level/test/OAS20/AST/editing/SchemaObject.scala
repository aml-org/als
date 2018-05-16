package org.mulesoft.high.level.test.OAS20.AST.editing

import org.mulesoft.high.level.test.OAS20.OAS20ASTEditingTest

class SchemaObject extends OAS20ASTEditingTest{

    test("SchemaObject 'name' editing. YAML"){
        runAttributeEditingTest("SchemaObject/SchemaObject.yml", project => {
            project.rootASTUnit.rootNode.elements("definitions").head.attribute("name")
        }, "Pet1")
    }

    test("SchemaObject 'name' editing. JSON"){
        runAttributeEditingTest("SchemaObject/SchemaObject.json", project => {
            project.rootASTUnit.rootNode.elements("definitions").head.attribute("name")
        }, "Pet1")
    }

    test("SchemaObject '$ref' editing. YAML"){
        runAttributeEditingTest("SchemaObject/SchemaObject.yml", project => {
            project.rootASTUnit.rootNode.elements("definitions")(2).attribute("$ref")
        }, "#/definitions/T1")
    }

    test("SchemaObject '$ref' editing. JSON"){
        runAttributeEditingTest("SchemaObject/SchemaObject.json", project => {
            project.rootASTUnit.rootNode.elements("definitions")(2).attribute("$ref")
        }, "#/definitions/T1")
    }

    test("SchemaObject property 'name' editing. YAML"){
        runAttributeEditingTest("SchemaObject/SchemaObject.yml", project => {
            project.rootASTUnit.rootNode.elements("definitions").head.elements("properties").head.attribute("name")
        }, "newPropertyName")
    }

    test("SchemaObject property 'name' editing. JSON"){
        runAttributeEditingTest("SchemaObject/SchemaObject.json", project => {
            project.rootASTUnit.rootNode.elements("definitions").head.elements("properties").head.attribute("name")
        }, "newPropertyName")
    }

}