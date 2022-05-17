package org.mulesoft.als.suggestions.styler

import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.lsp.configuration.FormatOptions

case class StylerParams(
    prefix: String,
    yPartBranch: YPartBranch,
    position: Position,
    supportSnippets: Boolean,
    indentation: Int,
    formattingConfiguration: FormatOptions
) {}

object StylerParams {
  def apply(
      prefix: String,
      position: Position,
      yPartBranch: YPartBranch,
      formattingConfiguration: FormatOptions,
      indentation: Int = 0,
      supportSnippets: Boolean = true
  ): StylerParams = {

    StylerParams(prefix, yPartBranch, position, supportSnippets, indentation, formattingConfiguration)
  }
}
