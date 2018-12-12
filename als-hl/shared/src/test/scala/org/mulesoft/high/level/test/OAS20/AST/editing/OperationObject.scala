package org.mulesoft.high.level.test.OAS20.AST.editing

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
            var typeNode = paramNode.newChild(paramDef.property("type").get).flatMap(_.asAttr).get
            typeNode.modify("string")
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

    test("OperationObject summary editing. YAML"){
        runAttributeEditingTest( "OperationObject/OperationObject2.yml", project => {
            project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.attribute("summary")
        }, "Pet by id")
    }

    test("OperationObject description editing. YAML"){
        runAttributeEditingTest( "OperationObject/OperationObject2.yml", project => {
            project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.attribute("description")
        }, "description")
    }

    test("OperationObject externalDocs editing. YAML"){
        runAttributeEditingTest( "OperationObject/OperationObject2.yml", project => {
            project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.elements("externalDocs").head.attribute("description")
        }, "description")
    }

    test("OperationObject operationId editing. YAML"){
        runAttributeEditingTest( "OperationObject/OperationObject2.yml", project => {
            project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.attribute("operationId")
        }, "byPetId")
    }

//    test("OperationObject consumes editing. YAML"){
//        runAttributeEditingTest( "OperationObject/OperationObject2.yml", project => {
//            project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.attributes("consumes").headOption
//        }, "application/xml")
//    }
//
//    test("OperationObject produces editing. YAML"){
//        runAttributeEditingTest( "OperationObject/OperationObject2.yml", project => {
//            project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.attributes("produces").headOption
//        }, "application/xml")
//    }
//
//    test("OperationObject parameters editing. YAML"){
//        runAttributeEditingTest( "OperationObject/OperationObject2.yml", project => {
//            project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.elements("parameters").head.attribute("in")
//        }, "body")
//    }
//
//    test("OperationObject responses editing. YAML"){
//        runAttributeEditingTest( "OperationObject/OperationObject2.yml", project => {
//            project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.elements("responses").head.attribute("description")
//        }, "description")
//    }
//
//    test("OperationObject schemes editing. YAML"){
//        runAttributeEditingTest( "OperationObject/OperationObject2.yml", project => {
//            project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.attributes("schemes").headOption
//        }, "https")
//    }

    test("OperationObject deprecated editing. YAML"){
        runAttributeEditingTest( "OperationObject/OperationObject2.yml", project => {
            project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.attribute("deprecated")
        }, false)
    }

//    test("OperationObject security editing. YAML"){
//        runAttributeEditingTest( "OperationObject/OperationObject2.yml", project => {
//            project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.elements("security").head.attribute("name")
//        }, "user")
//    }

    test("OperationObject summary editing. JSON"){
        runAttributeEditingTest( "OperationObject/OperationObject2.json", project => {
            project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.attribute("summary")
        }, "Pet by id")
    }

    test("OperationObject description editing. JSON"){
        runAttributeEditingTest( "OperationObject/OperationObject2.json", project => {
            project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.attribute("description")
        }, "description")
    }

    test("OperationObject externalDocs editing. JSON"){
        runAttributeEditingTest( "OperationObject/OperationObject2.json", project => {
            project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.elements("externalDocs").head.attribute("description")
        }, "description")
    }

    test("OperationObject operationId editing. JSON"){
        runAttributeEditingTest( "OperationObject/OperationObject2.json", project => {
            project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.attribute("operationId")
        }, "byPetId")
    }

//    test("OperationObject consumes editing. JSON"){
//        runAttributeEditingTest( "OperationObject/OperationObject2.json", project => {
//            project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.attributes("consumes").headOption
//        }, "application/xml")
//    }
//
//    test("OperationObject produces editing. JSON"){
//        runAttributeEditingTest( "OperationObject/OperationObject2.json", project => {
//            project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.attributes("produces").headOption
//        }, "application/xml")
//    }
//
//    test("OperationObject parameters editing. JSON"){
//        runAttributeEditingTest( "OperationObject/OperationObject2.json", project => {
//            project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.elements("parameters").head.attribute("in")
//        }, "body")
//    }
//
//    test("OperationObject responses editing. JSON"){
//        runAttributeEditingTest( "OperationObject/OperationObject2.json", project => {
//            project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.elements("responses").head.attribute("description")
//        }, "description")
//    }
//
//    test("OperationObject schemes editing. JSON"){
//        runAttributeEditingTest( "OperationObject/OperationObject2.json", project => {
//            project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.attributes("schemes").headOption
//        }, "https")
//    }

    test("OperationObject deprecated editing. JSON"){
        runAttributeEditingTest( "OperationObject/OperationObject2.json", project => {
            project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.attribute("deprecated")
        }, false)
    }

//    test("OperationObject security editing. JSON"){
//        runAttributeEditingTest( "OperationObject/OperationObject2.json", project => {
//            project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.elements("security").head.attribute("name")
//        }, "user")
//    }
//
//    test("Operation Object 'method' creation. YAML"){
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

    test("OperationObject summary creation. YAML"){
        runAttributeCreationTest("OperationObject/OperationObject.yml", project=>{
            Option(project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head)
        }, "summary", "Pet by id")
    }

    test("OperationObject description creation. YAML"){
        runAttributeCreationTest("OperationObject/OperationObject.yml", project=>{
            Option(project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head)
        }, "description", "description")
    }

//    test("OperationObject externalDocs creation. YAML"){
//        var fp = "PathItemObject/PathItemObject.yml"
//        parse(filePath(fp)).flatMap(project=>{
//            var apiNode = project.rootASTUnit.rootNode
//            var apiDef = apiNode.definition
//            var paths2Node = apiNode.element("paths").get.elements("paths").head
//            var paths2Def = paths2Node.definition
//            var operationNode = paths2Node.newChild(paths2Def.property("operations").get).flatMap(_.asElement).get
//            var operationDef = operationNode.definition
//            var methodAttr = operationNode.newChild(operationDef.property("method").get).flatMap(_.asAttr).get
//            methodAttr.setValue("post").map(_ => operationNode)
//            var externalDocsNode = operationNode.newChild(operationDef.property("externalDocs").get).flatMap(_.asElement).get
//            var externalDocsDef = externalDocsNode.definition
//            runAttributeCreationTest1(project, project => {
//                Some(project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.elements("externalDocs").head)
//            },"url","https://example.com/docs")
//        })
//    }

    test("OperationObject operationId creation. YAML"){
        runAttributeCreationTest("OperationObject/OperationObject.yml", project=>{
            Option(project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head)
        }, "operationId", "byPetId")
    }

//    test("OperationObject consumes creation. YAML"){
//        runAttributeCreationTest("OperationObject/OperationObject.yml", project=>{
//            Option(project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head)
//        }, "consumes", "application/xml")
//    }
//
//    test("OperationObject produces creation. YAML"){
//        runAttributeCreationTest("OperationObject/OperationObject.yml", project=>{
//            Option(project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head)
//        }, "produces", "application/xml")
//    }
//
//    test("OperationObject parameters creation. YAML"){
//        var fp = "PathItemObject/PathItemObject.yml"
//        parse(filePath(fp)).flatMap(project=>{
//            var apiNode = project.rootASTUnit.rootNode
//            var apiDef = apiNode.definition
//            var paths2Node = apiNode.element("paths").get.elements("paths").head
//            var paths2Def = paths2Node.definition
//            var operationNode = paths2Node.newChild(paths2Def.property("operations").get).flatMap(_.asElement).get
//            var operationDef = operationNode.definition
//            var methodAttr = operationNode.newChild(operationDef.property("method").get).flatMap(_.asAttr).get
//            methodAttr.setValue("post").map(_ => operationNode)
//            var externalDocsNode = operationNode.newChild(operationDef.property("parameters").get).flatMap(_.asElement).get
//            var externalDocsDef = externalDocsNode.definition
//            runAttributeCreationTest1(project, project => {
//                Some(project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.elements("parameters").head)
//            },"in","body")
//        })
//    }
//
//    test("OperationObject responses creation. YAML"){
//        var fp = "PathItemObject/PathItemObject.yml"
//        parse(filePath(fp)).flatMap(project=>{
//            var apiNode = project.rootASTUnit.rootNode
//            var apiDef = apiNode.definition
//            var paths2Node = apiNode.element("paths").get.elements("paths").head
//            var paths2Def = paths2Node.definition
//            var operationNode = paths2Node.newChild(paths2Def.property("operations").get).flatMap(_.asElement).get
//            var operationDef = operationNode.definition
//            var methodAttr = operationNode.newChild(operationDef.property("method").get).flatMap(_.asAttr).get
//            methodAttr.setValue("post").map(_ => operationNode)
//            var responsesNode = operationNode.newChild(operationDef.property("responses").get).flatMap(_.asElement).get
//            var responsesDef = responsesNode.definition
//            runAttributeCreationTest1(project, project => {
//                Some(project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.elements("responses").head)
//            },"code",200)
//        })
//    }
//
//    test("OperationObject schemes creation. YAML"){
//        runAttributeCreationTest("OperationObject/OperationObject.yml", project=>{
//            Option(project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head)
//        }, "schemes", "https")
//    }

    test("OperationObject deprecated creation. YAML"){
        runAttributeCreationTest("OperationObject/OperationObject.yml", project=>{
            Option(project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head)
        }, "deprecated", true)
    }

//    test("OperationObject security creation. YAML"){
//        var fp = "PathItemObject/PathItemObject.yml"
//        parse(filePath(fp)).flatMap(project=>{
//            var apiNode = project.rootASTUnit.rootNode
//            var apiDef = apiNode.definition
//            var paths2Node = apiNode.element("paths").get.elements("paths").head
//            var paths2Def = paths2Node.definition
//            var operationNode = paths2Node.newChild(paths2Def.property("operations").get).flatMap(_.asElement).get
//            var operationDef = operationNode.definition
//            var methodAttr = operationNode.newChild(operationDef.property("method").get).flatMap(_.asAttr).get
//            methodAttr.setValue("post").map(_ => operationNode)
//            var securityNode = operationNode.newChild(operationDef.property("security").get).flatMap(_.asElement).get
//            var securityDef = securityNode.definition
//            runAttributeCreationTest1(project, project => {
//                Some(project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.elements("security").head)
//            },"name","user")
//        })
//    }
//
//    test("Operation Object 'method' creation. JSON"){
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

    test("OperationObject summary creation. JSON"){
        runAttributeCreationTest("OperationObject/OperationObject.json", project=>{
            Option(project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head)
        }, "summary", "Pet by id")
    }

    test("OperationObject description creation. JSON"){
        runAttributeCreationTest("OperationObject/OperationObject.json", project=>{
            Option(project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head)
        }, "description", "description")
    }

//    test("OperationObject externalDocs creation. JSON"){
//        var fp = "PathItemObject/PathItemObject.json"
//        parse(filePath(fp)).flatMap(project=>{
//            var apiNode = project.rootASTUnit.rootNode
//            var apiDef = apiNode.definition
//            var paths2Node = apiNode.element("paths").get.elements("paths").head
//            var paths2Def = paths2Node.definition
//            var operationNode = paths2Node.newChild(paths2Def.property("operations").get).flatMap(_.asElement).get
//            var operationDef = operationNode.definition
//            var methodAttr = operationNode.newChild(operationDef.property("method").get).flatMap(_.asAttr).get
//            methodAttr.setValue("post").map(_ => operationNode)
//            var externalDocsNode = operationNode.newChild(operationDef.property("externalDocs").get).flatMap(_.asElement).get
//            var externalDocsDef = externalDocsNode.definition
//            runAttributeCreationTest1(project, project => {
//                Some(project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.elements("externalDocs").head)
//            },"url","https://example.com/docs")
//        })
//    }

    test("OperationObject operationId creation. JSON"){
        runAttributeCreationTest("OperationObject/OperationObject.json", project=>{
            Option(project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head)
        }, "operationId", "byPetId")
    }

//    test("OperationObject consumes creation. JSON"){
//        runAttributeCreationTest("OperationObject/OperationObject.json", project=>{
//            Option(project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head)
//        }, "consumes", "application/xml")
//    }
//
//    test("OperationObject produces creation. JSON"){
//        runAttributeCreationTest("OperationObject/OperationObject.json", project=>{
//            Option(project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head)
//        }, "produces", "application/xml")
//    }
//
//    test("OperationObject parameters creation. JSON"){
//        var fp = "PathItemObject/PathItemObject.json"
//        parse(filePath(fp)).flatMap(project=>{
//            var apiNode = project.rootASTUnit.rootNode
//            var apiDef = apiNode.definition
//            var paths2Node = apiNode.element("paths").get.elements("paths").head
//            var paths2Def = paths2Node.definition
//            var operationNode = paths2Node.newChild(paths2Def.property("operations").get).flatMap(_.asElement).get
//            var operationDef = operationNode.definition
//            var methodAttr = operationNode.newChild(operationDef.property("method").get).flatMap(_.asAttr).get
//            methodAttr.setValue("post").map(_ => operationNode)
//            var externalDocsNode = operationNode.newChild(operationDef.property("parameters").get).flatMap(_.asElement).get
//            var externalDocsDef = externalDocsNode.definition
//            runAttributeCreationTest1(project, project => {
//                Some(project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.elements("parameters").head)
//            },"in","body")
//        })
//    }
//
//    test("OperationObject responses creation. JSON"){
//        var fp = "PathItemObject/PathItemObject.json"
//        parse(filePath(fp)).flatMap(project=>{
//            var apiNode = project.rootASTUnit.rootNode
//            var apiDef = apiNode.definition
//            var paths2Node = apiNode.element("paths").get.elements("paths").head
//            var paths2Def = paths2Node.definition
//            var operationNode = paths2Node.newChild(paths2Def.property("operations").get).flatMap(_.asElement).get
//            var operationDef = operationNode.definition
//            var methodAttr = operationNode.newChild(operationDef.property("method").get).flatMap(_.asAttr).get
//            methodAttr.setValue("post").map(_ => operationNode)
//            var responsesNode = operationNode.newChild(operationDef.property("responses").get).flatMap(_.asElement).get
//            var responsesDef = responsesNode.definition
//            runAttributeCreationTest1(project, project => {
//                Some(project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.elements("responses").head)
//            },"code",200)
//        })
//    }
//
//    test("OperationObject schemes creation. JSON"){
//        runAttributeCreationTest("OperationObject/OperationObject.json", project=>{
//            Option(project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head)
//        }, "schemes", "https")
//    }

    test("OperationObject deprecated creation. JSON"){
        runAttributeCreationTest("OperationObject/OperationObject.json", project=>{
            Option(project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head)
        }, "deprecated", true)
    }

//    test("OperationObject security creation. JSON"){
//        var fp = "PathItemObject/PathItemObject.json"
//        parse(filePath(fp)).flatMap(project=>{
//            var apiNode = project.rootASTUnit.rootNode
//            var apiDef = apiNode.definition
//            var paths2Node = apiNode.element("paths").get.elements("paths").head
//            var paths2Def = paths2Node.definition
//            var operationNode = paths2Node.newChild(paths2Def.property("operations").get).flatMap(_.asElement).get
//            var operationDef = operationNode.definition
//            var methodAttr = operationNode.newChild(operationDef.property("method").get).flatMap(_.asAttr).get
//            methodAttr.setValue("post").map(_ => operationNode)
//            var securityNode = operationNode.newChild(operationDef.property("security").get).flatMap(_.asElement).get
//            var securityDef = securityNode.definition
//            runAttributeCreationTest1(project, project => {
//                Some(project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.elements("operations").head.elements("security").head)
//            },"name","user")
//        })
//    }
}
