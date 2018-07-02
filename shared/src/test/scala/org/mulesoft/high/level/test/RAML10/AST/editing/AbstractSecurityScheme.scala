package org.mulesoft.high.level.test.RAML10.AST.editing

import org.mulesoft.high.level.test.RAML10.RAML10ASTEditingTest

class AbstractSecurityScheme extends RAML10ASTEditingTest{
  test("AbstractSecurityScheme name editing") {
    runAttributeEditingTest("SecurityScheme/abstractSecurityScheme.raml", project => {
      project.rootASTUnit.rootNode.elements("securitySchemes").head.attribute("name")
    }, "oa2")
  }

  test("AbstractSecurityScheme type editing" ) {
    runAttributeEditingTest("SecurityScheme/abstractSecurityScheme.raml", project => {
      project.rootASTUnit.rootNode.elements("securitySchemes").head.attribute("type")
    }, "OAuth 1.0")
  }

  test("AbstractSecurityScheme description editing" ) {
    runAttributeEditingTest("SecurityScheme/abstractSecurityScheme.raml", project => {
      project.rootASTUnit.rootNode.elements("securitySchemes").head.attribute("description")
    }, "text")
  }

  test("AbstractSecurityScheme describedBy editing" ) {
    runAttributeEditingTest("SecurityScheme/abstractSecurityScheme.raml", project => {
      project.rootASTUnit.rootNode.elements("securitySchemes").head.elements("describedBy").head.element("headers").head.attribute("name")
    },"If-Range")
  }

  test("AbstractSecurityScheme displayName editing" ) {
    runAttributeEditingTest("SecurityScheme/abstractSecurityScheme.raml", project => {
      project.rootASTUnit.rootNode.elements("securitySchemes").head.attribute("displayName")
    }, "text")
  }

  test("AbstractSecurityScheme settings editing" ) {
    runAttributeEditingTest("SecurityScheme/abstractSecurityScheme.raml", project => {
      project.rootASTUnit.rootNode.elements("securitySchemes").head.elements("settings").head.attribute("accessTokenUri")
    }, "b")
  }
  //abstractSecuritySchemeCreation.raml
  test("AbstractSecurityScheme name creation") {
    var fp = "Api/api_empty.raml"
    parse(filePath(fp)).flatMap(project=>{
      var apiNode = project.rootASTUnit.rootNode
      var apiDef = apiNode.definition
      var typeNode = apiNode.newChild(apiDef.property("securitySchemes").get).flatMap(_.asElement).get
      runAttributeCreationTest1(project, project => {
        Some(project.rootASTUnit.rootNode.elements("securitySchemes").head)
      },"name","oa2")
    })
  }

  test("AbstractSecurityScheme type creation" ) {
    runAttributeCreationTest("SecurityScheme/abstractSecuritySchemeCreation.raml", project => {
      Option(project.rootASTUnit.rootNode.elements("securitySchemes").head)
    }, "type", "OAuth 2.0")
  }

  test("AbstractSecurityScheme description creation" ) {
    runAttributeCreationTest("SecurityScheme/abstractSecuritySchemeCreation.raml", project => {
      Option(project.rootASTUnit.rootNode.elements("securitySchemes").head)
    },"description", "text")
  }

  test("AbstractSecurityScheme describedBy creation" ) {
    parse(filePath("SecurityScheme/abstractSecuritySchemeCreation.raml")).flatMap(project=>{
      var apiNode = project.rootASTUnit.rootNode.elements("securitySchemes").head.elements("describedBy").head
      var apiDef = apiNode.definition
      var typeNode = apiNode.newChild(apiDef.property("headers").get).flatMap(_.asElement).get
      runAttributeCreationTest1(project, project => {
        Some(project.rootASTUnit.rootNode.elements("securitySchemes").head.elements("describedBy").head.elements("headers")(1))
      },"name","If-Range")
    })
  }

  test("AbstractSecurityScheme displayName creation" ) {
    runAttributeCreationTest("SecurityScheme/abstractSecuritySchemeCreation.raml", project => {
      Option(project.rootASTUnit.rootNode.elements("securitySchemes").head)
    },"displayName", "text")
  }
}
