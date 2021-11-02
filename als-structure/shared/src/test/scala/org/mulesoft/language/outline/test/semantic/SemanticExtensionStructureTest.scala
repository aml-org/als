package org.mulesoft.language.outline.test.semantic

import org.mulesoft.language.outline.test.aml.DialectStructureTest

class SemanticExtensionStructureTest extends DialectStructureTest {

  test("Test simple RAML") {
    this.runTest("raml/simple1.raml", "dialects/extension.smx", "raml/simple1.json")
  }

  test("Test simple OAS2") {
    this.runTest("oas2/simple1.yaml", "dialects/extension.smx", "oas2/simple1.json")
  }

  test("Test simple OAS3") {
    this.runTest("oas3/simple1.yaml", "dialects/extension.smx", "oas3/simple1.json")
  }

  test("Test simple ASYNC2") {
    this.runTest("async2/simple1.yaml", "dialects/extension.smx", "async2/simple1.json")
  }

  override def rootPath: String = "semantic"
}
