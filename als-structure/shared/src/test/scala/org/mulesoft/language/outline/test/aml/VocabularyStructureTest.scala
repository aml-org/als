package org.mulesoft.language.outline.test.aml

class VocabularyStructureTest extends DialectStructureTest {

  test("Test complete vocabulary") {
    this.runTest("vocabulary/vocabulary.yaml", "vocabulary/vocabulary.json")
  }

}
