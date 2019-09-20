package org.mulesoft.als.suggestions.styler

import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.implementation.CompletionItemBuilder
import org.mulesoft.lsp.feature.completion.{CompletionItem, InsertTextFormat}

object SuggestionStylerBuilder {
  def build(isYAML: Boolean,
            prefix: String,
            originalContent: String,
            position: Position,
            snippetsSupport: Boolean = true): SuggestionStyler = {
    val params = StylerParams.apply(prefix: String, originalContent: String, position: Position, snippetsSupport)

    if (isYAML) YamlSuggestionStyler(params)
    else JsonSuggestionStyler(params)
  }
}

trait SuggestionStyler {
  val params: StylerParams
  def style(suggestion: RawSuggestion): Styled

  def patchPath(builder: CompletionItemBuilder, text: String): Unit = {
    val index =
      params.prefix.lastIndexOf(".").max(params.prefix.lastIndexOf("/"))
    if (index > 0 && text.startsWith(params.prefix))
      if (index == params.prefix.length)
        builder.withText(text.substring(index)).withDisplayText(builder.getDisplayText.split('.').last)
      else
        builder
          .withText(text.substring(index + 1))
          .withDisplayText(builder.getDisplayText.substring(index + 1))
          .withPrefix(params.prefix.substring(index + 1))
          .withRange(builder.getRange.copy(start = builder.getRange.start.moveColumn(index + 1)))
  }

  def rawToStyledSuggestion(suggestions: RawSuggestion): CompletionItem = {
    val builder = new CompletionItemBuilder(
      suggestions.range.getOrElse(PositionRange(params.position.moveColumn(-params.prefix.length), params.position)))
    val styled = style(suggestions)
    builder
      .withText(styled.text)
      .withDescription(suggestions.description)
      .withDisplayText(suggestions.displayText)
      .withCategory(suggestions.category)
      .withPrefix(params.prefix)

    if (styled.plain) patchPath(builder, styled.text)
    else builder.withInsertTextFormat(InsertTextFormat.Snippet)

    builder.build()
  }
}
