package org.mulesoft.als.suggestions.styler

import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.configuration.{AlsConfigurationReader, AlsFormatMime, AlsFormattingOptions}
import org.mulesoft.als.suggestions.patcher.PatchedContent

object SuggestionStylerBuilder {
  def build(isYAML: Boolean,
            prefix: String,
            patchedContent: PatchedContent,
            position: Position,
            yPartBranch: YPartBranch,
            configuration: AlsConfigurationReader,
            snippetsSupport: Boolean = true,
            indentation: Int = 0): SuggestionRender = {

    val formatOptions: AlsFormattingOptions = configuration.getFormattingOptions(
      if (isYAML) AlsFormatMime.YAML else AlsFormatMime.JSON
    )

    val params =
      StylerParams.apply(prefix: String,
                         patchedContent,
                         position: Position,
                         yPartBranch,
                         formatOptions,
                         indentation,
                         snippetsSupport)

    if (isYAML) YamlSuggestionStyler(params)
    else JsonSuggestionStyler(params)
  }
}
