package org.mulesoft.high.level.test.RAML10.AST.editing

import org.mulesoft.high.level.test.RAML10.RAML10ASTEditingTest

class TypeDeclaration extends RAML10ASTEditingTest {

    test("TypeDeclaration 'name' editing") {
        runAttributeEditingTest("TypeDeclaration/typeDeclarationRoot.raml", project => {
            project.rootASTUnit.rootNode.elements("types").head.attribute("name")
        }, "UpdatedRootTypeName")
    }

    test("TypeDeclaration property 'name' editing") {
        runAttributeEditingTest("TypeDeclaration/typeDeclarationProperty.raml", project => {
            project.rootASTUnit.rootNode.elements("types").head.elements("properties").head.attribute("name")
        }, "updatedPropertyName")
    }

    test("TypeDeclaration parameter 'name' editing") {
        runAttributeEditingTest("TypeDeclaration/typeDeclarationParameter.raml", project => {
            project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("queryParameters").head.attribute("name")
        }, "updatedParameterName")
    }

    test("TypeDeclaration body 'name' editing") {
        runAttributeEditingTest("TypeDeclaration/typeDeclarationBody.raml", project => {
            project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("body").head.attribute("name")
        }, "application/xml")
    }

    test("TypeDeclaration 'type' editing 1") {
        runAttributeEditingTest("TypeDeclaration/typeDeclarationRoot.raml", project => {
            project.rootASTUnit.rootNode.elements("types")(3).attribute("type")
        }, "T1")
    }

    test("TypeDeclaration 'type' editing 2") {
        runAttributeEditingTest("TypeDeclaration/typeDeclarationRoot.raml", project => {
            project.rootASTUnit.rootNode.elements("types")(3).attribute("type")
        }, "T1 | T2")
    }

    test("TypeDeclaration property 'type' editing 1") {
        runAttributeEditingTest("TypeDeclaration/typeDeclarationProperty.raml", project => {
            project.rootASTUnit.rootNode.elements("types").head.elements("properties").head.attribute("type")
        }, "T1")
    }

    test("TypeDeclaration property 'type' editing 2") {
        runAttributeEditingTest("TypeDeclaration/typeDeclarationProperty.raml", project => {
            project.rootASTUnit.rootNode.elements("types").head.elements("properties").head.attribute("type")
        }, "T1 | T2")
    }

    test("TypeDeclaration parameter 'type' editing 1") {
        runAttributeEditingTest("TypeDeclaration/typeDeclarationParameter.raml", project => {
            project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("queryParameters").head.attribute("type")
        }, "T1")
    }

    test("TypeDeclaration parameter 'type' editing 2") {
        runAttributeEditingTest("TypeDeclaration/typeDeclarationParameter.raml", project => {
            project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("queryParameters").head.attribute("type")
        }, "T1 | T2")
    }

    test("TypeDeclaration body 'type' editing 1") {
        runAttributeEditingTest("TypeDeclaration/typeDeclarationBody.raml", project => {
            project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("body").head.attribute("type")
        }, "T1")
    }

    test("TypeDeclaration body 'type' editing 2") {
        runAttributeEditingTest("TypeDeclaration/typeDeclarationBody.raml", project => {
            project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("body").head.attribute("type")
        }, "T1 | T2")
    }


}
