package org.mulesoft.high.level.test.RAML10.AST.editing

import amf.core.model.domain.{ObjectNode, ScalarNode}
import amf.core.model.domain.templates.{Variable, VariableValue}
import org.mulesoft.high.level.interfaces.IProject
import org.mulesoft.high.level.test.RAML10.RAML10ASTEditingTest

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
                var param1Node =  traitNode.newChild(traitDef.property("parameters").get).get
//                param1Node.amfNode.asInstanceOf[VariableValue].withValue(ObjectNode())
                runAttributeCreationTest1Internal(project, project => {
                    Some(project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("is")(1).elements("parameters")(0))
                }, "name", "param1")
            }
            else {
                Future.successful(r)
            }
        }).flatMap(r => {
            if(r.result == succeed) {
                runAttributeCreationTest1Internal(r.modifiedProject, project => {
                    Some(project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("is")(1).elements("parameters")(0))
                }, "value", "updatedValueForParam1")
            }
            else {
                Future.successful(r)
            }
        }).flatMap(r => {
            if(r.result == succeed) {
                var project = r.modifiedProject
                var traitNode = project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("is")(1)
                var traitDef = traitNode.definition
                var param2Node =  traitNode.newChild(traitDef.property("parameters").get).get
                runAttributeCreationTest1Internal(project, project => {
                    Some(project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("is")(1).elements("parameters")(1))
                }, "name", "param2")
            }
            else {
                Future.successful(r)
            }
        }).flatMap(r => {
            if(r.result == succeed) {
                runAttributeCreationTest1Internal(r.modifiedProject, project => {
                    Some(project.rootASTUnit.rootNode.elements("resources").head.elements("methods").head.elements("is")(1).elements("parameters")(1))
                }, "value", "updatedValueForParam2")
            }
            else {
                Future.successful(r)
            }
        }).map(_.result)
    }


}
