package org.mulesoft.high.level.test.RAML10.AST.editing

import amf.core.model.domain.extensions.PropertyShape
import amf.plugins.domain.shapes.models.NodeShape
import org.mulesoft.high.level.test.RAML10.RAML10ASTEditingTest
import org.mulesoft.typesystem.json.interfaces.JSONWrapper
import org.mulesoft.typesystem.json.interfaces.JSONWrapperKind.STRING

import scala.collection.mutable.ListBuffer
import scala.concurrent.Future


class Method extends RAML10ASTEditingTest{

    test("Method. Editing name."){
        runAttributeEditingTest("Method/method_method.raml", project => {
            project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.attribute("method")
        }, "post")
    }

    test("Method. Editing used parameterless trait."){
        runAttributeEditingTest("Method/method_is.raml", project => {
            project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("is").head.attribute("name")
        }, "tr2")
    }

    test("Method. Adding new parameterless trait to method."){
        var fp = "Method/method_is.raml"
        parse(filePath(fp)).flatMap(project=>{
            var methodNode = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head
            var methodDef = methodNode.definition
            var traitNode = methodNode.newChild(methodDef.property("is").get).flatMap(_.asElement).get
            var traitDef = traitNode.definition
            runAttributeCreationTest1(project, project => {
                Some(project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("is")(1))
            }, "name", "tr2")
        })
    }

    test("Method. Editing used parametrized trait 1."){
        runAttributeEditingTest("Method/method_is_parametrized.raml", project => {
            project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("is").head.attribute("name")
        }, "tr2")
    }

    test("Method. Editing used parametrized trait 2."){
        runAttributeEditingTest("Method/method_is_parametrized.raml", project => {
            project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("is").head.attribute("name")
        }, "tr3")
    }

    test("Method. Adding new parametrized trait to method."){
        var fp = "Method/method_is_parametrized.raml"
        parse(filePath(fp)).flatMap(project=>{
            var methodNode = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head
            var methodDef = methodNode.definition
            var traitNode = methodNode.newChild(methodDef.property("is").get).flatMap(_.asElement).get
            var traitDef = traitNode.definition
            runAttributeCreationTest1(project, project => {
                Some(project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("is")(1))
            }, "name", "tr2")
        })
    }

    test("Method. Adding new parametrized trait to method and setting its parameters."){
        var fp = "Method/method_is_parametrized.raml"

        val param1Name = "param1"
        val param2Name = "param2"
        val param1Value = "updatedValueForParam1"
        val param2Value = "updatedValueForParam2"

        parse(filePath(fp)).map(project=>{
            var methodNode = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head
            var methodDef = methodNode.definition
            var traitNode = methodNode.newChild(methodDef.property("is").get).flatMap(_.asElement).get
            var traitDef = traitNode.definition
            project
        }).flatMap(project=>runAttributeCreationTest1Internal(project, project => {
            Some(project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("is")(1))
        }, "name", "tr2")).flatMap(r => {
            if(r.result == succeed) {
                var project = r.modifiedProject
                var traitNode = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("is")(1)
                var traitDef = traitNode.definition
                var param1Node =  traitNode.newChild(traitDef.property("parameters").get).flatMap(_.asElement).get
                var paramDef = param1Node.definition
                var paramValueAttr = param1Node.newChild(paramDef.property("value").get).flatMap(_.asAttr).get
                paramValueAttr.modify(param1Value)
                runAttributeCreationTest1Internal(project, project => {
                    Some(project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("is")(1).elements("parameters")(0))
                }, "name", param1Name)
            }
            else {
                Future.successful(r)
            }
        })/*.flatMap(r => {
            if(r.result == succeed) {
                runAttributeCreationTest1Internal(r.modifiedProject, project => {
                    Some(project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("is")(1).elements("parameters")(0))
                }, "value", param1Value)
            }
            else {
                Future.successful(r)
            }
        })*/.flatMap(r => {
            if(r.result == succeed) {
                var project = r.modifiedProject
                var traitNode = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("is")(1)
                var traitDef = traitNode.definition
                var param2Node =  traitNode.newChild(traitDef.property("parameters").get).flatMap(_.asElement).get
                var paramDef = param2Node.definition
                var paramValueAttr = param2Node.newChild(paramDef.property("value").get).flatMap(_.asAttr).get
                paramValueAttr.modify(param2Value)
                runAttributeCreationTest1Internal(project, project => {
                    Some(project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("is")(1).elements("parameters")(1))
                }, "name", param2Name)
            }
            else {
                Future.successful(r)
            }
        })/*.flatMap(r => {
            if(r.result == succeed) {
                runAttributeCreationTest1Internal(r.modifiedProject, project => {
                    Some(project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("is")(1).elements("parameters")(1))
                }, "value", param2Value)
            }
            else {
                Future.successful(r)
            }
        })*/.map(r=> {
            if(r.result != succeed){
                r.result
            }
            else {
                var project = r.modifiedProject
                var traitParameters = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("is")(1).elements("parameters")
                var param1Name_ = traitParameters(0).attribute("name").get.value.get
                var param2Name_ = traitParameters(1).attribute("name").get.value.get
                var param1Value_ = traitParameters(0).attribute("value").get.value.get.asInstanceOf[JSONWrapper].value(STRING).get
                var param2Value_ = traitParameters(1).attribute("value").get.value.get.asInstanceOf[JSONWrapper].value(STRING).get

                if(param1Name!=param1Name_){
                    fail(s"expected '$param1Name' but got '$param1Name_'")
                }
                else if(param2Name!=param2Name_){
                    fail(s"expected '$param2Name' but got '$param2Name_'")
                }
                else if(param1Value!=param1Value_){
                    fail(s"expected '$param1Value' but got '$param1Value_'")
                }
                else if(param2Value!=param2Value_){
                    fail(s"expected '$param2Value' but got '$param2Value_'")
                }
                else {
                    succeed
                }
            }
        })
    }

    test("Method. Creating new body and a property for it."){
        parse(filePath("Method/method_method.raml")).flatMap(project=>{

            var methodNode = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head
            var methodDef = methodNode.definition

            var bodyNode = methodNode.newChild(methodDef.property("body").get).flatMap(_.asElement).get

            runAttributeCreationTest1Internal(project, project => {
                Some(project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("body").head)
            }, "name", "application/json")
        }).flatMap(r=>{
            if(r.result != succeed){
                Future.successful(r)
            }
            else {
                var project = r.modifiedProject
                runAttributeCreationTest1Internal(project, project => {
                    Some(project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("body").head)
                }, "type", "object")
            }
        }).flatMap(r=>{
            if(r.result != succeed){
                r.result
            }
            else {
                var project = r.modifiedProject
                var bodyNode = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("body").head
                var bodyDef = bodyNode.definition
                var propNode = bodyNode.newChild(bodyDef.property("properties").get).flatMap(_.asElement).get

                val propType = NodeShape()
                propNode.amfNode.asInstanceOf[PropertyShape].withRange(propType)

                runAttributeCreationTest1(project, project => {
                    Some(project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("body").head.elements("properties").head)
                }, "name", "newProperty1")
            }
        })
    }

    test("Method queryParameters editing") {
        runAttributeEditingTest("Method/query_parameters.raml", project => {
            project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("queryParameters").head.attribute("name")
        }, "ih")
    }

    test("Method headers editing") {
        runAttributeEditingTest("Method/headers.raml", project => {
            project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("headers").head.attribute("name")
        }, "A")
    }

    test("Method queryString editing"){
        runAttributeEditingTest( "Method/query_string.raml", project => {
            project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("queryString").head.attribute("type")
        }, "number")
    }

    test("Method responses editing") {
        runAttributeEditingTest("Method/responses.raml", project => {
            project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("responses").head.attribute("code")
        }, "202")
    }

    test("Method body editing") {
        runAttributeEditingTest("Method/body.raml", project => {
            project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("body").head.attribute("name")
        }, "application/xml")
    }

//    test("Method protocols editing") {
//        runAttributeEditingTest("Method/protocols.raml", project => {
//            project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.attribute("protocols")
//        }, "HTTPS")
//    }

    test("Method securedBy name editing") {
        runAttributeEditingTest("Method/secured_by.raml", project => {
            project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("securedBy").head.attribute("name")
        }, "oauth")
    }

    test("Method description editing"){
        runAttributeEditingTest( "Method/description.raml", project => {
            project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.attribute("description")
        }, "dsc")
    }

    test("Method displayName editing"){
        runAttributeEditingTest( "Method/display_name.raml", project => {
            project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.attribute("displayName")
        }, "name")
    }

    test("Method queryParameters creation") {
        var fp = "Method/method.raml"
        parse(filePath(fp)).flatMap(project=>{
            var methodNode = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head
            var methodDef = methodNode.definition
            var queryParameterNode = methodNode.newChild(methodDef.property("queryParameters").get).flatMap(_.asElement).get
            var queryParameterDef = queryParameterNode.definition
            runAttributeCreationTest1(project, project => {
                Some(project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("queryParameters").head)
            },"name","qParam")
        })
    }

    test("Method headers creation") {
        var fp = "Method/method.raml"
        parse(filePath(fp)).flatMap(project=>{
            var methodNode = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head
            var methodDef = methodNode.definition
            var headersNode = methodNode.newChild(methodDef.property("headers").get).flatMap(_.asElement).get
            var headersDef = headersNode.definition
            runAttributeCreationTest1(project, project => {
                Some(project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("headers").head)
            },"name","Acpt")
        })
    }

//    test("Method queryString creation"){
//        var fp = "Method/method.raml"
//        parse(filePath(fp)).flatMap(project=>{
//            var methodNode = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head
//            var methodDef = methodNode.definition
//            var queryStringNode = methodNode.newChild(methodDef.property("queryString").get).flatMap(_.asElement).get
//            var queryStringDef = queryStringNode.definition
//            runAttributeCreationTest1(project, project => {
//                Some(project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("queryString").head)
//            },"displayName","string")
//        })
//    }

    test("Method responses creation") {
        var fp = "Method/method.raml"
        parse(filePath(fp)).flatMap(project=>{
            var methodNode = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head
            var methodDef = methodNode.definition
            var responsesNode = methodNode.newChild(methodDef.property("responses").get).flatMap(_.asElement).get
            var responsesDef = responsesNode.definition
            runAttributeCreationTest1(project, project => {
                Some(project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("responses").head)
            },"code","200")
        })
    }

//    test("Method protocols creation") {
//        runAttributeCreationTest( "Method/method.raml", project => {
//            Option(project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head)
//        }, "protocols",  "HTTP")
//    }

    test("Method securedBy creation") {
        var fp = "Method/method.raml"
        parse(filePath(fp)).flatMap(project=>{
            var methodNode = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head
            var methodDef = methodNode.definition
            var securedByNode = methodNode.newChild(methodDef.property("securedBy").get).flatMap(_.asElement).get
            var securedByDef = securedByNode.definition
            runAttributeCreationTest1(project, project => {
                Some(project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("securedBy").head)
            },"name","oauth")
        })
    }

    test("Method description creation"){
        runAttributeCreationTest( "Method/method.raml", project => {
            Option(project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head)
        }, "description",  "txt")
    }

    test("Method displayName creation"){
        runAttributeCreationTest( "Method/method.raml", project => {
            Option(project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head)
        }, "displayName",  "displayName")
    }

    test("Method method creation"){
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
}
