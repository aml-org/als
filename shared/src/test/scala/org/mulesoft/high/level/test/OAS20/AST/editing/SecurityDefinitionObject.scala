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
}
