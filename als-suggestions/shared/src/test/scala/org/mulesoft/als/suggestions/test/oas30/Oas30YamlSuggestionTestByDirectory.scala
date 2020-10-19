package org.mulesoft.als.suggestions.test.oas30

import amf.core.remote.{Hint, OasYamlHint}
import org.mulesoft.als.suggestions.test.SuggestionByDirectoryTest

class Oas30YamlSuggestionTestByDirectory extends SuggestionByDirectoryTest {
  override def basePath: String = "als-suggestions/shared/src/test/resources/test/oas30/by-directory/yaml"

  override def origin: Hint = OasYamlHint

  override def fileExtensions: Seq[String] = Seq(".yaml")
}
