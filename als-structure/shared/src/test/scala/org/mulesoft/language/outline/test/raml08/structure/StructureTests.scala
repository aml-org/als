package org.mulesoft.language.outline.test.raml08.structure

import org.mulesoft.language.outline.test.raml08.RAML08StructureTest

class StructureTests extends RAML08StructureTest {

  override def rootPath: String = "RAML08/structure"

  ignore("test 003") {
    this.runTest("test003/api.raml", "test003/api-outline.json")
  }

  ignore("test 005") {
    this.runTest("test005/api.raml", "test005/api-outline.json")
  }

  test("test 028") {
    this.runTest("test028/api.raml", "test028/api-outline.json")
  }

  test("test 029") {
    this.runTest("test029/api.raml", "test029/api-outline.json")
  }

  test("test 030") {
    this.runTest("test030/api.raml", "test030/api-outline.json")
  }

  test("test 031") {
    this.runTest("test031/api.raml", "test031/api-outline.json")
  }

  test("inlined arrays") {
    this.runTest("inlined-array/api.raml", "inlined-array/api-outline.json")
  }

  test("!included example") {
    this.runTest("included-example/api.raml", "included-example/api-outline.json")
  }

}
