package org.mulesoft.als.suggestions.styler

import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.configuration.AlsConfigurationReader
import org.mulesoft.lsp.configuration.FormatOptions

object SuggestionStylerBuilder {
  def build(
      isYAML: Boolean,
      prefix: String,
      position: Position,
      yPartBranch: YPartBranch,
      configuration: AlsConfigurationReader,
      snippetsSupport: Boolean = true,
      mimeType: Option[String] = None,
      indentation: Int = 0
  ): SuggestionRender = {

    val formatOptions: FormatOptions = configuration.getFormatOptionForMime(
      mimeType.getOrElse("default")
    )

    val params =
      StylerParams.apply(prefix: String, position: Position, yPartBranch, formatOptions, indentation, snippetsSupport)

    if (isYAML) YamlSuggestionStyler(params)
    else JsonSuggestionStyler(params)
  }
}
