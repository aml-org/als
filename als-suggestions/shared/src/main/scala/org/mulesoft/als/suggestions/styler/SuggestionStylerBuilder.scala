package org.mulesoft.als.suggestions.styler

import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.suggestions.patcher.PatchedContent

object SuggestionStylerBuilder {
  def build(isYAML: Boolean,
            prefix: String,
            patchedContent: PatchedContent,
            position: Position,
            yPartBranch: YPartBranch,
            snippetsSupport: Boolean = true,
            indentation: Int = 0): SuggestionRender = {
    val params =
      StylerParams.apply(prefix: String, patchedContent, position: Position, yPartBranch, snippetsSupport, indentation)

    if (isYAML) YamlSuggestionStyler(params)
    else JsonSuggestionStyler(params)
  }
}
