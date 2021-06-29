package org.mulesoft.als.suggestions.test.aml

class BooleanTests extends AMLSuggestionsTest {
  override def rootPath: String = "AML/booleans"

  test("test simple boolean") {
    this.withDialect(
      "instance.yaml",
      Set("true"),
      "dialect.yaml"
    )
  }
}
