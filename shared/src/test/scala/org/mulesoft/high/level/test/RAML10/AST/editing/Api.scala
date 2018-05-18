package org.mulesoft.high.level.test.RAML10.AST.editing

import amf.core.metamodel.domain.templates.AbstractDeclarationModel
import amf.core.model.domain.ObjectNode
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

    test("Api. Creating Trait."){
        var fp = "Api/api_empty.raml"
        parse(filePath(fp)).flatMap(project=>{
            var apiNode = project.rootASTUnit.rootNode
            var apiDef = apiNode.definition
            var traitNode = apiNode.newChild(apiDef.property("traits").get).flatMap(_.asElement).get

            traitNode.amfNode.fields.setWithoutId(AbstractDeclarationModel.DataNode,ObjectNode())

            var traitDef = traitNode.definition

            runAttributeCreationTest1(project, project => {
                Some(project.rootASTUnit.rootNode.elements("traits").head)
            },"name","trait1")
        })
    }

    test("Api. Creating Resource Type."){
        var fp = "Api/api_empty.raml"
        parse(filePath(fp)).flatMap(project=>{
            var apiNode = project.rootASTUnit.rootNode
            var apiDef = apiNode.definition
            var resourceTypeNode = apiNode.newChild(apiDef.property("resourceTypes").get).flatMap(_.asElement).get

            resourceTypeNode.amfNode.fields.setWithoutId(AbstractDeclarationModel.DataNode,ObjectNode())

            var traitDef = resourceTypeNode.definition

            runAttributeCreationTest1(project, project => {
                Some(project.rootASTUnit.rootNode.elements("resourceTypes").head)
            },"name","resourceType1")
        })
    }

    test("Api. Creating Type Declaration 1."){
        var fp = "Api/api_empty.raml"
        parse(filePath(fp)).flatMap(project=>{
            var apiNode = project.rootASTUnit.rootNode
            var apiDef = apiNode.definition
            var typeNode = apiNode.newChild(apiDef.property("types").get).flatMap(_.asElement).get
            runAttributeCreationTest1(project, project => {
                Some(project.rootASTUnit.rootNode.elements("types").head)
            },"name","type1")
        })
    }
}
