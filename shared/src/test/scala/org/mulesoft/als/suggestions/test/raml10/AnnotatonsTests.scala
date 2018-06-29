package org.mulesoft.als.suggestions.test.raml10

class AnnotatonsTests extends RAML10Test {
  test("test"){
    this.runTest("annotations/test01.raml", Set("Two"))
  }
}
