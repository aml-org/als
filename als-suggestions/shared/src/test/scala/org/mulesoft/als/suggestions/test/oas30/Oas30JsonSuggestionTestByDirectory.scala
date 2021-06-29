package org.mulesoft.als.suggestions.test.oas30

import amf.core.internal.remote.{Hint, Oas30JsonHint}
import org.mulesoft.als.suggestions.test.SuggestionByDirectoryTest

class Oas30JsonSuggestionTestByDirectory extends SuggestionByDirectoryTest {

  override def basePath: String = "als-suggestions/shared/src/test/resources/test/oas30/by-directory/json"

  override def origin: Hint = Oas30JsonHint

  override def fileExtensions: Seq[String] = Seq(".json")
}
