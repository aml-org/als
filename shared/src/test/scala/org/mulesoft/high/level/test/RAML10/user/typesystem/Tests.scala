package org.mulesoft.high.level.test.RAML10.user.typesystem

import org.mulesoft.high.level.test.RAML10.RAML10TypesystemTest

class Tests extends RAML10TypesystemTest {

    test("Annotable annotations") {
        runTest("test001/api.raml", project => {

            var typeNode = project.rootASTUnit.rootNode.elements("types").head
            if (typeNode.localType.isDefined)
                succeed
            else
                fail("Local type is not defined for the node")
        })
    }
}
