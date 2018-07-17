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

    test("API title editing"){
        runAttributeEditingTest( "Api/api_title.raml", project => {
            project.rootASTUnit.rootNode.attribute("title")
        }, "api")
    }

    test("API version editing"){
        runAttributeEditingTest( "Api/api_version.raml", project => {
            project.rootASTUnit.rootNode.attribute("version")
        }, "2")
    }

    test("API resources editing"){
        runAttributeEditingTest( "Api/api_resources.raml", project => {
            project.rootASTUnit.rootNode.elements("resources").head.attribute("relativeUri")
        }, "/res")
    }

    test("API securedBy editing"){
        runAttributeEditingTest( "Api/api_secured_by.raml", project => {
            project.rootASTUnit.rootNode.elements("securedBy").head.attribute("name")
        }, "oauth")
    }

    test("API baseUriParameters editing"){
        runAttributeEditingTest( "Api/api_base_uri_parameters.raml", project => {
            project.rootASTUnit.rootNode.elements("baseUriParameters").head.attribute("name")
        }, "nd")
    }

//    test("API protocols editing"){
//        runAttributeEditingTest( "Api/api_protocols.raml", project => {
//            Option(project.rootASTUnit.rootNode.attributes("protocols").head)
//        }, "HTTPS")
//    }

    test("API documentation editing"){
        runAttributeEditingTest( "Api/api_documentation.raml", project => {
            project.rootASTUnit.rootNode.elements("documentation").head.attribute("title")
        }, "rml")
    }

//    test("API mediaType editing"){
//        runAttributeEditingTest( "Api/api_media_type.raml", project => {
//            project.rootASTUnit.rootNode.attribute("mediaType")
//        }, "application/xml")
//    }
//
//    test("API title creating"){
//        runAttributeCreationTest("Api/api_empty.raml", project => {
//            Option(project.rootASTUnit.rootNode)
//        }, "title", "new API")
//    }

    test("API version creating"){
        runAttributeCreationTest("Api/api_empty.raml", project => {
            Option(project.rootASTUnit.rootNode)
        }, "version", "2")
    }

    test("API securedBy creating"){
        var fp = "Api/api_empty.raml"
        parse(filePath(fp)).flatMap(project=>{
            var apiNode = project.rootASTUnit.rootNode
            var apiDef = apiNode.definition
            var securedByNode = apiNode.newChild(apiDef.property("securedBy").get).flatMap(_.asElement).get
            var securedByDef = securedByNode.definition
            runAttributeCreationTest1(project, project => {
                Some(project.rootASTUnit.rootNode.elements("securedBy").head)
            },"name","oauth")
        })
    }

    test("API baseUriParameters creating"){
        var fp = "Api/api_empty.raml"
        parse(filePath(fp)).flatMap(project=>{
            var apiNode = project.rootASTUnit.rootNode
            var apiDef = apiNode.definition
            var baseUriParameterNode = apiNode.newChild(apiDef.property("baseUriParameters").get).flatMap(_.asElement).get
            var baseUriParameterDef = baseUriParameterNode.definition
            runAttributeCreationTest1(project, project => {
                Some(project.rootASTUnit.rootNode.elements("baseUriParameters").head)
            },"name","node")
        })
    }

//    test("API protocols creating"){
//        runAttributeCreationTest("Api/api_empty.raml", project => {
//            Option(project.rootASTUnit.rootNode)
//        }, "protocols", "HTTPS")
//    }

    test("API documentation creating"){
        var fp = "Api/api_empty.raml"
        parse(filePath(fp)).flatMap(project=>{
            var apiNode = project.rootASTUnit.rootNode
            var apiDef = apiNode.definition
            var documentationNode = apiNode.newChild(apiDef.property("documentation").get).flatMap(_.asElement).get
            var documentationDef = documentationNode.definition
            runAttributeCreationTest1(project, project => {
                Some(project.rootASTUnit.rootNode.elements("documentation").head)
            },"title","raml")
        })
    }

//    test("API mediaType creating"){
//        runAttributeCreationTest("Api/api_empty.raml", project => {
//            Option(project.rootASTUnit.rootNode)
//        }, "mediaType", "application/xml")
//    }

    test("API baseUri creating"){
        runAttributeCreationTest("Api/api_empty.raml", project => {
            Option(project.rootASTUnit.rootNode)
        }, "baseUri", "https://www.example.com/rest")
    }
}
