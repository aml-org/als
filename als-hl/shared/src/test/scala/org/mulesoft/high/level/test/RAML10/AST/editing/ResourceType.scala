package org.mulesoft.high.level.test.RAML10.AST.editing

import org.mulesoft.high.level.test.RAML10.RAML10ASTEditingTest

class ResourceType extends RAML10ASTEditingTest {

    test("ResourceType 'name' editing") {
        runAttributeEditingTest("ResourceType/resource_type_name.raml", project => {
            project.rootASTUnit.rootNode.elements("resourceTypes").head.attribute("name")
        }, "updatedResourceTypeName")
    }

}
