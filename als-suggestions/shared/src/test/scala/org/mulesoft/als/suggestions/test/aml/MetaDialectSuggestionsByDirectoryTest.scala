package org.mulesoft.als.suggestions.test.aml

import amf.core.internal.remote.{Hint, VocabularyYamlHint}
import org.mulesoft.als.suggestions.test.SuggestionByDirectoryTest

class MetaDialectSuggestionsByDirectoryTest extends SuggestionByDirectoryTest {
  override def basePath: String = "als-suggestions/shared/src/test/resources/test/AML/meta-dialect"

  override def origin: Hint = VocabularyYamlHint

  override def fileExtensions: Seq[String] = Seq(".yaml")
}
