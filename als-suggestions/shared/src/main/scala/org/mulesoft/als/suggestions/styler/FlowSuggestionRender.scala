package org.mulesoft.als.suggestions.styler

import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.suggestions.styler.astbuilder.AstRawBuilder

trait FlowSuggestionRender extends SuggestionRender {

  protected val useSpaces: Boolean = params.formattingConfiguration.insertSpaces
  private val isFlow: Boolean      = params.yPartBranch.isInFlow
  protected val escapeChar: String = ""

  protected def fix(builder: AstRawBuilder, rendered: String): String =
    new SuggestionFix(builder, rendered).fix()

  sealed class SuggestionFix(builder: AstRawBuilder, rendered: String) {

    private val indent: String = if (useSpaces) " " * tabSize else "\t"

    private val cursorPosition: String = escapeChar + "$1" + escapeChar

    def fix(): String = collectionSeparator(fixFlow())

    private def fixFlow(): String = {
      val result = rendered.stripSuffix("\n")
      if (result.endsWith("{}")) {
        if (isFlow) {
          builder.forSnippet()
          result.replace("{}", "{\n" + indent + cursorPosition + "\n}")
        } else {
          result.stripSuffix(" {}") + "\n" + indent
        }
      } else if (result.endsWith("[\n \n]") && isFlow) {
        builder.forSnippet()
        result.replace("[\n \n]", "[\n" + indent + cursorPosition + "\n]")
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

    private def hasBrotherAfterwards(yPartBranch: YPartBranch): Boolean =
      yPartBranch.brothers.exists(brother =>
        PositionRange(brother.range).end > Position(yPartBranch.position)
      ) && yPartBranch.isKey
  }
}
