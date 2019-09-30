package org.mulesoft.als.suggestions.styler

import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.suggestions.RawSuggestion

case class DummySuggestionStyle(prefix: String, position: Position) extends SuggestionStyler {
  override val params: StylerParams =
    StylerParams(prefix,
                 hasQuote = false,
                 hasColon = false,
                 hasLine = false,
                 hasKeyClosingQuote = false,
                 hasOpeningQuote = false,
                 position = position,
                 supportSnippets = true)

  override def style(suggestion: RawSuggestion): Styled = Styled(suggestion.newText, plain = true)

  override def styleKey(key: String): String = "key: "
}
