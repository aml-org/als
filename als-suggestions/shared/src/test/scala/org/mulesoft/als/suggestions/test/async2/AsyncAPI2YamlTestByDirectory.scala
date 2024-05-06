package org.mulesoft.als.suggestions.test.async2

import amf.core.internal.remote.{Async20YamlHint, Hint}
import org.mulesoft.als.suggestions.test.SuggestionByDirectoryTest

class AsyncAPI2YamlTestByDirectory extends SuggestionByDirectoryTest {
  override def basePath: String = "als-suggestions/shared/src/test/resources/test/AsyncAPI2/by-directory"

  override def origin: Hint = Async20YamlHint

  override def fileExtensions: Seq[String] = Seq(".yaml")
}
