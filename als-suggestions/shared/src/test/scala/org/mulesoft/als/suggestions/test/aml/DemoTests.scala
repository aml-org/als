package org.mulesoft.als.suggestions.test.aml

class DemoTests extends AMLSuggestionsTest {
  override def rootPath: String = "AML/demo"

  test("test suggest offices") {
    this.withDialect(
      "visit04.yaml",
      Set("Pilar", "BA", "SFO", "Chicago", "Palo Alto"),
      "dialect.yaml"
    )
  }

  test("test empty file") {
    this.withDialect(
      "empty.yaml",
      Set("#%Mark Visit 1.0"),
      "dialect.yaml"
    )
  }
}
