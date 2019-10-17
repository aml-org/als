package org.mulesoft.als.suggestions.styler

import org.mulesoft.als.common.dtoTypes.Position

case class StylerParams(prefix: String,
                        hasQuote: Boolean,
                        hasColon: Boolean,
                        hasLine: Boolean,
                        hasKeyClosingQuote: Boolean,
                        hasOpeningQuote: Boolean,
                        position: Position,
                        supportSnippets: Boolean,
                        indentation: Int) {}

object StylerParams {
  def apply(prefix: String,
            originalContent: String,
            position: Position,
            supportSnippets: Boolean = true,
            indentation: Int = 0): StylerParams = {

    var hasQuote           = false
    var hasColon           = false
    var hasLine            = false
    var hasKeyClosingQuote = false

    val lines = originalContent.linesIterator drop position.line
    val lineOpt =
      if (lines hasNext) lines next
      else ""

    hasLine = true
    val tail = lineOpt substring position.column
    hasQuote = tail contains "\""
    val colonIndex = tail indexOf ":"
    hasColon = colonIndex >= 0
    if (colonIndex > 0)
      hasKeyClosingQuote = tail.substring(0, colonIndex).trim endsWith "\""
    else
      hasKeyClosingQuote = hasQuote

    StylerParams(prefix,
                 hasQuote,
                 hasColon,
                 hasLine,
                 hasKeyClosingQuote,
                 hasOpeningQuote(lineOpt, position),
                 position,
                 supportSnippets,
                 indentation)
  }
  private def hasOpeningQuote(lineOpt: String, position: Position) = {
    val prev = lineOpt.substring(0, position.column)
    prev.substring(0 max prev.indexOf(':') + 1).trim.startsWith("\"")
  }
}
