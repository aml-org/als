package org.mulesoft.als.suggestions.test.AsyncAPI2

import amf.core.remote.{Hint, AsyncYamlHint}
import org.mulesoft.als.suggestions.test.SuggestionByDirectoryTest

class AsyncAPI2YamlTestByDirectory extends SuggestionByDirectoryTest {
  override def basePath: String = "als-suggestions/shared/src/test/resources/test/AsyncAPI2/by-directory"

  override def origin: Hint = AsyncYamlHint

  override def fileExtensions: Seq[String] = Seq(".yaml")
}
