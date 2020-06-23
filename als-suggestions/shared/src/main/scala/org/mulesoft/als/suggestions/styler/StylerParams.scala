package org.mulesoft.als.suggestions.styler

import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.configuration.AlsFormatOptions
import org.mulesoft.als.suggestions.patcher.PatchedContent

case class StylerParams(prefix: String,
                        yPartBranch: YPartBranch,
                        patchedContent: PatchedContent,
                        position: Position,
                        supportSnippets: Boolean,
                        indentation: Int,
                        formattingConfiguration: AlsFormatOptions) {}

object StylerParams {
  def apply(prefix: String,
            patchedContent: PatchedContent,
            position: Position,
            yPartBranch: YPartBranch,
            formattingConfiguration: AlsFormatOptions,
            indentation: Int = 0,
            supportSnippets: Boolean = true): StylerParams = {

    StylerParams(prefix,
                 yPartBranch,
                 patchedContent: PatchedContent,
                 position,
                 supportSnippets,
                 indentation,
                 formattingConfiguration)
  }
}
