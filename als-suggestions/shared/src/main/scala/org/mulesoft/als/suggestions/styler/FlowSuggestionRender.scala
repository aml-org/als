package org.mulesoft.als.suggestions.styler

import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.suggestions.styler.astbuilder.AstRawBuilder
import org.mulesoft.als.suggestions.{RawSuggestion, SuggestionStructure}
import org.yaml.model.{YNode, YType}

trait FlowSuggestionRender extends SuggestionRender {

  protected val useSpaces: Boolean = params.formattingConfiguration.insertSpaces
  private val isFlow: Boolean      = params.yPartBranch.strict
  protected val escapeChar: String = ""

  protected def fix(builder: AstRawBuilder, rendered: String): String =
    new SuggestionFix(builder, rendered).fix()

  sealed class SuggestionFix(builder: AstRawBuilder, rendered: String) {

    private val indent: String = if (useSpaces) " " * tabSize else "\t"

    private val cursorPosition: String = escapeChar + "$1" + escapeChar

    def fix(): String = collectionSeparator(fixFlow())

    /** This method fix the indentation when the object is inside of an Array
      * @param raw
      *   the [[RawSuggestion]]
      * @return
      *   the final indent
      */
    def fixIndent(raw: RawSuggestion): String =
      if (raw.options.isObject && params.yPartBranch.isInArray && isInSameLine())
        indent * 2
      else indent

    /** This method is post condition when the suggestion is an object and is inside an Array and it evaluate if the
      * suggestion is asked in the same line of beginning node.
      *
      * @return
      *   true if line position is same as lineFrom of the [[org.mulesoft.common.client.lexical.PositionRange]] of the
      *   node
      */
    def isInSameLine(): Boolean =
      params.yPartBranch.position.line == params.yPartBranch.node.range.lineFrom

    private def fixFlow(): String = {
      val result = rendered.stripSuffix("\n")
      if (result.endsWith("{}")) {
        if (isFlow) {
          builder.forSnippet()
          result.replace("{}", "{\n" + indent + cursorPosition + "\n}")
        } else {
          result.stripSuffix(" {}") + "\n" + fixIndent(builder.raw)
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

  private def isRecoveredValue(): Boolean = {
    params.yPartBranch.node match {
      case n: YNode => n.tagType == YType.Null
      case _        => false
    }
  }

  override def adaptRangeToPositionValue(r: PositionRange, option: SuggestionStructure): PositionRange = {
    if (!option.isKey && isFlow && isRecoveredValue()) {
      r.copy(start = params.position)
    } else
      r
  }
}
