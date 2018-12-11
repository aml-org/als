package org.mulesoft.high.level.test.OAS20.AST.editing

import org.mulesoft.high.level.test.OAS20.OAS20ASTEditingTest

class SecurityDefinitionObject extends OAS20ASTEditingTest{

    test("Security Definition Object 'name' editing. YAML"){
        runAttributeEditingTest("SecurityDefinitionObject/SecurityDefinitionObject.yml", project => {
            project.rootASTUnit.rootNode.elements("securityDefinitions").head.attribute("name")
        }, "updatedParameterKeyValue")
    }

    test("Security Definition Object 'name' editing. JSON"){
        runAttributeEditingTest("SecurityDefinitionObject/SecurityDefinitionObject.json", project => {
            project.rootASTUnit.rootNode.elements("securityDefinitions").head.attribute("name")
        }, "updatedParameterKeyValue")
    }

//    test("Security Definition Object 'type' editing. YAML"){
//        runAttributeEditingTest("SecurityDefinitionObject/SecurityDefinitionObject.yml", project => {
//            project.rootASTUnit.rootNode.elements("securityDefinitions").head.attribute("type")
//        }, "apiKey")
//    }

    test("Security Definition Object 'description' editing. YAML"){
        runAttributeEditingTest("SecurityDefinitionObject/SecurityDefinitionObject.yml", project => {
            project.rootASTUnit.rootNode.elements("securityDefinitions").head.attribute("description")
        }, "text")
    }

//    test("Security Definition Object 'type' editing. JSON"){
//        runAttributeEditingTest("SecurityDefinitionObject/SecurityDefinitionObject.json", project => {
//            project.rootASTUnit.rootNode.elements("securityDefinitions").head.attribute("type")
//        }, "apiKey")
//    }

    test("Security Definition Object 'description' editing. JSON"){
        runAttributeEditingTest("SecurityDefinitionObject/SecurityDefinitionObject.json", project => {
            project.rootASTUnit.rootNode.elements("securityDefinitions").head.attribute("description")
        }, "text")
    }

    test("Security Definition Object 'name' creation. YAML"){
        var fp = "SwaggerObject/SwaggerObjectEmpty.yml"
        parse(filePath(fp)).flatMap(project=>{
            var apiNode = project.rootASTUnit.rootNode
            var apiDef = apiNode.definition
            var securityDefinitionsNode = apiNode.newChild(apiDef.property("securityDefinitions").get).flatMap(_.asElement).get
            var securityDefinitionsDef = securityDefinitionsNode.definition
            runAttributeCreationTest1(project, project => {
                Some(project.rootASTUnit.rootNode.elements("securityDefinitions").head)
            },"name", "pet")
        })
    }

//    test("Security Definition Object 'type' creation. YAML"){
//        var fp = "SwaggerObject/SwaggerObjectEmpty.yml"
//        parse(filePath(fp)).flatMap(project=>{
//            var apiNode = project.rootASTUnit.rootNode
//            var apiDef = apiNode.definition
//            var securityDefinitionsNode = apiNode.newChild(apiDef.property("securityDefinitions").get).flatMap(_.asElement).get
//            var securityDefinitionsDef = securityDefinitionsNode.definition
//            var paramKeyAttr = securityDefinitionsNode.newChild(securityDefinitionsDef.property("name").get).flatMap(_.asAttr).get
//            paramKeyAttr.setValue("Pet").map(_ => securityDefinitionsNode)
//            runAttributeCreationTest1(project, project => {
//                Some(project.rootASTUnit.rootNode.elements("securityDefinitions").head)
//            },"type", "apiKey")
//        })
//    }

    test("Security Definition Object 'description' creation. YAML"){
        var fp = "SwaggerObject/SwaggerObjectEmpty.yml"
        parse(filePath(fp)).flatMap(project=>{
            var apiNode = project.rootASTUnit.rootNode
            var apiDef = apiNode.definition
            var securityDefinitionsNode = apiNode.newChild(apiDef.property("securityDefinitions").get).flatMap(_.asElement).get
            var securityDefinitionsDef = securityDefinitionsNode.definition
            var paramKeyAttr = securityDefinitionsNode.newChild(securityDefinitionsDef.property("name").get).flatMap(_.asAttr).get
            paramKeyAttr.setValue("Pet").map(_ => securityDefinitionsNode)
            runAttributeCreationTest1(project, project => {
                Some(project.rootASTUnit.rootNode.elements("securityDefinitions").head)
            },"description", "pet")
        })
    }

    test("Security Definition Object 'name' creation. JSON"){
        var fp = "SwaggerObject/SwaggerObjectEmpty.json"
        parse(filePath(fp)).flatMap(project=>{
            var apiNode = project.rootASTUnit.rootNode
            var apiDef = apiNode.definition
            var securityDefinitionsNode = apiNode.newChild(apiDef.property("securityDefinitions").get).flatMap(_.asElement).get
            var securityDefinitionsDef = securityDefinitionsNode.definition
            runAttributeCreationTest1(project, project => {
                Some(project.rootASTUnit.rootNode.elements("securityDefinitions").head)
            },"name", "pet")
        })
    }

//    test("Security Definition Object 'type' creation. JSON"){
//        var fp = "SwaggerObject/SwaggerObjectEmpty.json"
//        parse(filePath(fp)).flatMap(project=>{
//            var apiNode = project.rootASTUnit.rootNode
//            var apiDef = apiNode.definition
//            var securityDefinitionsNode = apiNode.newChild(apiDef.property("securityDefinitions").get).flatMap(_.asElement).get
//            var securityDefinitionsDef = securityDefinitionsNode.definition
//            var paramKeyAttr = securityDefinitionsNode.newChild(securityDefinitionsDef.property("name").get).flatMap(_.asAttr).get
//            paramKeyAttr.setValue("Pet").map(_ => securityDefinitionsNode)
//            runAttributeCreationTest1(project, project => {
//                Some(project.rootASTUnit.rootNode.elements("securityDefinitions").head)
//            },"type", "apiKey")
//        })
//    }

    test("Security Definition Object 'description' creation. JSON"){
        var fp = "SwaggerObject/SwaggerObjectEmpty.json"
        parse(filePath(fp)).flatMap(project=>{
            var apiNode = project.rootASTUnit.rootNode
            var apiDef = apiNode.definition
            var securityDefinitionsNode = apiNode.newChild(apiDef.property("securityDefinitions").get).flatMap(_.asElement).get
            var securityDefinitionsDef = securityDefinitionsNode.definition
            var paramKeyAttr = securityDefinitionsNode.newChild(securityDefinitionsDef.property("name").get).flatMap(_.asAttr).get
            paramKeyAttr.setValue("Pet").map(_ => securityDefinitionsNode)
            runAttributeCreationTest1(project, project => {
                Some(project.rootASTUnit.rootNode.elements("securityDefinitions").head)
            },"description", "pet")
        })
    }
}
