package org.mulesoft.als.suggestions.test.oas30

import amf.core.remote.{Hint, OasJsonHint}
import org.mulesoft.als.suggestions.test.SuggestionByDirectoryTest

class Oas30JsonSuggestionTestByDirectory extends SuggestionByDirectoryTest {

  override def basePath: String = "als-suggestions/shared/src/test/resources/test/oas30/by-directory/json"

  override def origin: Hint = OasJsonHint

  override def fileExtensions: Seq[String] = Seq(".json")
}
