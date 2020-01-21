package org.mulesoft.language.outline.test.nodocument

import org.mulesoft.language.outline.test.BaseStructureTest

class NoDocumentStructureTest extends BaseStructureTest {

  override def rootPath: String = "no-document/"

  test("test json generic structure") {
    this.runTest("json/api.json", "json/api-json-outline.json")
  }

  test("test yaml generic structure") {
    this.runTest("yaml/api.yaml", "yaml/api-yaml-outline.json")
  }
}
