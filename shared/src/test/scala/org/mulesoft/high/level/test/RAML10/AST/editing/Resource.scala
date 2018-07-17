package org.mulesoft.high.level.test.RAML10.AST.editing

import org.mulesoft.high.level.interfaces.IProject
import org.mulesoft.high.level.test.RAML10.RAML10ASTEditingTest

import scala.collection.mutable.ListBuffer


class Resource extends RAML10ASTEditingTest{

    test("Resource. Editing Relative URl of Parent Resource."){
        var projectOpt:Option[IProject] = None
        runAttributeEditingTest("Resource/resource_relative_uri.raml", project => {
            projectOpt = Some(project)
            project.rootASTUnit.rootNode.elements("resources").head.attribute("relativeUri")
        }, "/resource1/{updated}").map(x=>{
            if(x != succeed){
                x
            }
            else{
                val subresource11 = projectOpt.get.rootASTUnit.rootNode.elements("resources").head.elements("resources").head
                val subresource12 = subresource11.elements("resources").head

                var rp1 = subresource11.attribute("relativeUri").get.value.get
                var rp2 = subresource12.attribute("relativeUri").get.value.get
                val expected1 = "/subresource11"
                val expected2 = "/subresource12"
                var messages:Seq[String] = ((expected1,rp1) :: (expected2,rp2) :: Nil).filter(e=>e._1!=e._2)
                    .map(e=>s"Subresource path expected: '${e._1}', but obtained: '${e._2}'")
                if(messages.isEmpty){
                    succeed
                }
                else {
                    fail(messages.mkString("; "))
                }
            }
        })
    }

    test("Resource methods editing") {
        runAttributeEditingTest("Resource/methods.raml", project => {
            project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.attribute("method")
        }, "head")
    }

    test("Resource is editing") {
        runAttributeEditingTest("Resource/is.raml", project => {
            project.rootASTUnit.rootNode.elements("resources").head.elements("is").head.attribute("name")
        }, "main")
    }

    test("Resource type editing"){
        runAttributeEditingTest( "Resource/type.raml", project => {
            project.rootASTUnit.rootNode.elements("resources").head.element("type").head.attribute("name")
        }, "main")
    }

    test("Resource description editing"){
        runAttributeEditingTest( "Resource/description.raml", project => {
            project.rootASTUnit.rootNode.elements("resources").head.attribute("description")
        }, "text")
    }

    test("Resource securedBy editing") {
        runAttributeEditingTest("Resource/secured_by.raml", project => {
            project.rootASTUnit.rootNode.elements("resources").head.elements("securedBy").head.attribute("name")
        }, "oauth1")
    }

//    test("Resource uriParameters editing") {
//        runAttributeEditingTest("Resource/uri_parameters.raml", project => {
//            project.rootASTUnit.rootNode.elements("resources").head.elements("uriParameters").head.attribute("name")
//        }, "ser")
//    }

    test("Resource displayName editing"){
        runAttributeEditingTest( "Resource/display_name.raml", project => {
            project.rootASTUnit.rootNode.elements("resources").head.attribute("displayName")
        }, "dn")
    }

    test("Resource resources editing") {
        runAttributeEditingTest("Resource/resources.raml", project => {
            project.rootASTUnit.rootNode.elements("resources").head.elements("resources").head.attribute("relativeUri")
        }, "/res")
    }

    test("Resource methods creation") {
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

    test("Resource is creation"){
        var fp = "Resource/resource.raml"
        parse(filePath(fp)).flatMap(project=>{
            var resourceNode = project.rootASTUnit.rootNode.elements("resources").head
            var resourceDef = resourceNode.definition
            var isNode = resourceNode.newChild(resourceDef.property("is").get).flatMap(_.asElement).get
            var isDef = isNode.definition
            runAttributeCreationTest1(project, project => {
                Some(project.rootASTUnit.rootNode.elements("resources").head.elements("is").head)
            },"name","trt")
        })
    }

    test("Resource type reference creation"){
        var fp = "Resource/resource.raml"
        parse(filePath(fp)).flatMap(project=>{
            var resourceNode = project.rootASTUnit.rootNode.elements("resources").head
            var resourceDef = resourceNode.definition
            var resourceTypeNode = resourceNode.newChild(resourceDef.property("type").get).flatMap(_.asElement).get
            var resourceTypeDef = resourceTypeNode.definition
            runAttributeCreationTest1(project, project => {
                Some(project.rootASTUnit.rootNode.elements("resources").head.elements("type").head)
            },"name","rt")
        })
    }

    test("Resource description creation"){
        runAttributeCreationTest( "Resource/resource.raml", project => {
            Option(project.rootASTUnit.rootNode.elements("resources").head)
        }, "description", "text")
    }

    test("Resource securedBy creation") {
        var fp = "Resource/resource.raml"
        parse(filePath(fp)).flatMap(project=>{
            var resourceNode = project.rootASTUnit.rootNode.elements("resources").head
            var resourceDef = resourceNode.definition
            var securedByNode = resourceNode.newChild(resourceDef.property("securedBy").get).flatMap(_.asElement).get
            var securedByDef = securedByNode.definition
            runAttributeCreationTest1(project, project => {
                Some(project.rootASTUnit.rootNode.elements("resources").head.elements("securedBy").head)
            },"name","oauth2")
        })
    }

//    test("Resource uriParameters creation") {
//        var fp = "Resource/resource.raml"
//        parse(filePath(fp)).flatMap(project=>{
//            var resourceNode = project.rootASTUnit.rootNode.elements("resources").head
//            var resourceDef = resourceNode.definition
//            var uriParametersNode = resourceNode.newChild(resourceDef.property("uriParameters").get).flatMap(_.asElement).get
//            var uriParametersDef = uriParametersNode.definition
//            runAttributeCreationTest1(project, project => {
//                Some(project.rootASTUnit.rootNode.elements("resources").head.elements("uriParameters").head)
//            },"name","id")
//        })
//    }

    test("Resource displayName creation"){
        runAttributeCreationTest( "Resource/resource.raml", project => {
            Option(project.rootASTUnit.rootNode.elements("resources").head)
        }, "displayName",  "dn")
    }

    test("Resource resources creation") {
        var fp = "Resource/resource.raml"
        parse(filePath(fp)).flatMap(project=>{
            var resourceNode = project.rootASTUnit.rootNode.elements("resources").head
            var resourceDef = resourceNode.definition
            var subResourceNode = resourceNode.newChild(resourceDef.property("resources").get).flatMap(_.asElement).get
            var subResourceDef = subResourceNode.definition
            runAttributeCreationTest1(project, project => {
                Some(project.rootASTUnit.rootNode.elements("resources").head.elements("resources").head)
            },"relativeUri","/subResource")
        })
    }
}
