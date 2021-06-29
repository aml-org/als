package org.mulesoft.als.suggestions.test.oas20

import amf.core.internal.remote.{Hint, Oas20YamlHint}
import org.mulesoft.als.suggestions.test.SuggestionByDirectoryTest

class Oas20YamlSuggestionTestByDirectory extends SuggestionByDirectoryTest {
  override def basePath: String = "als-suggestions/shared/src/test/resources/test/oas20/by-directory/yaml"

  override def origin: Hint = Oas20YamlHint

  override def fileExtensions: Seq[String] = Seq(".yml")
}
