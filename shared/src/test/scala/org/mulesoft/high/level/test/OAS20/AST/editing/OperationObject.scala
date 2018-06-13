package org.mulesoft.high.level.test.OAS20.AST.editing

import amf.plugins.domain.shapes.metamodel.ScalarShapeModel
import amf.plugins.domain.webapi.metamodel.ParameterModel
import org.mulesoft.high.level.test.OAS20.OAS20ASTEditingTest

class OperationObject extends OAS20ASTEditingTest{

    test("Operation Object 'method' editing. YAML"){
        runAttributeEditingTest("OperationObject/OperationObject.yml", project => {
            project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.attribute("method")
        }, "options")
    }

    test("Operation Object 'method' editing. JSON"){
        runAttributeEditingTest("OperationObject/OperationObject.json", project => {
            project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.attribute("method")
        }, "options")
    }

    test("Operation Object. Adding a query parameter. YAML"){

        var fp = "OperationObject/OperationObject.yml"
        parse(filePath(fp)).flatMap(project=>{
            var operationNode = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head
            var operationDef = operationNode.definition
            val prop = operationDef.property("parameters").get
            val typeHint = project.rootASTUnit.rootNode.definition.universe.`type`("CommonParameterObject")

            var paramNode = operationNode.newChild(prop,typeHint).flatMap(_.asElement).get
            var paramDef = paramNode.definition
            var inNode = paramNode.newChild(paramDef.property("in").get).flatMap(_.asAttr).get
            inNode.modify("query")
            runAttributeCreationTest1Internal(project, project => {
                project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.elements("parameters").headOption
            }, "name", "queryParam1").flatMap(x=>{
                if(x.result != succeed){
                    x.result
                }
                else {
                    var project = x.modifiedProject
                    var paramNode = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.elements("parameters").headOption.flatMap(_.asElement).get

                    paramNode.amfNode.fields.setWithoutId(ParameterModel.Schema,ScalarShapeModel.modelInstance)
                    var typeNode = paramNode.newChild(paramDef.property("type").get).flatMap(_.asAttr).get
                    typeNode.modify("string")
                    runAttributeCreationTest1(project, project => {
                        project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.elements("parameters").headOption
                    }, "maxLength", 8)
                }
            })
        })
    }

    test("Operation Object. Adding a body parameter. YAML"){

        var fp = "OperationObject/OperationObject.yml"
        parse(filePath(fp)).flatMap(project=>{
            var operationNode = project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head
            var operationDef = operationNode.definition
            val prop = operationDef.property("parameters").get
            val typeHint = project.rootASTUnit.rootNode.definition.universe.`type`("BodyParameterObject")

            var paramNode = operationNode.newChild(prop,typeHint).flatMap(_.asElement).get
            var paramDef = paramNode.definition
            var schemaNode = paramNode.newChild(paramDef.property("schema").get).flatMap(_.asElement).get
            var paramNameAttr = paramNode.newChild(paramDef.property("name").get).get
            runAttributeCreationTest1(project, project => {
                project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.elements("parameters").head.element("schema")
            }, "title", "New Schema")
        })
    }
}
