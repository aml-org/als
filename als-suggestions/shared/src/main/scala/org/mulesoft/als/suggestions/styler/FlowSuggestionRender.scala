package org.mulesoft.als.suggestions.styler

import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.suggestions.styler.astbuilder.AstRawBuilder

trait FlowSuggestionRender extends SuggestionRender {

  protected val useSpaces: Boolean = params.formattingConfiguration.insertSpaces
  private val isFlow: Boolean      = params.yPartBranch.isInFlow
  protected val escapeChar: String = ""

  def fix(builder: AstRawBuilder, rendered: String): String =
    new SuggestionFix(builder, rendered).fix()

  sealed class SuggestionFix(builder: AstRawBuilder, rendered: String) {

    private val indent: String = if (useSpaces) " " * tabSize else "\t"

    private val cursorPosition: String = escapeChar + "$1" + escapeChar

    def fix(): String = collectionSeparator(fixMap())

    private def fixMap(): String = {
      val result = rendered.stripSuffix("\n")
      if (result.endsWith("{}")) {
        if (isFlow) {
          builder.forSnippet()
          result.replace("{}", "{\n" + indent + cursorPosition + "\n}")
        } else {
          result.stripSuffix(" {}") + "\n" + indent
        }
      } else result
    }

    private def shouldEmitPosition: Boolean = !builder.asSnippet

    private def collectionSeparator(s: String): String = {
      if (isFlow && hasBrotherAfterwards(params.yPartBranch)) {
        (if (shouldEmitPosition) {
           builder.forSnippet()
           s + " " + cursorPosition
         } else s) + ","
      } else s
    }

    private def hasBrotherAfterwards(yPartBranch: YPartBranch): Boolean = {
      val range = yPartBranch.node.range
      yPartBranch.brothers.exists(brother => brother.range.compareTo(range) > 0) && yPartBranch.isKey
    }
  }
}
