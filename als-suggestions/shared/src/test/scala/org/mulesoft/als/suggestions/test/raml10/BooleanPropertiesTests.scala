package org.mulesoft.als.suggestions.test.raml10

class BooleanPropertiesTests extends RAML10Test {
  test("XML.wrapped test 001") {
    this.runTest("booleanProperties/test001.raml", Set(" true", " false"))
  }

  test("XML.wrapped test 002") {
    this.runTest("booleanProperties/test002.raml", Set("true"))
  }

  test("XML.attribute test 001") {
    this.runTest("booleanProperties/test003.raml", Set("true", "false"))
  }

  test("XML.attribute test 002") {
    this.runTest("booleanProperties/test004.raml", Set("false"))
  }
}
