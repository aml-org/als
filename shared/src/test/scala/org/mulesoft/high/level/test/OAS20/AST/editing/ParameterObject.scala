package org.mulesoft.high.level.test.OAS20.AST.editing

import org.mulesoft.high.level.test.OAS20.OAS20ASTEditingTest

class ParameterObject extends OAS20ASTEditingTest{

    test("Parameter Definition Object 'key' editing. YAML"){
        runAttributeEditingTest("ParameterObject/ParameterObject.yml", project => {
            project.rootASTUnit.rootNode.elements("parameters").head.attribute("key")
        }, "updatedParameterKeyValue")
    }

    test("Parameter Definition Object 'key' editing. JSON"){
        runAttributeEditingTest("ParameterObject/ParameterObject.json", project => {
            project.rootASTUnit.rootNode.elements("parameters").head.attribute("key")
        }, "updatedParameterKeyValue")
    }

    test("Parameter Definition Object 'in' editing ('query'->'path'). YAML"){
        runAttributeEditingTest("ParameterObject/ParameterObject.yml", project => {
            project.rootASTUnit.rootNode.elements("parameters").head.attribute("in")
        }, "path")
    }

    test("Parameter Definition Object 'in' editing ('query'->'path'). JSON"){
        runAttributeEditingTest("ParameterObject/ParameterObject.json", project => {
            project.rootASTUnit.rootNode.elements("parameters").head.attribute("in")
        }, "path")
    }

    test("Parameter Definition Object 'in' editing ('query'->'header'). YAML"){
        runAttributeEditingTest("ParameterObject/ParameterObject.yml", project => {
            project.rootASTUnit.rootNode.elements("parameters").head.attribute("in")
        }, "header")
    }

    test("Parameter Definition Object 'in' editing ('query'->'header'). JSON"){
        runAttributeEditingTest("ParameterObject/ParameterObject.json", project => {
            project.rootASTUnit.rootNode.elements("parameters").head.attribute("in")
        }, "header")
    }

    test("Parameter Definition Object 'in' editing ('query'->'body'). YAML"){
        runAttributeEditingTest("ParameterObject/ParameterObject.yml", project => {
            project.rootASTUnit.rootNode.elements("parameters").head.attribute("in")
        }, "body")
    }

    test("Parameter Definition Object 'in' editing ('query'->'body'). JSON"){
        runAttributeEditingTest("ParameterObject/ParameterObject.json", project => {
            project.rootASTUnit.rootNode.elements("parameters").head.attribute("in")
        }, "body")
    }

    test("Parameter Definition Object 'in' editing ('query'->'formData'). YAML"){
        runAttributeEditingTest("ParameterObject/ParameterObject.yml", project => {
            project.rootASTUnit.rootNode.elements("parameters").head.attribute("in")
        }, "formData")
    }

    test("Parameter Definition Object 'in' editing ('query'->'formData'). JSON"){
        runAttributeEditingTest("ParameterObject/ParameterObject.json", project => {
            project.rootASTUnit.rootNode.elements("parameters").head.attribute("in")
        }, "formData")
    }

    test("Parameter Definition Object 'in' editing ('body'->'query'). YAML"){
        runAttributeEditingTest("ParameterObject/ParameterObject.yml", project => {
            project.rootASTUnit.rootNode.elements("parameters")(1).attribute("in")
        }, "query")
    }

    test("Parameter Definition Object 'in' editing ('body'->'query'). JSON"){
        runAttributeEditingTest("ParameterObject/ParameterObject.json", project => {
            project.rootASTUnit.rootNode.elements("parameters")(1).attribute("in")
        }, "query")
    }

    test("Parameter Object (located in path item) 'in' editing ('query'->'path'). YAML"){
        runAttributeEditingTest("ParameterObject/ParameterObject.yml", project => {
            project.rootASTUnit.rootNode.elements("paths").head.elements("paths").head.elements("parameters").head.attribute("in")
        }, "path")
    }

    test("Parameter Object (located in path item) 'in' editing ('query'->'path'). JSON"){
        runAttributeEditingTest("ParameterObject/ParameterObject.json", project => {
            project.rootASTUnit.rootNode.elements("paths").head.elements("paths").head.elements("parameters").head.attribute("in")
        }, "path")
    }

    test("Parameter Object (located in path item) 'in' editing ('query'->'header'). YAML"){
        runAttributeEditingTest("ParameterObject/ParameterObject.yml", project => {
            project.rootASTUnit.rootNode.elements("paths").head.elements("paths").head.elements("parameters").head.attribute("in")
        }, "header")
    }

    test("Parameter Object (located in path item) 'in' editing ('query'->'header'). JSON"){
        runAttributeEditingTest("ParameterObject/ParameterObject.json", project => {
            project.rootASTUnit.rootNode.elements("paths").head.elements("paths").head.elements("parameters").head.attribute("in")
        }, "header")
    }

//    test("Parameter Definition Object (located in path item) 'in' editing ('query'->'body'). YAML"){
//        runAttributeEditingTest("ParameterObject/ParameterObject.yml", project => {
//            project.rootASTUnit.rootNode.elements("paths").head.elements("paths").head.elements("parameters").head.attribute("in")
//        }, "body")
//    }
//
//    test("Parameter Definition Object (located in path item) 'in' editing ('query'->'body'). JSON"){
//        runAttributeEditingTest("ParameterObject/ParameterObject.json", project => {
//            project.rootASTUnit.rootNode.elements("paths").head.elements("paths").head.elements("parameters").head.attribute("in")
//        }, "body")
//    }
//
//    test("Parameter Definition Object (located in path item) 'in' editing ('query'->'formData'). YAML"){
//        runAttributeEditingTest("ParameterObject/ParameterObject.yml", project => {
//            project.rootASTUnit.rootNode.elements("paths").head.elements("paths").head.elements("parameters").head.attribute("in")
//        }, "formData")
//    }
//
//    test("Parameter Definition Object (located in path item) 'in' editing ('query'->'formData'). JSON"){
//        runAttributeEditingTest("ParameterObject/ParameterObject.json", project => {
//            project.rootASTUnit.rootNode.elements("paths").head.elements("paths").head.elements("parameters").head.attribute("in")
//        }, "formData")
//    }

    test("Parameter Object '$ref' editing for refering parameter. YAML"){
        runAttributeEditingTest("ParameterObject/ParameterObjectRef.json", project => {
            project.rootASTUnit.rootNode.elements("paths").head.elements("paths").head.elements("operations").head.elements("parameters").head.attribute("$ref")
        }, "#/parameters/skipParam1")
    }

    test("Parameter Object '$ref' editing for refering parameter. JSON"){
        runAttributeEditingTest("ParameterObject/ParameterObjectRef.json", project => {
            project.rootASTUnit.rootNode.elements("paths").head.elements("paths").head.elements("operations").head.elements("parameters").head.attribute("$ref")
        }, "#/parameters/skipParam1")
    }

    test("Parameter Object '$ref' editing for parameter expressed as AMF Parameter. YAML") {

        runAttributeCreationTest("ParameterObject/ParameterObjectRef.yml", project => {
            Some(project.rootASTUnit.rootNode.elements("paths").head.elements("paths").head.elements("operations").head.elements("parameters")(1))
        }, "$ref", "#/parameters/skipParam1")
    }

    test("Parameter Object '$ref' editing for parameter expressed as AMF Parameter. JSON") {
        runAttributeCreationTest("ParameterObject/ParameterObjectRef.json", project => {
            Some(project.rootASTUnit.rootNode.elements("paths").head.elements("paths").head.elements("operations").head.elements("parameters")(1))
        }, "$ref", "#/parameters/skipParam1")
    }

    //    test("Parameter Object '$ref' editing for parameter expressed as AMF Payload. YAML") {
    //        runAttributeCreationTest("ParameterObject/ParameterObjectRef.yml", project => {
    //            Some(project.rootASTUnit.rootNode.elements("paths").head.elements("paths").head.elements("operations").head.elements("parameters")(2))
    //        }, "$ref", "#/parameters/skipParam1")
    //    }
    //
    //    test("Parameter Object '$ref' editing for parameter expressed as AMF Payload. JSON") {
    //        runAttributeCreationTest("ParameterObject/ParameterObjectRef.json", project => {
    //            Some(project.rootASTUnit.rootNode.elements("paths").head.elements("paths").head.elements("operations").head.elements("parameters")(2))
    //        }, "$ref", "#/parameters/skipParam1")
    //    }

    test("Parameter Definition Object 'name' editing. YAML"){
        runAttributeEditingTest("ParameterObject/ParameterObject.yml", project => {
            project.rootASTUnit.rootNode.elements("parameters").head.attribute("name")
        }, "upd")
    }

    test("Parameter Definition Object 'description' editing. YAML"){
        runAttributeEditingTest("ParameterObject/ParameterObject.yml", project => {
            project.rootASTUnit.rootNode.elements("parameters").head.attribute("name")
        }, "text")
    }

    test("Parameter Definition Object 'required' editing. YAML"){
        runAttributeEditingTest("ParameterObject/ParameterObject.yml", project => {
            project.rootASTUnit.rootNode.elements("parameters").head.attribute("required")
        }, false)
    }

    test("Parameter Definition Object 'name' editing. JSON"){
        runAttributeEditingTest("ParameterObject/ParameterObject.json", project => {
            project.rootASTUnit.rootNode.elements("parameters").head.attribute("name")
        }, "upd")
    }

    test("Parameter Definition Object 'description' editing. JSON"){
        runAttributeEditingTest("ParameterObject/ParameterObject.json", project => {
            project.rootASTUnit.rootNode.elements("parameters").head.attribute("name")
        }, "text")
    }

    test("Parameter Definition Object 'required' editing. JSON"){
        runAttributeEditingTest("ParameterObject/ParameterObject.json", project => {
            project.rootASTUnit.rootNode.elements("parameters").head.attribute("required")
        }, false)
    }

//    test("ParameterObject name creation YAML"){
//        var fp = "SwaggerObject/SwaggerObjectEmpty.yml"
//        parse(filePath(fp)).flatMap(project=>{
//            var apiNode = project.rootASTUnit.rootNode
//            var apiDef = apiNode.definition
//            var parameterNode = apiNode.newChild(apiDef.property("parameters").get).flatMap(_.asElement).get
//            var parameterDef = parameterNode.definition
//            var paramKeyAttr = parameterNode.newChild(parameterDef.property("key").get).flatMap(_.asAttr).get
//            paramKeyAttr.setValue("param").map(_ => parameterNode)
//            runAttributeCreationTest1(project, project => {
//                Some(project.rootASTUnit.rootNode.elements("parameters").head)
//            },"name","param")
//        })
//    }
//
//    test("ParameterObject description creation YAML"){
//        var fp = "SwaggerObject/SwaggerObjectEmpty.yml"
//        parse(filePath(fp)).flatMap(project=>{
//          var apiNode = project.rootASTUnit.rootNode
//          var apiDef = apiNode.definition
//          var parameterNode = apiNode.newChild(apiDef.property("parameters").get).flatMap(_.asElement).get
//          var parameterDef = parameterNode.definition
//          var paramKeyAttr = parameterNode.newChild(parameterDef.property("key").get).flatMap(_.asAttr).get
//          paramKeyAttr.setValue("param").map(_ => parameterNode)
//          runAttributeCreationTest1(project, project => {
//            Some(project.rootASTUnit.rootNode.elements("parameters").head)
//          },"description","text")
//        })
//    }
//
//    test("ParameterObject in creation YAML"){
//        var fp = "SwaggerObject/SwaggerObjectEmpty.yml"
//        parse(filePath(fp)).flatMap(project=>{
//            var apiNode = project.rootASTUnit.rootNode
//            var apiDef = apiNode.definition
//            var parameterNode = apiNode.newChild(apiDef.property("parameters").get).flatMap(_.asElement).get
//            var parameterDef = parameterNode.definition
//            var paramKeyAttr = parameterNode.newChild(parameterDef.property("key").get).flatMap(_.asAttr).get
//            paramKeyAttr.setValue("param").map(_ => parameterNode)
//            runAttributeCreationTest1(project, project => {
//                Some(project.rootASTUnit.rootNode.elements("parameters").head)
//            },"in","query")
//        })
//    }
//
//    test("ParameterObject required creation YAML"){
//        var fp = "SwaggerObject/SwaggerObjectEmpty.yml"
//        parse(filePath(fp)).flatMap(project=>{
//            var apiNode = project.rootASTUnit.rootNode
//            var apiDef = apiNode.definition
//            var parameterNode = apiNode.newChild(apiDef.property("parameters").get).flatMap(_.asElement).get
//            var parameterDef = parameterNode.definition
//            var paramKeyAttr = parameterNode.newChild(parameterDef.property("key").get).flatMap(_.asAttr).get
//            paramKeyAttr.setValue("param").map(_ => parameterNode)
//            runAttributeCreationTest1(project, project => {
//                Some(project.rootASTUnit.rootNode.elements("parameters").head)
//            },"required", true)
//        })
//    }
//
//    test("ParameterObject name creation JSON"){
//        var fp = "SwaggerObject/SwaggerObjectEmpty.yml"
//        parse(filePath(fp)).flatMap(project=>{
//            var apiNode = project.rootASTUnit.rootNode
//            var apiDef = apiNode.definition
//            var parameterNode = apiNode.newChild(apiDef.property("parameters").get).flatMap(_.asElement).get
//            var parameterDef = parameterNode.definition
//            var paramKeyAttr = parameterNode.newChild(parameterDef.property("key").get).flatMap(_.asAttr).get
//            paramKeyAttr.setValue("param").map(_ => parameterNode)
//            runAttributeCreationTest1(project, project => {
//                Some(project.rootASTUnit.rootNode.elements("parameters").head)
//            },"name","param")
//        })
//    }
//
//    test("ParameterObject description creation JSON"){
//        var fp = "SwaggerObject/SwaggerObjectEmpty.yml"
//        parse(filePath(fp)).flatMap(project=>{
//          var apiNode = project.rootASTUnit.rootNode
//          var apiDef = apiNode.definition
//          var parameterNode = apiNode.newChild(apiDef.property("parameters").get).flatMap(_.asElement).get
//          var parameterDef = parameterNode.definition
//          var paramKeyAttr = parameterNode.newChild(parameterDef.property("key").get).flatMap(_.asAttr).get
//          paramKeyAttr.setValue("param").map(_ => parameterNode)
//          runAttributeCreationTest1(project, project => {
//            Some(project.rootASTUnit.rootNode.elements("parameters").head)
//          },"description","text")
//        })
//    }
//
//    test("ParameterObject in creation JSON"){
//        var fp = "SwaggerObject/SwaggerObjectEmpty.yml"
//        parse(filePath(fp)).flatMap(project=>{
//            var apiNode = project.rootASTUnit.rootNode
//            var apiDef = apiNode.definition
//            var parameterNode = apiNode.newChild(apiDef.property("parameters").get).flatMap(_.asElement).get
//            var parameterDef = parameterNode.definition
//            var paramKeyAttr = parameterNode.newChild(parameterDef.property("key").get).flatMap(_.asAttr).get
//            paramKeyAttr.setValue("param").map(_ => parameterNode)
//            runAttributeCreationTest1(project, project => {
//                Some(project.rootASTUnit.rootNode.elements("parameters").head)
//            },"in","query")
//        })
//    }
//
//    test("ParameterObject required creation JSON"){
//        var fp = "SwaggerObject/SwaggerObjectEmpty.yml"
//        parse(filePath(fp)).flatMap(project=>{
//            var apiNode = project.rootASTUnit.rootNode
//            var apiDef = apiNode.definition
//            var parameterNode = apiNode.newChild(apiDef.property("parameters").get).flatMap(_.asElement).get
//            var parameterDef = parameterNode.definition
//            var paramKeyAttr = parameterNode.newChild(parameterDef.property("key").get).flatMap(_.asAttr).get
//            paramKeyAttr.setValue("param").map(_ => parameterNode)
//            runAttributeCreationTest1(project, project => {
//                Some(project.rootASTUnit.rootNode.elements("parameters").head)
//            },"required", true)
//        })
//    }
}
