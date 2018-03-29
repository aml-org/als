package org.mulesoft.als.suggestions.test.oas

import org.mulesoft.als.suggestions.test.OASTest

class StructureTests extends OASTest {

  test("test"){
    this.runTest("test01.yaml", Set("operationId"))
  }
}
