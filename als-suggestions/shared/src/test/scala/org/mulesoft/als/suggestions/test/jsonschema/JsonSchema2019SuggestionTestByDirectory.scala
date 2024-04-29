package org.mulesoft.als.suggestions.test.jsonschema

import amf.core.internal.remote.Hint
import amf.core.internal.remote.Spec.JSONSCHEMA
import amf.core.internal.remote.Syntax.Json
import org.mulesoft.als.suggestions.test.SuggestionByDirectoryTest

class JsonSchema2019SuggestionTestByDirectory extends SuggestionByDirectoryTest {
  override def basePath: String =
    "als-suggestions/shared/src/test/resources/test/jsonschema/draft2019/by-directory"

  override def origin: Hint = Hint(JSONSCHEMA, Json)

  override def fileExtensions: Seq[String] = Seq(".json")
}
