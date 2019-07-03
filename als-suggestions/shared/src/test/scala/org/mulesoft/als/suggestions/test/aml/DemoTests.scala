package org.mulesoft.als.suggestions.test.aml

import amf.ProfileName
import org.mulesoft.als.suggestions.BaseCompletionPluginsRegistryAML

class DemoTests extends AMLSuggestionsTest {
  override def rootPath: String = "AML/demo"

  test("test suggest offices") {
    this.withDialect(
      "visit24.yaml",
      Set("Pilar", "BA", "SFO", "Chicago", "Palo Alto"),
      "dialect.yaml",
      ProfileName("Mark Visit 1.0"),
      BaseCompletionPluginsRegistryAML.get()
    )
  }
}
