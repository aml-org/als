package org.mulesoft.als.suggestions.test.oas30

import amf.core.internal.remote.{Hint, Oas30YamlHint}
import org.mulesoft.als.configuration.TemplateTypes
import org.mulesoft.als.configuration.TemplateTypes.TemplateTypes
import org.mulesoft.als.suggestions.test.TemplateSuggestionByDirectoryTest

class Oas30FullTemplateSuggestionTestByDirectory extends TemplateSuggestionByDirectoryTest {

  override def basePath: String = "als-suggestions/shared/src/test/resources/test/oas30/by-directory/templates"

  override def origin: Hint = Oas30YamlHint

  override def fileExtensions: Seq[String] = Seq(".yaml")

  override def templateType: TemplateTypes = TemplateTypes.FULL
}
