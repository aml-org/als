package org.mulesoft.als.suggestions

import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.suggestions.implementation.{Suggestion => SuggestionImpl}
import org.mulesoft.als.suggestions.interfaces.Suggestion

object SuggestionStyler {

  def adjustedSuggestions(suggestions: Seq[Suggestion],
                          isYAML: Boolean,
                          isKey: Boolean,
                          noColon: Boolean, // just set in AnnotationReferencesCompletionPlugin ??
                          position: Position,
                          originalContent: String): Seq[Suggestion] = {

    var hasQuote           = false
    var hasColon           = false
    var hasLine            = false
    var hasKeyClosingQuote = false

    val lines = originalContent.lines drop position.line
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

    val styler =
      if (isYAML)
        yamlStyle(noColon, isKey, hasLine, hasColon, _)
      else
        jsonStyle(noColon, isKey, hasLine, hasColon, hasKeyClosingQuote, hasQuote, _)

    suggestions.map(
      s =>
        SuggestionImpl(
          styler(s),
          s.description,
          s.displayText,
          s.prefix,
          Option(PositionRange(position.moveColumn(-s.prefix.length), position))).withCategory(s.category))
  }

  private def yamlStyle(noColon: Boolean,
                        isKey: Boolean,
                        hasLine: Boolean,
                        hasColon: Boolean,
                        suggestion: Suggestion): String = {
    def whiteSpaceOrSpace(str: String): String =
      if (str isEmpty) " "
      else str
    if (!noColon && isKey)
      if (!hasLine || !hasColon)
        s"${suggestion.text}:${whiteSpaceOrSpace(suggestion.trailingWhitespace)}"
      else suggestion.text
    else if (!isKey)
      if (suggestion.prefix == ":" && !(suggestion.text.startsWith("\n") || suggestion.text
            .startsWith("\r\n") || suggestion.text.startsWith(" ")))
        s" ${suggestion.text}"
      else if (suggestion.text endsWith ":") s"${suggestion.text} "
      else suggestion.text
    else suggestion.text
  }

  private def jsonStyle(noColon: Boolean,
                        isKey: Boolean,
                        hasLine: Boolean,
                        hasColon: Boolean,
                        hasKeyClosingQuote: Boolean,
                        hasQuote: Boolean,
                        suggestion: Suggestion): String = {
    val isJSONObject = (suggestion.text startsWith "{") && (suggestion.text endsWith "}")
    var endingQuote  = false
    var postfix      = ""
    if (isKey) {
      if (!hasKeyClosingQuote) {
        postfix += "\""
        if (!hasColon && !noColon)
          postfix += ":"
        else if (!hasQuote)
          postfix += "\""
      } else if (!hasQuote) {
        postfix += "\""
        endingQuote = true
      }
    } else if (!hasQuote) {
      postfix += "\""
      endingQuote = true
    }
    if (!isJSONObject && (!endingQuote || !(suggestion.text endsWith "\"")))
      suggestion.text + postfix
    else suggestion.text
  }
}
