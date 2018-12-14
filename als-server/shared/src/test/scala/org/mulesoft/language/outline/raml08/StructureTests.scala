package org.mulesoft.language.outline.raml08

import org.mulesoft.language.outline.StructureTest

class StructureTests extends StructureTest with RAML08Test  {

    test("/test 003") {
        runTest("structure/test003/api.raml", "structure/test003/api-outline.json")
    }

    test("/test 005") {
        runTest("structure/test005/api.raml", "structure/test005/api-outline.json")
    }
}
