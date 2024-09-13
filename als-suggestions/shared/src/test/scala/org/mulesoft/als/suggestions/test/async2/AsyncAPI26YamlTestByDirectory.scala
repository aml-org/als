package org.mulesoft.als.suggestions.test.async2

import amf.core.internal.remote.{Async20YamlHint, Hint}
import org.mulesoft.als.suggestions.test.SuggestionByDirectoryTest

class AsyncAPI26YamlTestByDirectory extends SuggestionByDirectoryTest {
  override def basePath: String = "als-suggestions/shared/src/test/resources/test/async26/by-directory/messages/payload"

  override def origin: Hint = Async20YamlHint

  override def fileExtensions: Seq[String] = Seq(".yaml")
}
