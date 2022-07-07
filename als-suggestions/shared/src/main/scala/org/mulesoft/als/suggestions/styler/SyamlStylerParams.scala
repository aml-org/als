package org.mulesoft.als.suggestions.styler

import org.mulesoft.als.common.{ASTPartBranch, YPartBranch}
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.lsp.configuration.FormatOptions

case class SyamlStylerParams(prefix: String,
                             yPartBranch: YPartBranch,
                             position: Position,
                             supportSnippets: Boolean,
                             indentation: Int,
                             formattingConfiguration: FormatOptions) {}

object SyamlStylerParams {
  def apply(prefix: String,
            position: Position,
            yPartBranch: YPartBranch,
            formattingConfiguration: FormatOptions,
            indentation: Int = 0,
            supportSnippets: Boolean = true): SyamlStylerParams = {

    SyamlStylerParams(prefix,
      yPartBranch,
                 position,
                 supportSnippets,
                 indentation,
                 formattingConfiguration)
  }
}
