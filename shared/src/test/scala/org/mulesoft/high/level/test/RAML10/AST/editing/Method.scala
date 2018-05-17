package org.mulesoft.high.level.test.RAML10.AST.editing

import amf.core.model.domain.{ObjectNode, ScalarNode}
import amf.core.model.domain.templates.{Variable, VariableValue}
import org.mulesoft.high.level.interfaces.IProject
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


}
