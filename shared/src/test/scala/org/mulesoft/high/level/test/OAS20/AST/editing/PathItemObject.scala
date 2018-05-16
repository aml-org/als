package org.mulesoft.high.level.test.OAS20.AST.editing

import org.mulesoft.high.level.test.OAS20.OAS20ASTEditingTest

class PathItemObject extends OAS20ASTEditingTest{

    test("Path Item Object 'path' editing. YAML"){
        runAttributeEditingTest("PathItemObject/PathItemObject.yml", project => {
            project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.attribute("path")
        }, "/updatedPathValue")
    }

    test("Path Item Object 'path' editing. JSON"){
        runAttributeEditingTest("PathItemObject/PathItemObject.json", project => {
            project.rootASTUnit.rootNode.element("paths").get.elements("paths").head.attribute("path")
        }, "/updatedPathValue")
    }
}
