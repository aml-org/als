package org.mulesoft.als.suggestions.test.raml

import org.mulesoft.als.suggestions.test.RAMLTest

class AnnotatonsTests extends RAMLTest {
  test("test"){
    this.runTest("annotations/test01.raml", Set("Two"))
  }
}
