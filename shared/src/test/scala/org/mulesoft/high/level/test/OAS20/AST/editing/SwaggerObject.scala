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
}
