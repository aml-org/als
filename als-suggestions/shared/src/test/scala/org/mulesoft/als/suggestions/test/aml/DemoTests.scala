package org.mulesoft.als.suggestions.test.aml

import amf.ProfileName

class DemoTests extends AMLSuggestionsTest {
  override def rootPath: String = "AML/demo"

  test("test suggest offices") {
    this.withDialect(
      "visit24.yaml",
      Set("Pilar", "BA", "SFO", "Chicago", "Palo Alto"),
      "dialect.yaml",
      ProfileName("Mark Visit 1.0")
    )
  }

  test("test suggest map") {
    this.withDialect(
      "visit242.yaml",
      Set("Pilar", "BA", "SFO", "Chicago", "Palo Alto"),
      "dialect.yaml",
      ProfileName("Mark Visit 1.0")
    )
  }
}
