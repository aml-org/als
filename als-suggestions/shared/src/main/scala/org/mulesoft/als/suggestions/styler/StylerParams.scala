package org.mulesoft.als.suggestions.styler

import org.mulesoft.als.common.dtoTypes.Position

case class StylerParams(prefix: String,
                        hasQuote: Boolean,
                        hasColon: Boolean,
                        hasLine: Boolean,
                        hasKeyClosingQuote: Boolean,
                        position: Position,
                        supportSnippets: Boolean) {}

object StylerParams {
  def apply(prefix: String,
            originalContent: String,
            position: Position,
            supportSnippets: Boolean = true): StylerParams = {

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

    StylerParams(prefix, hasQuote, hasColon, hasLine, hasKeyClosingQuote, position, supportSnippets)
  }
}
