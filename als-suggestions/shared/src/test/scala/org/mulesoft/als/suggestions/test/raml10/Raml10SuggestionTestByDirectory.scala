package org.mulesoft.als.suggestions.test.raml10

import amf.core.remote.{Hint, RamlYamlHint}
import org.mulesoft.als.suggestions.test.SuggestionByDirectoryTest

class Raml10SuggestionTestByDirectory extends SuggestionByDirectoryTest {
  override def basePath: String = "als-suggestions/shared/src/test/resources/test/raml10/by-directory"

  override def origin: Hint = RamlYamlHint

  override def fileExtensions: Seq[String] = Seq(".raml")
}
