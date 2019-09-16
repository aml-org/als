package org.mulesoft.als.suggestions

import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.suggestions.implementation.{Suggestion => SuggestionImpl}
import org.mulesoft.als.suggestions.interfaces.Suggestion

case class StylerParams(prefix: String,
                        hasQuote: Boolean,
                        hasColon: Boolean,
                        hasLine: Boolean,
                        hasKeyClosingQuote: Boolean,
                        position: Position)
object StylerParams {
  def apply(prefix: String, originalContent: String, position: Position): StylerParams = {

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

    StylerParams(prefix, hasQuote, hasColon, hasLine, hasKeyClosingQuote, position)
  }
}

object SuggestionStylerBuilder {
  def build(isYAML: Boolean, prefix: String, originalContent: String, position: Position): SuggestionStyler = {
    val params = StylerParams.apply(prefix: String, originalContent: String, position: Position)

    if (isYAML) YamlSuggestionStyler(params)
    else JsonSuggestionStyler(params)
  }
}

case class Styled(text: String, plain: Boolean)

trait SuggestionStyler {
  val params: StylerParams
  def style(suggestion: RawSuggestion): Styled

  def asSuggestionImpl(s: RawSuggestion): SuggestionImpl = {
    val styled = style(s)
    SuggestionImpl(
      styled.text,
      s.description,
      s.displayText,
      params.prefix,
      s.range.orElse(Option(PositionRange(params.position.moveColumn(-params.prefix.length), params.position))),
      styled.plain
    ).withCategory(s.category)
  }

  def rawToStyledSuggestion(suggestions: RawSuggestion): Suggestion = {
    asSuggestionImpl(suggestions)
  }
}

case class DummySuggestionStyle(prefix: String, position: Position) extends SuggestionStyler {
  override val params: StylerParams =
    StylerParams(prefix, hasQuote = false, hasColon = false, hasLine = false, hasKeyClosingQuote = false, position)

  override def style(suggestion: RawSuggestion): Styled = Styled(suggestion.newText, plain = true)
}

case class YamlSuggestionStyler(override val params: StylerParams) extends SuggestionStyler {
  override def style(rawSuggestion: RawSuggestion): Styled = {

    val text =
      if (rawSuggestion.options.isKey) keyAdapter(rawSuggestion) + arrayAdapter(rawSuggestion)
      else if (!rawSuggestion.options.isKey)
        if (params.prefix == ":" && !(rawSuggestion.newText.startsWith("\n") || rawSuggestion.newText
              .startsWith("\r\n") || rawSuggestion.newText.startsWith(" ")))
          s" ${rawSuggestion.newText}"
        else if (rawSuggestion.newText endsWith ":") s"${rawSuggestion.newText} "
        else if (rawSuggestion.options.arrayItem)
          "\n" + whiteSpaceOrSpace(rawSuggestion.whiteSpacesEnding) + "- " + rawSuggestion.newText
        else rawSuggestion.newText
      else rawSuggestion.newText
    Styled(text, plain = true)
  }

  private def whiteSpaceOrSpace(str: String): String = if (str.isEmpty) " " else str

  private def keyAdapter(rawSuggestion: RawSuggestion) =
    if (!params.hasLine || !params.hasColon)
      s"${rawSuggestion.newText}:${whiteSpaceOrSpace(rawSuggestion.whiteSpacesEnding)}"
    else rawSuggestion.newText

  private def arrayAdapter(rawSuggestion: RawSuggestion) = if (rawSuggestion.options.arrayProperty) "- " else ""
}

case class JsonSuggestionStyler(override val params: StylerParams) extends SuggestionStyler {
  override def style(suggestion: RawSuggestion): Styled = {
    val isJSONObject = (suggestion.newText startsWith "{") && (suggestion.newText endsWith "}")
    var endingQuote  = false
    var postfix      = ""
    var prefix       = ""
    if (suggestion.options.isKey) {
      if (!suggestion.newText.startsWith("\"") && !params.hasKeyClosingQuote)
        prefix = "\""
      if (!params.hasKeyClosingQuote) {
        postfix += "\""
        if (!params.hasColon)
          postfix += ":"
        else if (!params.hasQuote)
          postfix += "\""
      } else if (!params.hasQuote) {
        postfix += "\""
        endingQuote = true
      }
//      if (!suggestion.newText.startsWith("\"") && !params.hasKeyClosingQuote)
//        prefix = "\""
//      if (!params.hasKeyClosingQuote) {
//        postfix += "\""
//        if (!params.hasColon)
//          postfix += ":"
//        if (!params.hasQuote && !suggestion.options.arrayProperty)
//          postfix += "\"$1\""
//        else if( !params.hasQuote && suggestion.options.arrayProperty)
//          postfix += "[ $1 ]"
//      } else if (!params.hasQuote && !suggestion.options.arrayProperty) {
//        postfix += "\"$1\""
//        endingQuote = true
//      }else if( !params.hasQuote && suggestion.options.arrayProperty){
//        postfix += "[ $1 ]"
//      }
    } else if (!params.hasQuote) {
      postfix += "\""
      endingQuote = true
    }
    val text = prefix + {
      if (!isJSONObject && (!endingQuote || !(suggestion.newText endsWith "\"")))
        suggestion.newText + postfix
      else suggestion.newText
    }
    if (postfix.contains("$1"))
      Styled(text, false)
    else Styled(text, true)
  }
}
