package org.mulesoft.high.level.test.RAML10.AST.editing

import org.mulesoft.high.level.test.RAML10.RAML10ASTEditingTest

class SecurityScheme extends RAML10ASTEditingTest {

    test("SecurityScheme 'name' editing") {
        runAttributeEditingTest("SecurityScheme/security_scheme_name.raml", project => {
            project.rootASTUnit.rootNode.elements("securitySchemes")(1).attribute("name")
        }, "updatedSecuritySchemeName")
    }

}
