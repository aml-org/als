package org.mulesoft.als.suggestions

import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.suggestions.implementation.{Suggestion => SuggestionImpl}
import org.mulesoft.als.suggestions.interfaces.Suggestion

case class StylerParams(isYAML: Boolean,
                        isKey: Boolean,
                        noColon: Boolean,
                        hasQuote: Boolean,
                        hasColon: Boolean,
                        hasLine: Boolean,
                        hasKeyClosingQuote: Boolean,
                        position: Position)
object StylerParams {
  def apply(isYAML: Boolean,
            isKey: Boolean,
            noColon: Boolean,
            originalContent: String,
            position: Position): StylerParams = {

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

    StylerParams(isYAML,
                 isKey,
                 noColon, // just set in AnnotationReferencesCompletionPlugin ??
                 hasQuote,
                 hasColon,
                 hasLine,
                 hasKeyClosingQuote,
                 position)
  }
}

object SuggestionStyler {

  def adjustedSuggestions(stylerParams: StylerParams, suggestions: Seq[Suggestion]): Seq[Suggestion] = {

    val styler: Suggestion => String =
      if (stylerParams.isYAML)
        yamlStyle(stylerParams.noColon, stylerParams.isKey, stylerParams.hasLine, stylerParams.hasColon, _)
      else
        jsonStyle(stylerParams.noColon,
                  stylerParams.isKey,
                  stylerParams.hasLine,
                  stylerParams.hasColon,
                  stylerParams.hasKeyClosingQuote,
                  stylerParams.hasQuote,
                  _)

    suggestions.map(s => asSuggestionImpl(styler)(s, stylerParams.position))
  }

  def asSuggestionImpl(styler: Suggestion => String)(s: Suggestion, position: Position): SuggestionImpl =
    SuggestionImpl(styler(s),
                   s.description,
                   s.displayText,
                   s.prefix,
                   s.range.orElse(Option(PositionRange(position.moveColumn(-s.prefix.length), position))))
      .withCategory(s.category)

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
    var prefix       = ""
    if (isKey) {
      if (!suggestion.text.startsWith("\"") && !hasKeyClosingQuote)
        prefix = "\""
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
    prefix + {
      if (!isJSONObject && (!endingQuote || !(suggestion.text endsWith "\"")))
        suggestion.text + postfix
      else suggestion.text
    }
  }
}
