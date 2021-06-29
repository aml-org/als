package org.mulesoft.als.suggestions.test.raml10

import amf.core.internal.remote.{Hint, Raml10YamlHint}
import org.mulesoft.als.suggestions.test.SuggestionByDirectoryTest

class Raml10SuggestionTestByDirectory extends SuggestionByDirectoryTest {
  override def basePath: String = "als-suggestions/shared/src/test/resources/test/raml10/by-directory"

  override def origin: Hint = Raml10YamlHint

  override def fileExtensions: Seq[String] = Seq(".raml")
}
