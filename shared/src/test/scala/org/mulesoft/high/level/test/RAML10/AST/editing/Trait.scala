package org.mulesoft.high.level.test.RAML10.AST.editing

import org.mulesoft.high.level.test.RAML10.RAML10ASTEditingTest

class Trait extends RAML10ASTEditingTest {

    test("Trait 'name' editing") {
        runAttributeEditingTest("Trait/trait_name.raml", project => {
            project.rootASTUnit.rootNode.elements("traits").head.attribute("name")
        }, "updatedTraitName")
    }

}
