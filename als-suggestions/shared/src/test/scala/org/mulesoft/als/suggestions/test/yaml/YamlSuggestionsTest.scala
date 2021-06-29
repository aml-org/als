package org.mulesoft.als.suggestions.test.yaml

import amf.core.internal.remote.{Hint, Oas20YamlHint}
import org.mulesoft.als.suggestions.test.SuggestionByDirectoryTest

class YamlSuggestionsTest extends SuggestionByDirectoryTest {
  override def basePath: String = "als-suggestions/shared/src/test/resources/test/yaml"

  override def fileExtensions: Seq[String] = Seq(".yaml")

  override def origin: Hint = Oas20YamlHint // not all are oas 2
}