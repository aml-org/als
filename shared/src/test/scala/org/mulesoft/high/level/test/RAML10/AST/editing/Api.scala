package org.mulesoft.high.level.test.RAML10.AST.editing

import org.mulesoft.high.level.test.RAML10.RAML10ASTEditingTest


class Api extends RAML10ASTEditingTest{

    test("Api 'baseUri' editing") {
        runAttributeEditingTest("Api/api_base_uri.raml", project => {
            project.rootASTUnit.rootNode.attribute("baseUri")
        }, "https://updated.base/uri")
    }

    test("Api 'description' editing") {
        runAttributeEditingTest("Api/api_description.raml", project => {
            project.rootASTUnit.rootNode.attribute("description")
        }, "updated API description")
    }

    test("Api 'description' creation") {
        runAttributeCreationTest("Api/api_empty.raml", project => {
            Option(project.rootASTUnit.rootNode)
        }, "description", "new API description")
    }

    test("Api. Creating Resource."){
        var fp = "Api/api_empty.raml"
        parse(filePath(fp)).flatMap(project=>{
            var apiNode = project.rootASTUnit.rootNode
            var apiDef = apiNode.definition
            var resourceNode = apiNode.newChild(apiDef.property("resources").get).flatMap(_.asElement).get
            var resourceDef = resourceNode.definition
            runAttributeCreationTest1(project, project => {
                Some(project.rootASTUnit.rootNode.elements("resources").head)
            },"relativeUri","/new/resource/{path}")
        })
    }

    test("Api. Creating Method."){

        var fp = "Api/api_empty.raml"
        parse(filePath(fp)).flatMap(project=>{
            var apiNode = project.rootASTUnit.rootNode
            var apiDef = apiNode.definition
            var resourceNode = apiNode.newChild(apiDef.property("resources").get).flatMap(_.asElement).get
            var resourceDef = resourceNode.definition
            var pathAttr = resourceNode.newChild(resourceDef.property("relativeUri").get).flatMap(_.asAttr).get
            pathAttr.setValue("/resource").map(_=>resourceNode)
        }).flatMap(resourceNode=>{
            var project = resourceNode.astUnit.project
            var resourceDef = resourceNode.definition
            var methodNode = resourceNode.newChild(resourceDef.property("methods").get).flatMap(_.asElement).get
            runAttributeCreationTest1(project, project => {
                Some(project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head)
            }, "method", "get")
        })
    }

    test("Api. Creating Resource Chain and a Method."){

        var fp = "Api/api_empty.raml"
        parse(filePath(fp)).flatMap(project=>{
            var apiNode = project.rootASTUnit.rootNode
            var apiDef = apiNode.definition
            var resourceNode = apiNode.newChild(apiDef.property("resources").get).flatMap(_.asElement).get
            var resourceDef = resourceNode.definition
            var pathAttr = resourceNode.newChild(resourceDef.property("relativeUri").get).flatMap(_.asAttr).get
            pathAttr.setValue("/resource").map(_=>resourceNode)
        }).flatMap(resourceNode=>{
            var resourceDef = resourceNode.definition
            var resourceNode1 = resourceNode.newChild(resourceDef.property("resources").get).flatMap(_.asElement).get
            var pathAttr = resourceNode1.newChild(resourceDef.property("relativeUri").get).flatMap(_.asAttr).get
            pathAttr.setValue("/subresource").map(_=>resourceNode1)
        }).flatMap(resourceNode=>{
            var project = resourceNode.astUnit.project
            var resourceDef = resourceNode.definition
            var methodNode = resourceNode.newChild(resourceDef.property("methods").get).flatMap(_.asElement).get
            runAttributeCreationTest1(project, project => {
                Some(project.rootASTUnit.rootNode.elements("resources").head.elements("resources").head.elements("methods").head)
            }, "method", "get")
        })
    }
}
