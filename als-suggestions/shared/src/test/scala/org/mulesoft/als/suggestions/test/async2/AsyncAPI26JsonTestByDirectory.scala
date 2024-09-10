package org.mulesoft.als.suggestions.test.async2

import amf.core.internal.remote.{Async20JsonHint, Hint}
import org.mulesoft.als.suggestions.test.SuggestionByDirectoryTest

class AsyncAPI26JsonTestByDirectory extends SuggestionByDirectoryTest {

  override def basePath: String = "als-suggestions/shared/src/test/resources/test/async26-json/by-directory"

  override def origin: Hint = Async20JsonHint

  override def fileExtensions: Seq[String] = Seq(".json")

}
