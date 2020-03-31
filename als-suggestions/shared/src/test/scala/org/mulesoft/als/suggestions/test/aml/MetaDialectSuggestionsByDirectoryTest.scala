package org.mulesoft.als.suggestions.test.aml

import amf.core.remote.{Hint, VocabularyYamlHint}
import org.mulesoft.als.suggestions.test.SuggestionByDirectoryTest

class MetaDialectSuggestionsByDirectoryTest extends SuggestionByDirectoryTest {
  override def basePath: String = "als-suggestions/shared/src/test/resources/test/AML/meta-dialect"

  override def origin: Hint = VocabularyYamlHint

  override def fileExtension: String = ".yaml"
}
