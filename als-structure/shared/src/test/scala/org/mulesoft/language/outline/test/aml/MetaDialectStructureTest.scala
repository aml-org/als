package org.mulesoft.language.outline.test.aml

class MetaDialectStructureTest extends DialectStructureTest {

  test("Test simple but complete dialect") {
    this.runTest("meta-dialect/complete.yaml", "meta-dialect/complete.json")
  }

}
