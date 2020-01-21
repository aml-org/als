package org.mulesoft.als.suggestions

import org.mulesoft.als.suggestions.test.SuggestionsTest

class InMemorySuggestionTest extends SuggestionsTest {

  test("Test suggestion over json non aml document") {
    runSuggestionTest("generic.json", Set.empty)
  }

  test("Test suggestion over yaml non aml document") {
    runSuggestionTest("generic.yaml", Set.empty)
  }

  override def rootPath: String = "no-document/"
}
