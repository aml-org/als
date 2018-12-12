package org.mulesoft.high.level.test.OAS20.AST.editing

import org.mulesoft.high.level.test.OAS20.OAS20ASTEditingTest

class ScopeObject extends OAS20ASTEditingTest{
  test("ScopeObject name edition. YAML"){
    runAttributeEditingTest("SecurityDefinitionObject/SecurityDefinitionObject.yml", project => {
      project.rootASTUnit.rootNode.elements("securityDefinitions").head.elements("scopes").head.elements("scopes").head.attribute("name")
    }, "admin")
  }

  test("ScopeObject description edition. YAML"){
    runAttributeEditingTest("SecurityDefinitionObject/SecurityDefinitionObject.yml", project => {
      project.rootASTUnit.rootNode.elements("securityDefinitions").head.elements("scopes").head.elements("scopes").head.attribute("description")
    }, "description")
  }

  test("ScopeObject name edition. JSON"){
    runAttributeEditingTest("SecurityDefinitionObject/SecurityDefinitionObject.json", project => {
      project.rootASTUnit.rootNode.elements("securityDefinitions").head.elements("scopes").head.elements("scopes").head.attribute("name")
    }, "admin")
  }

  test("ScopeObject description edition. JSON"){
    runAttributeEditingTest("SecurityDefinitionObject/SecurityDefinitionObject.json", project => {
      project.rootASTUnit.rootNode.elements("securityDefinitions").head.elements("scopes").head.elements("scopes").head.attribute("description")
    }, "description")
  }

//  test("ScopeObject name creation. YAML"){
//    var fp = "SecurityDefinitionObject/SecurityDefinitionObjectScopeEmpty.yml"
//    parse(filePath(fp)).flatMap(project=>{
//      var securityDefinitionsNode = project.rootASTUnit.rootNode.elements("securityDefinitions").head
//      var securityDefinitionsDef = securityDefinitionsNode.definition
//      var scopesNode = securityDefinitionsNode.newChild(securityDefinitionsDef.property("scopes").get).flatMap(_.asElement).get
//      var scopesDef = scopesNode.definition
//      var scopesInnerNode = scopesNode.newChild(scopesDef.property("scopes").get).flatMap(_.asElement).get
//      var scopesInnerDef = scopesInnerNode.definition
//      runAttributeCreationTest1(project, project => {
//        Some(project.rootASTUnit.rootNode.elements("securityDefinitions").head.elements("scopes").head.elements("scopes").head)
//      },"name", "admin")
//    })
//  }
//
//  test("ScopeObject description creation. YAML"){
//    var fp = "SecurityDefinitionObject/SecurityDefinitionObjectScopeEmpty.yml"
//    parse(filePath(fp)).flatMap(project=>{
//      var securityDefinitionsNode = project.rootASTUnit.rootNode.elements("securityDefinitions").head
//      var securityDefinitionsDef = securityDefinitionsNode.definition
//      var scopesNode = securityDefinitionsNode.newChild(securityDefinitionsDef.property("scopes").get).flatMap(_.asElement).get
//      var scopesDef = scopesNode.definition
//      var scopesInnerNode = scopesNode.newChild(scopesDef.property("scopes").get).flatMap(_.asElement).get
//      var scopesInnerDef = scopesInnerNode.definition
//      runAttributeCreationTest1(project, project => {
//        Some(project.rootASTUnit.rootNode.elements("securityDefinitions").head.elements("scopes").head.elements("scopes").head)
//      },"description", "descriptionText")
//    })
//  }
//
//  test("ScopeObject name creation. JSON"){
//    var fp = "SecurityDefinitionObject/SecurityDefinitionObjectScopeEmpty.json"
//    parse(filePath(fp)).flatMap(project=>{
//      var securityDefinitionsNode = project.rootASTUnit.rootNode.elements("securityDefinitions").head
//      var securityDefinitionsDef = securityDefinitionsNode.definition
//      var scopesNode = securityDefinitionsNode.newChild(securityDefinitionsDef.property("scopes").get).flatMap(_.asElement).get
//      var scopesDef = scopesNode.definition
//      var scopesInnerNode = scopesNode.newChild(scopesDef.property("scopes").get).flatMap(_.asElement).get
//      var scopesInnerDef = scopesInnerNode.definition
//      runAttributeCreationTest1(project, project => {
//        Some(project.rootASTUnit.rootNode.elements("securityDefinitions").head.elements("scopes").head.elements("scopes").head)
//      },"name", "admin")
//    })
//  }
//
//  test("ScopeObject description creation. JSON"){
//    var fp = "SecurityDefinitionObject/SecurityDefinitionObjectScopeEmpty.json"
//    parse(filePath(fp)).flatMap(project=>{
//      var securityDefinitionsNode = project.rootASTUnit.rootNode.elements("securityDefinitions").head
//      var securityDefinitionsDef = securityDefinitionsNode.definition
//      var scopesNode = securityDefinitionsNode.newChild(securityDefinitionsDef.property("scopes").get).flatMap(_.asElement).get
//      var scopesDef = scopesNode.definition
//      var scopesInnerNode = scopesNode.newChild(scopesDef.property("scopes").get).flatMap(_.asElement).get
//      var scopesInnerDef = scopesInnerNode.definition
//      runAttributeCreationTest1(project, project => {
//        Some(project.rootASTUnit.rootNode.elements("securityDefinitions").head.elements("scopes").head.elements("scopes").head)
//      },"description", "descriptionText")
//    })
//  }
}
