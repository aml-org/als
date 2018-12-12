package org.mulesoft.high.level.test.OAS20.AST.editing

import org.mulesoft.high.level.test.OAS20.OAS20ASTEditingTest

class PathItemObject extends OAS20ASTEditingTest{

    test("Path Item Object 'path' editing. YAML"){
        runAttributeEditingTest("PathItemObject/PathItemObject.yml", project => {
            project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.attribute("path")
        }, "/updatedPathValue")
    }

    test("Path Item Object 'path' editing. JSON"){
        runAttributeEditingTest("PathItemObject/PathItemObject.json", project => {
            project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.attribute("path")
        }, "/updatedPathValue")
    }

    test("PathItemObject operations editing YAML"){
        runAttributeEditingTest("PathItemObject/PathItemObject.yml", project => {
            project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.attribute("description")
        }, "txt")
    }

    test("PathItemObject operations editing JSON"){
        runAttributeEditingTest("PathItemObject/PathItemObject.json", project => {
            project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.attribute("description")
        }, "txt")
    }

    test("PathItemObject parameters editing YAML"){
        runAttributeEditingTest("ParameterObject/ParameterObject.yml", project => {
            project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("parameters").head.attribute("description")
        }, "txt")
    }

    test("PathItemObject parameters editing JSON"){
        runAttributeEditingTest("ParameterObject/ParameterObject.json", project => {
            project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("parameters").head.attribute("description")
        }, "txt")
    }

    test("Path Item Object 'path' creation. YAML"){
        var fp = "SwaggerObject/SwaggerObjectEmpty.yml"
        parse(filePath(fp)).flatMap(project=>{
            var apiNode = project.rootASTUnit.rootNode
            var apiDef = apiNode.definition
            var paths1Node = apiNode.newChild(apiDef.property("paths").get).flatMap(_.asElement).get
            var paths1Def = paths1Node.definition
            var paths2Node = paths1Node.newChild(paths1Def.property("paths").get).flatMap(_.asElement).get
            var paths2Def = paths2Node.definition
            runAttributeCreationTest1(project, project => {
                Some(project.rootASTUnit.rootNode.elements("paths").head.elements("paths").head)
            },"path","/pets")
        })
    }

    test("Path Item Object 'path' creation. JSON"){
        var fp = "SwaggerObject/SwaggerObjectEmpty.json"
        parse(filePath(fp)).flatMap(project=>{
            var apiNode = project.rootASTUnit.rootNode
            var apiDef = apiNode.definition
            var paths1Node = apiNode.newChild(apiDef.property("paths").get).flatMap(_.asElement).get
            var paths1Def = paths1Node.definition
            var paths2Node = paths1Node.newChild(paths1Def.property("paths").get).flatMap(_.asElement).get
            var paths2Def = paths2Node.definition
            runAttributeCreationTest1(project, project => {
                Some(project.rootASTUnit.rootNode.elements("paths").head.elements("paths").head)
            },"path","/pets")
        })
    }

//    test("PathItemObject operations creation YAML"){
//        var fp = "PathItemObject/PathItemObject.yml"
//        parse(filePath(fp)).flatMap(project=>{
//            var apiNode = project.rootASTUnit.rootNode
//            var apiDef = apiNode.definition
//            var paths2Node = apiNode.element("paths").get.elements("paths").head
//            var paths2Def = paths2Node.definition
//            var operationNode = paths2Node.newChild(paths2Def.property("operations").get).flatMap(_.asElement).get
//            var operationDef = operationNode.definition
//            runAttributeCreationTest1(project, project => {
//                Some(project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head)
//            },"method","get")
//        })
//    }
//
//    test("PathItemObject operations creation JSON"){
//        var fp = "PathItemObject/PathItemObject.json"
//        parse(filePath(fp)).flatMap(project=>{
//            var apiNode = project.rootASTUnit.rootNode
//            var apiDef = apiNode.definition
//            var paths2Node = apiNode.element("paths").get.elements("paths").head
//            var paths2Def = paths2Node.definition
//            var operationNode = paths2Node.newChild(paths2Def.property("operations").get).flatMap(_.asElement).get
//            var operationDef = operationNode.definition
//            runAttributeCreationTest1(project, project => {
//                Some(project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head)
//            },"method","get")
//        })
//    }
//
//    test("PathItemObject parameters creation YAML"){
//        var fp = "PathItemObject/PathItemObject.yml"
//        parse(filePath(fp)).flatMap(project=>{
//            var apiNode = project.rootASTUnit.rootNode
//            var apiDef = apiNode.definition
//            var paths2Node = apiNode.element("paths").get.elements("paths").head
//            var paths2Def = paths2Node.definition
//            var operationNode = paths2Node.newChild(paths2Def.property("parameters").get).flatMap(_.asElement).get
//            var operationDef = operationNode.definition
//            runAttributeCreationTest1(project, project => {
//                Some(project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("parameters").head)
//            },"key","txt")
//        })
//    }
}
