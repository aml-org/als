package org.mulesoft.als.suggestions.test.oas20

import amf.core.internal.remote.{Hint, Oas20JsonHint}
import org.mulesoft.als.suggestions.test.SuggestionByDirectoryTest

class Oas20JsonSuggestionTestByDirectory extends SuggestionByDirectoryTest {
  override def basePath: String = "als-suggestions/shared/src/test/resources/test/oas20/by-directory/json"

  override def origin: Hint = Oas20JsonHint

  override def fileExtensions: Seq[String] = Seq(".json")
}
