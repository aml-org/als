package org.mulesoft.als.suggestions.styler

import org.mulesoft.als.suggestions.RawSuggestion

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

  override def styleKey(key: String): String = s"$key: "
}
