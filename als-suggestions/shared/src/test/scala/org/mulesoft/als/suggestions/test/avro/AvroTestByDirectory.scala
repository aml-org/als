package org.mulesoft.als.suggestions.test.avro

import amf.core.internal.remote.{AvroHint, Hint}
import org.mulesoft.als.suggestions.test.SuggestionByDirectoryTest

class AvroTestByDirectory extends SuggestionByDirectoryTest {
  override def basePath: String = "als-suggestions/shared/src/test/resources/test/avro/by-directory"

  override def origin: Hint = AvroHint

  override def fileExtensions: Seq[String] = Seq(".avsc")
}
