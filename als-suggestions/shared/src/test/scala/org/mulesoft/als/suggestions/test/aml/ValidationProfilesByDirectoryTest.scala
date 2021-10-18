package org.mulesoft.als.suggestions.test.aml

import amf.core.internal.remote.{Hint, VocabularyYamlHint}
import org.mulesoft.als.suggestions.test.SuggestionByDirectoryTest

class ValidationProfilesByDirectoryTest extends SuggestionByDirectoryTest {
  override def basePath: String = "als-suggestions/shared/src/test/resources/test/AML/validation-profile"

  override def origin: Hint = VocabularyYamlHint

  override def fileExtensions: Seq[String] = Seq(".yaml")
}
