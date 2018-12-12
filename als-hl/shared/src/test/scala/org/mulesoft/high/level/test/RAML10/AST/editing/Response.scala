package org.mulesoft.high.level.test.RAML10.AST.editing

import org.mulesoft.high.level.test.RAML10.RAML10ASTEditingTest

class Response extends RAML10ASTEditingTest {

    test("Response 'code' editing") {
        runAttributeEditingTest("Response/response_code.raml", project => {
            project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("responses")(1).attribute("code")
        }, "404")
    }

    test("Response header editing") {
        runAttributeEditingTest("Response/api.raml", project => {
            project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("responses").head.elements("headers").head.attribute("name")
        }, "match")
    }

    test("Response body editing") {
        runAttributeEditingTest("Response/api.raml", project => {
            project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("responses").head.elements("body").head.attribute("name")
        }, "application/xml")
    }

    test("Response description editing") {
        runAttributeEditingTest("Response/api.raml", project => {
            project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("responses").head.attribute("description")
        }, "Created")
    }

    test("Response 'code' creation") {
//        runAttributeEditingTest("Response/response_code.raml", project => {
//            project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("responses")(1).attribute("code")
//        }, "404")
        var fp = "Response/api.raml"
        parse(filePath(fp)).flatMap(project=>{
            var methodNode = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head
            var methodDef = methodNode.definition
            var response = methodNode.newChild(methodDef.property("responses").get).flatMap(_.asElement).get

            var responseDef = response.definition

            runAttributeCreationTest1(project, project => {
                Some(project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("responses")(1))
            },"code","207")
        })
    }

    test("Response header creation") {
        var fp = "Response/api.raml"
        parse(filePath(fp)).flatMap(project=>{
            var responseNode = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("responses").head
            var responseNodeDef = responseNode.definition
            var responseHeader = responseNode.newChild(responseNodeDef.property("headers").get).flatMap(_.asElement).get

            var headerDef = responseHeader.definition

            runAttributeCreationTest1(project, project => {
                Some(project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("responses").head.elements("headers")(1))
            },"name","If")
        })
    }

    test("Response body creation") {
        var fp = "Response/api.raml"
        parse(filePath(fp)).flatMap(project=>{
            var responseNode = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("responses").head
            var responseNodeDef = responseNode.definition
            var responseBody = responseNode.newChild(responseNodeDef.property("body").get).flatMap(_.asElement).get

            var bodyDef = responseBody.definition

            runAttributeCreationTest1(project, project => {
                Some(project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("responses").head.elements("body")(1))
            },"name","application/xml")
        })
    }

    test("Response description creation") {
        runAttributeCreationTest("Response/apiEmptyDescription.raml", project => {
            Some(project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("responses").head)
        },"description", "Created")
    }
}
