package org.mulesoft.als.suggestions.test.aml

class KeyPropertyTest extends AMLSuggestionsTest {
  override def rootPath: String = "AML/keyPropertyDialects"

  test("KeyProperty - suggests structure") {
    withDialect("instances/instance01.yaml", Set("title: ", "version: "), "dialects/dialect01.yaml")
  }
}
