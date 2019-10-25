package org.mulesoft.als.suggestions.styler

import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.suggestions.patcher.PatchedContent

case class StylerParams(prefix: String,
                        yPartBranch: YPartBranch,
                        patchedContent: PatchedContent,
                        position: Position,
                        supportSnippets: Boolean,
                        indentation: Int) {}

object StylerParams {
  def apply(prefix: String,
            patchedContent: PatchedContent,
            position: Position,
            yPartBranch: YPartBranch,
            supportSnippets: Boolean = true,
            indentation: Int = 0): StylerParams = {

    StylerParams(prefix, yPartBranch, patchedContent: PatchedContent, position, supportSnippets, indentation)
  }
}
