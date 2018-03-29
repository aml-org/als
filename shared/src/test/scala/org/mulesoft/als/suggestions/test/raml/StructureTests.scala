package org.mulesoft.als.suggestions.test.raml

import org.mulesoft.als.suggestions.test.RAMLTest

class StructureTests extends RAMLTest {

  test("test"){
    this.runTest("test01.raml", Set("responses"))
  }
}
