package org.mulesoft.als.suggestions.test.jsonschema

import amf.core.internal.remote.Spec.JSONSCHEMA
import amf.core.internal.remote.Syntax.Json
import amf.core.internal.remote.{Hint, Raml08YamlHint}
import org.mulesoft.als.suggestions.test.SuggestionByDirectoryTest

class JsonSchema4SuggestionTestByDirectory extends SuggestionByDirectoryTest {
  override def basePath: String =
    "als-suggestions/shared/src/test/resources/test/jsonschema/draft4/by-directory"

  override def origin: Hint = Hint(JSONSCHEMA, Json)

  override def fileExtensions: Seq[String] = Seq(".json")
}
