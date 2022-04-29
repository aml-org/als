package org.mulesoft.language.outline.test.aml

class MetaDialectStructureTest extends DialectStructureTest {

  test("Test simple but complete dialect") {
    this.runTest("meta-dialect/complete.yaml", "meta-dialect/complete.json")
  }

  test("Test more complex dialect") {
    this.runTest("complex-instance-with-dialect/dialect.yaml", "complex-instance-with-dialect/dialect.json")
  }

  test("Test dialect instance") {
    this.runTest(
      "complex-instance-with-dialect/instance.yaml",
      "complex-instance-with-dialect/dialect.yaml",
      "complex-instance-with-dialect/instance.json"
    )
  }

}
