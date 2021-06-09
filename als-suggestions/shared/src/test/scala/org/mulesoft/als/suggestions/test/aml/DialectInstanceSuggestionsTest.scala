package org.mulesoft.als.suggestions.test.aml

class DialectInstanceSuggestionsTest extends SuggestionsWithDialectTest {
  override def rootPath: String = "AML/instances"

  test("Suggest for node with classTerm") {
    runTest("test001.yaml", "dialect01.yaml")
  }

  test("Suggest for node without classTerm") {
    runTest("test002.yaml", "dialect01.yaml")
  }

  test("Suggest for node without classTerm at root level") {
    runTest("test003.yaml", "dialect01.yaml")
  }
}
