package org.mulesoft.high.level.test.OAS20.AST.editing

import org.mulesoft.high.level.test.OAS20.OAS20ASTEditingTest

class SwaggerObject extends OAS20ASTEditingTest{

    test("Swagger Object 'host' editing. YAML"){
        runAttributeEditingTest("SwaggerObject/SwaggerObject.yml", project => {
            project.rootASTUnit.rootNode.attribute("host")
        }, "updated.host.value")
    }

    test("Swagger Object 'host' editing. JSON"){
        runAttributeEditingTest("SwaggerObject/SwaggerObject.json", project => {
            project.rootASTUnit.rootNode.attribute("host")
        }, "updated.host.value")
    }

    test("Swagger Object 'host' creation. YAML"){
        runAttributeCreationTest("SwaggerObject/SwaggerObjectEmpty.yml", project => {
            Some(project.rootASTUnit.rootNode)
        },"host", "new.host.value")
    }

    test("Swagger Object 'host' creation. JSON"){
        runAttributeCreationTest("SwaggerObject/SwaggerObjectEmpty.json", project => {
            Some(project.rootASTUnit.rootNode)
        },"host", "new.host.value")
    }

    test("Swagger Object 'basePath' editing. YAML"){
        runAttributeEditingTest("SwaggerObject/SwaggerObject.yml", project => {
            project.rootASTUnit.rootNode.attribute("basePath")
        }, "/updated/base/path/value")
    }

    test("Swagger Object 'basePath' editing. JSON"){
        runAttributeEditingTest("SwaggerObject/SwaggerObject.json", project => {
            project.rootASTUnit.rootNode.attribute("basePath")
        }, "/updated/base/path/value")
    }

    test("Swagger Object 'basePath' creation. YAML"){
        runAttributeCreationTest("SwaggerObject/SwaggerObjectEmpty.yml", project => {
            Some(project.rootASTUnit.rootNode)
        },"basePath", "/new/base/path/value")
    }

    test("Swagger Object 'basePath' creation. JSON"){
        runAttributeCreationTest("SwaggerObject/SwaggerObjectEmpty.json", project => {
            Some(project.rootASTUnit.rootNode)
        },"basePath", "/new/base/path/value")
    }

    test("Swagger Object. Creating Path Item. YAML"){
        var fp = "SwaggerObject/SwaggerObjectEmpty.yml"
        parse(filePath(fp)).flatMap(project=>{
            var apiNode = project.rootASTUnit.rootNode
            var apiDef = apiNode.definition
            var pathsNode = apiNode.newChild(apiDef.property("paths").get).flatMap(_.asElement).get
            var pathsDef = pathsNode.definition
            var pathItemNode = pathsNode.newChild(pathsDef.property("paths").get).flatMap(_.asElement).get

            var pathItemDef = pathItemNode.definition
            runAttributeCreationTest1(project, project => {
                Some(project.rootASTUnit.rootNode.elements("paths").head.elements("paths").head)
            },"path","/new/resource/{path}")
        })
    }

    test("Swagger Object. Creating Path Item. JSON"){
        var fp = "SwaggerObject/SwaggerObjectEmpty.json"
        parse(filePath(fp)).flatMap(project=>{
            var apiNode = project.rootASTUnit.rootNode
            var apiDef = apiNode.definition
            var pathsNode = apiNode.newChild(apiDef.property("paths").get).flatMap(_.asElement).get
            var pathsDef = pathsNode.definition
            var pathItemNode = pathsNode.newChild(pathsDef.property("paths").get).flatMap(_.asElement).get

            var pathItemDef = pathItemNode.definition
            runAttributeCreationTest1(project, project => {
                Some(project.rootASTUnit.rootNode.elements("paths").head.elements("paths").head)
            },"path","/new/resource/{path}")
        })
    }

    test("Swagger Object. Creating Operation. YAML"){

        var fp = "SwaggerObject/SwaggerObjectEmpty.yml"
        parse(filePath(fp)).flatMap(project=>{
            var apiNode = project.rootASTUnit.rootNode
            var apiDef = apiNode.definition
            var pathsNode = apiNode.newChild(apiDef.property("paths").get).flatMap(_.asElement).get
            var pathsDef = pathsNode.definition
            var pathItemNode = pathsNode.newChild(pathsDef.property("paths").get).flatMap(_.asElement).get

            var pathItemDef = pathItemNode.definition
            var pathAttr = pathItemNode.newChild(pathItemDef.property("path").get).flatMap(_.asAttr).get
            pathAttr.setValue("/resource").map(_=>pathItemNode)
        }).flatMap(pathItemNode=>{
            var project = pathItemNode.astUnit.project
            var pathItemDef = pathItemNode.definition
            var methodNode = pathItemNode.newChild(pathItemDef.property("operations").get).flatMap(_.asElement).get
            runAttributeCreationTest1(project, project => {
                Some(project.rootASTUnit.rootNode.elements("paths").head.elements("paths").head.elements("operations").head)
            }, "method", "get")
        })
    }

    test("Swagger Object. Creating Operation. JSON"){

        var fp = "SwaggerObject/SwaggerObjectEmpty.json"
        parse(filePath(fp)).flatMap(project=>{
            var apiNode = project.rootASTUnit.rootNode
            var apiDef = apiNode.definition
            var pathsNode = apiNode.newChild(apiDef.property("paths").get).flatMap(_.asElement).get
            var pathsDef = pathsNode.definition
            var pathItemNode = pathsNode.newChild(pathsDef.property("paths").get).flatMap(_.asElement).get

            var pathItemDef = pathItemNode.definition
            var pathAttr = pathItemNode.newChild(pathItemDef.property("path").get).flatMap(_.asAttr).get
            pathAttr.setValue("/resource").map(_=>pathItemNode)
        }).flatMap(pathItemNode=>{
            var project = pathItemNode.astUnit.project
            var pathItemDef = pathItemNode.definition
            var methodNode = pathItemNode.newChild(pathItemDef.property("operations").get).flatMap(_.asElement).get
            runAttributeCreationTest1(project, project => {
                Some(project.rootASTUnit.rootNode.elements("paths").head.elements("paths").head.elements("operations").head)
            }, "method", "get")
        })
    }

    test("Swagger Object info editing YAML"){
        runAttributeEditingTest( "SwaggerObject/SwaggerObject.yml", project => {
            project.rootASTUnit.rootNode.elements("info").head.attribute("version")
        }, "v2")
    }

    test("Swagger Object info editing JSON"){
        runAttributeEditingTest( "SwaggerObject/SwaggerObject.json", project => {
            project.rootASTUnit.rootNode.elements("info").head.attribute("version")
        }, "v2")
    }

//    test("Swagger Object schemes editing YAML"){
//        runAttributeEditingTest( "SwaggerObject/SwaggerObject.yml", project => {
//            project.rootASTUnit.rootNode.attributes("schemes").headOption
//        }, "https")
//    }
//
//    test("Swagger Object schemes editing JSON"){
//        runAttributeEditingTest( "SwaggerObject/SwaggerObject.json", project => {
//            project.rootASTUnit.rootNode.attributes("schemes").headOption
//        }, "https")
//    }
//
//    test("Swagger Object consumes editing YAML"){
//        runAttributeEditingTest( "SwaggerObject/SwaggerObject.yml", project => {
//            project.rootASTUnit.rootNode.attributes("consumes").headOption
//        }, "application/xml")
//    }
//
//    test("Swagger Object consumes editing JSON"){
//        runAttributeEditingTest( "SwaggerObject/SwaggerObject.json", project => {
//            project.rootASTUnit.rootNode.attributes("consumes").headOption
//        }, "application/xml")
//    }
//
//    test("Swagger Object produces editing YAML"){
//        runAttributeEditingTest( "SwaggerObject/SwaggerObject.yml", project => {
//            project.rootASTUnit.rootNode.attributes("produces").headOption
//        }, "application/text")
//    }
//
//    test("Swagger Object produces editing JSON"){
//        runAttributeEditingTest( "SwaggerObject/SwaggerObject.json", project => {
//            project.rootASTUnit.rootNode.attributes("produces").headOption
//        }, "application/text")
//    }

    test("Swagger Object paths editing YAML"){
        runAttributeEditingTest( "SwaggerObject/SwaggerObject.yml", project => {
            project.rootASTUnit.rootNode.elements("paths").head.elements("paths").head.attribute("path")
        }, "/res")
    }

    test("Swagger Object paths editing JSON"){
        runAttributeEditingTest( "SwaggerObject/SwaggerObject.json", project => {
            project.rootASTUnit.rootNode.elements("paths").head.elements("paths").head.attribute("path")
        }, "/res")
    }

    test("Swagger Object definitions editing YAML"){
        runAttributeEditingTest( "SwaggerObject/SwaggerObject.yml", project => {
            project.rootASTUnit.rootNode.elements("definitions").head.attribute("name")
        }, "Planet")
    }

    test("Swagger Object definitions editing JSON"){
        runAttributeEditingTest( "SwaggerObject/SwaggerObject.json", project => {
            project.rootASTUnit.rootNode.elements("definitions").head.attribute("name")
        }, "Planet")
    }

    test("Swagger Object parameters editing YAML"){
        runAttributeEditingTest( "SwaggerObject/SwaggerObject.yml", project => {
            project.rootASTUnit.rootNode.elements("parameters").head.attribute("name")
        }, "smtn")
    }

    test("Swagger Object parameters editing JSON"){
        runAttributeEditingTest( "SwaggerObject/SwaggerObject.json", project => {
            project.rootASTUnit.rootNode.elements("parameters").head.attribute("name")
        }, "smtn")
    }

    test("Swagger Object responses editing YAML"){
        runAttributeEditingTest( "SwaggerObject/SwaggerObject.yml", project => {
            project.rootASTUnit.rootNode.elements("responses").head.attribute("description")
        }, "p4")
    }

    test("Swagger Object responses editing JSON"){
        runAttributeEditingTest( "SwaggerObject/SwaggerObject.json", project => {
            project.rootASTUnit.rootNode.elements("responses").head.attribute("description")
        }, "p4")
    }

    test("Swagger Object securityDefinitions editing YAML"){
        runAttributeEditingTest( "SwaggerObject/SwaggerObject.yml", project => {
            project.rootASTUnit.rootNode.elements("securityDefinitions").head.attribute("name")
        }, "auth")
    }

    test("Swagger Object securityDefinitions editing JSON"){
        runAttributeEditingTest( "SwaggerObject/SwaggerObject.json", project => {
            project.rootASTUnit.rootNode.elements("securityDefinitions").head.attribute("name")
        }, "auth")
    }

//    test("Swagger Object security editing YAML"){
//        runAttributeEditingTest( "SwaggerObject/SwaggerObject.yml", project => {
//            project.rootASTUnit.rootNode.elements("security").head.attribute("name")
//        }, "code")
//    }
//
//    test("Swagger Object security editing JSON"){
//        runAttributeEditingTest( "SwaggerObject/SwaggerObject.json", project => {
//            project.rootASTUnit.rootNode.elements("security").head.attribute("name")
//        }, "code")
//    }

    test("Swagger Object externalDocs editing YAML"){
        runAttributeEditingTest( "SwaggerObject/SwaggerObject.yml", project => {
            project.rootASTUnit.rootNode.elements("externalDocs").head.attribute("url")
        }, "https://www.example.com")
    }

    test("Swagger Object externalDocs editing JSON"){
        runAttributeEditingTest( "SwaggerObject/SwaggerObject.json", project => {
            project.rootASTUnit.rootNode.elements("externalDocs").head.attribute("url")
        }, "https://www.example.com")
    }

    test("Swagger Object info creation YAML"){
        var fp = "SwaggerObject/SwaggerObjectEmpty.yml"
        parse(filePath(fp)).flatMap(project=>{
            var apiNode = project.rootASTUnit.rootNode
            var apiDef = apiNode.definition
            var infoNode = apiNode.newChild(apiDef.property("info").get).flatMap(_.asElement).get
            var infoDef = infoNode.definition
            runAttributeCreationTest1(project, project => {
                Some(project.rootASTUnit.rootNode.elements("info").head)
            },"version","v2")
        })
    }

    test("Swagger Object info creation JSON"){
        var fp = "SwaggerObject/SwaggerObjectEmpty.json"
        parse(filePath(fp)).flatMap(project=>{
            var apiNode = project.rootASTUnit.rootNode
            var apiDef = apiNode.definition
            var infoNode = apiNode.newChild(apiDef.property("info").get).flatMap(_.asElement).get
            var infoDef = infoNode.definition
            runAttributeCreationTest1(project, project => {
                Some(project.rootASTUnit.rootNode.elements("info").head)
            },"version","v2")
        })
    }

//    test("Swagger Object schemes creation YAML"){
//        var fp = "SwaggerObject/SwaggerObjectEmpty.yml"
//        parse(filePath(fp)).flatMap(project=>{
//            var apiNode = project.rootASTUnit.rootNode
//            var apiDef = apiNode.definition
//            var schemesNode = apiNode.newChild(apiDef.property("schemes").get).flatMap(_.asElement).get
//            var schemesDef = schemesNode.definition
//            runAttributeCreationTest1(project, project => {
//                Some(project.rootASTUnit.rootNode.elements("schemes").head)
//            },"name","https")
//        })
//    }
//
//    test("Swagger Object schemes creation JSON"){
//        var fp = "SwaggerObject/SwaggerObjectEmpty.json"
//        parse(filePath(fp)).flatMap(project=>{
//            var apiNode = project.rootASTUnit.rootNode
//            var apiDef = apiNode.definition
//            var schemesNode = apiNode.newChild(apiDef.property("schemes").get).flatMap(_.asElement).get
//            var schemesDef = schemesNode.definition
//            runAttributeCreationTest1(project, project => {
//                Some(project.rootASTUnit.rootNode.elements("schemes").head)
//            },"name","https")
//        })
//    }
//
//    test("Swagger Object consumes creation YAML"){
//        var fp = "SwaggerObject/SwaggerObjectEmpty.yml"
//        parse(filePath(fp)).flatMap(project=>{
//            var apiNode = project.rootASTUnit.rootNode
//            var apiDef = apiNode.definition
//            var consumesNode = apiNode.newChild(apiDef.property("consumes").get).flatMap(_.asElement).get
//            var consumesDef = consumesNode.definition
//            runAttributeCreationTest1(project, project => {
//                Some(project.rootASTUnit.rootNode.elements("consumes").head)
//            },"name","application/json")
//        })
//    }
//
//    test("Swagger Object consumes creation JSON"){
//        var fp = "SwaggerObject/SwaggerObjectEmpty.json"
//        parse(filePath(fp)).flatMap(project=>{
//            var apiNode = project.rootASTUnit.rootNode
//            var apiDef = apiNode.definition
//            var consumesNode = apiNode.newChild(apiDef.property("consumes").get).flatMap(_.asElement).get
//            var consumesDef = consumesNode.definition
//            runAttributeCreationTest1(project, project => {
//                Some(project.rootASTUnit.rootNode.elements("consumes").head)
//            },"name","application/json")
//        })
//    }
//
//    test("Swagger Object produces creation YAML"){
//        var fp = "SwaggerObject/SwaggerObjectEmpty.yml"
//        parse(filePath(fp)).flatMap(project=>{
//            var apiNode = project.rootASTUnit.rootNode
//            var apiDef = apiNode.definition
//            var producesNode = apiNode.newChild(apiDef.property("produces").get).flatMap(_.asElement).get
//            var producesDef = producesNode.definition
//            runAttributeCreationTest1(project, project => {
//                Some(project.rootASTUnit.rootNode.elements("produces").head)
//            },"name","application/json")
//        })
//    }
//
//    test("Swagger Object produces creation JSON"){
//        var fp = "SwaggerObject/SwaggerObjectEmpty.json"
//        parse(filePath(fp)).flatMap(project=>{
//            var apiNode = project.rootASTUnit.rootNode
//            var apiDef = apiNode.definition
//            var producesNode = apiNode.newChild(apiDef.property("produces").get).flatMap(_.asElement).get
//            var producesDef = producesNode.definition
//            runAttributeCreationTest1(project, project => {
//                Some(project.rootASTUnit.rootNode.elements("produces").head)
//            },"name","application/json")
//        })
//    }

    test("Swagger Object definitions creation YAML"){
        var fp = "SwaggerObject/SwaggerObjectEmpty.yml"
        parse(filePath(fp)).flatMap(project=>{
            var apiNode = project.rootASTUnit.rootNode
            var apiDef = apiNode.definition
            var definitionsNode = apiNode.newChild(apiDef.property("definitions").get).flatMap(_.asElement).get
            var definitionsDef = definitionsNode.definition
            runAttributeCreationTest1(project, project => {
                Some(project.rootASTUnit.rootNode.elements("definitions").head)
            },"name","Planet")
        })
    }

    test("Swagger Object definitions creation JSON"){
        var fp = "SwaggerObject/SwaggerObjectEmpty.json"
        parse(filePath(fp)).flatMap(project=>{
            var apiNode = project.rootASTUnit.rootNode
            var apiDef = apiNode.definition
            var definitionsNode = apiNode.newChild(apiDef.property("definitions").get).flatMap(_.asElement).get
            var definitionsDef = definitionsNode.definition
            runAttributeCreationTest1(project, project => {
                Some(project.rootASTUnit.rootNode.elements("definitions").head)
            },"name","Planet")
        })
    }

//    test("Swagger Object parameters creation YAML"){
//        var fp = "SwaggerObject/SwaggerObjectEmpty.yml"
//        parse(filePath(fp)).flatMap(project=>{
//            var apiNode = project.rootASTUnit.rootNode
//            var apiDef = apiNode.definition
//            var parametersNode = apiNode.newChild(apiDef.property("parameters").get).flatMap(_.asElement).get
//            var parametersDef = parametersNode.definition
//            runAttributeCreationTest1(project, project => {
//                Some(project.rootASTUnit.rootNode.elements("parameters").head)
//            },"key","Planet")
//        })
//    }
//
//    test("Swagger Object parameters creation JSON"){
//        var fp = "SwaggerObject/SwaggerObjectEmpty.json"
//        parse(filePath(fp)).flatMap(project=>{
//            var apiNode = project.rootASTUnit.rootNode
//            var apiDef = apiNode.definition
//            var parametersNode = apiNode.newChild(apiDef.property("parameters").get).flatMap(_.asElement).get
//            var parametersDef = parametersNode.definition
//            runAttributeCreationTest1(project, project => {
//                Some(project.rootASTUnit.rootNode.elements("parameters").head)
//            },"key","Planet")
//        })
//    }
//
//    test("Swagger Object responses creation YAML"){
//        var fp = "SwaggerObject/SwaggerObjectEmpty.yml"
//        parse(filePath(fp)).flatMap(project=>{
//            var apiNode = project.rootASTUnit.rootNode
//            var apiDef = apiNode.definition
//            var responsesNode = apiNode.newChild(apiDef.property("responses").get).flatMap(_.asElement).get
//            var responsesDef = responsesNode.definition
//            runAttributeCreationTest1(project, project => {
//                Some(project.rootASTUnit.rootNode.elements("responses").head)
//            },"key","r3")
//        })
//    }

    test("Swagger Object responses creation JSON"){
        var fp = "SwaggerObject/SwaggerObjectEmpty.json"
        parse(filePath(fp)).flatMap(project=>{
            var apiNode = project.rootASTUnit.rootNode
            var apiDef = apiNode.definition
            var responsesNode = apiNode.newChild(apiDef.property("responses").get).flatMap(_.asElement).get
            var responsesDef = responsesNode.definition
            runAttributeCreationTest1(project, project => {
                Some(project.rootASTUnit.rootNode.elements("responses").head)
            },"key","r3")
        })
    }

    test("Swagger Object securityDefinitions creation YAML"){
        var fp = "SwaggerObject/SwaggerObjectEmpty.yml"
        parse(filePath(fp)).flatMap(project=>{
            var apiNode = project.rootASTUnit.rootNode
            var apiDef = apiNode.definition
            var securityDefinitionsNode = apiNode.newChild(apiDef.property("securityDefinitions").get).flatMap(_.asElement).get
            var securityDefinitionsDef = securityDefinitionsNode.definition
            runAttributeCreationTest1(project, project => {
                Some(project.rootASTUnit.rootNode.elements("securityDefinitions").head)
            },"name","r3")
        })
    }

    test("Swagger Object securityDefinitions creation JSON"){
        var fp = "SwaggerObject/SwaggerObjectEmpty.json"
        parse(filePath(fp)).flatMap(project=>{
            var apiNode = project.rootASTUnit.rootNode
            var apiDef = apiNode.definition
            var securityDefinitionsNode = apiNode.newChild(apiDef.property("securityDefinitions").get).flatMap(_.asElement).get
            var securityDefinitionsDef = securityDefinitionsNode.definition
            runAttributeCreationTest1(project, project => {
                Some(project.rootASTUnit.rootNode.elements("securityDefinitions").head)
            },"name","r3")
        })
    }

//    test("Swagger Object security creation YAML"){
//        var fp = "SwaggerObject/SwaggerObjectEmpty.yml"
//        parse(filePath(fp)).flatMap(project=>{
//            var apiNode = project.rootASTUnit.rootNode
//            var apiDef = apiNode.definition
//            var securityNode = apiNode.newChild(apiDef.property("security").get).flatMap(_.asElement).get
//            var securityDef = securityNode.definition
//            runAttributeCreationTest1(project, project => {
//                Some(project.rootASTUnit.rootNode.elements("security").head)
//            },"name","r3")
//        })
//    }
//
//    test("Swagger Object security creation JSON"){
//        var fp = "SwaggerObject/SwaggerObjectEmpty.json"
//        parse(filePath(fp)).flatMap(project=>{
//            var apiNode = project.rootASTUnit.rootNode
//            var apiDef = apiNode.definition
//            var securityNode = apiNode.newChild(apiDef.property("security").get).flatMap(_.asElement).get
//            var securityDef = securityNode.definition
//            runAttributeCreationTest1(project, project => {
//                Some(project.rootASTUnit.rootNode.elements("security").head)
//            },"name","r3")
//        })
//    }

    test("Swagger Object externalDocs creation YAML"){
        var fp = "SwaggerObject/SwaggerObjectEmpty.yml"
        parse(filePath(fp)).flatMap(project=>{
            var apiNode = project.rootASTUnit.rootNode
            var apiDef = apiNode.definition
            var externalDocsNode = apiNode.newChild(apiDef.property("externalDocs").get).flatMap(_.asElement).get
            var externalDocsDef = externalDocsNode.definition
            runAttributeCreationTest1(project, project => {
                Some(project.rootASTUnit.rootNode.elements("externalDocs").head)
            },"url","https://example.com/docs")
        })
    }

    test("Swagger Object externalDocs creation JSON"){
        var fp = "SwaggerObject/SwaggerObjectEmpty.json"
        parse(filePath(fp)).flatMap(project=>{
            var apiNode = project.rootASTUnit.rootNode
            var apiDef = apiNode.definition
            var externalDocsNode = apiNode.newChild(apiDef.property("externalDocs").get).flatMap(_.asElement).get
            var externalDocsDef = externalDocsNode.definition
            runAttributeCreationTest1(project, project => {
                Some(project.rootASTUnit.rootNode.elements("externalDocs").head)
            },"url","https://example.com/docs")
        })
    }
}
