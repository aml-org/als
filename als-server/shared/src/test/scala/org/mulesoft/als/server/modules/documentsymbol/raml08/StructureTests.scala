package org.mulesoft.als.server.modules.documentsymbol.raml08

import org.mulesoft.als.server.modules.documentsymbol.AlsStructureTest

class StructureTests extends AlsStructureTest with RAML08Test {

  test("/test 003") {
    runTest("structure/test003/api.raml", "structure/test003/api-outline.json")
  }

  test("/test 005") {
    runTest("structure/test005/api.raml", "structure/test005/api-outline.json")
  }
}
