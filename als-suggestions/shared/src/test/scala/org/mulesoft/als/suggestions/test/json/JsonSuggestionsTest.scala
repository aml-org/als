package org.mulesoft.als.suggestions.test.json

import amf.core.internal.remote.{AmfJsonHint, Hint}
import org.mulesoft.als.suggestions.test.SuggestionByDirectoryTest

class JsonSuggestionsTest extends SuggestionByDirectoryTest {
  override def basePath: String = "als-suggestions/shared/src/test/resources/test/json"

  override def fileExtensions: Seq[String] = Seq(".json")

  override def origin: Hint = AmfJsonHint
}
