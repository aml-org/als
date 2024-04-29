package org.mulesoft.als.suggestions.styler

import org.mulesoft.als.common.{ASTPartBranch, YPartBranch}
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.configuration.AlsConfigurationReader
import org.mulesoft.lsp.configuration.FormatOptions

object SuggestionStylerBuilder {
  def build(
      isYAML: Boolean,
      prefix: String,
      position: Position,
      astBranch: ASTPartBranch,
      configuration: AlsConfigurationReader,
      snippetsSupport: Boolean = true,
      mimeType: Option[String] = None,
      indentation: Int = 0
  ): SuggestionRender = {

    val formatOptions: FormatOptions = configuration.getFormatOptionForMime(
      mimeType.getOrElse("default")
    )
    astBranch match {
      case yPartBranch: YPartBranch =>
        val params =
          SyamlStylerParams.apply(
            prefix: String,
            position: Position,
            yPartBranch,
            formatOptions,
            indentation,
            snippetsSupport
          )

        if (isYAML) YamlSuggestionStyler(params)
        else JsonSuggestionStyler(params)
      case _ => DummySuggestionStyle(prefix, position)
    }
  }
}
