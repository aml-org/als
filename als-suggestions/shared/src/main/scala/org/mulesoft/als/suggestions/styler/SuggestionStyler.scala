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
            snippetsSupport: Boolean = true,
            indentation: Int = 0): SuggestionStyler = {
    val params =
      StylerParams.apply(prefix: String, originalContent: String, position: Position, snippetsSupport, indentation)

    if (isYAML) YamlSuggestionStyler(params)
    else JsonSuggestionStyler(params)
  }
}

trait SuggestionStyler {
  val params: StylerParams
  def style(suggestion: RawSuggestion): Styled

  def styleKey(key: String): String

  def patchPath(builder: CompletionItemBuilder): Unit = {
    val index =
      params.prefix.lastIndexOf(".").max(params.prefix.lastIndexOf("/"))
    if (index > 0 && builder.getDisplayText.startsWith(params.prefix))
      if (index == params.prefix.length)
        builder
          .withDisplayText(builder.getDisplayText.split('.').last)
      else
        builder
          .withDisplayText(builder.getDisplayText.substring(index + 1))
  }

  protected def indentIfInArrayItem(arrayItem: Boolean, singleIndent: String): String =
    if (arrayItem) singleIndent else ""

  def pathPrefix(displayText: String): String = {
    val index =
      params.prefix.lastIndexOf(".").max(params.prefix.lastIndexOf("/"))
    if (index > 0 && displayText.startsWith(params.prefix))
      if (index == params.prefix.length) displayText.split('.').last
      else
        displayText.substring(index + 1)
    else displayText
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

    patchPath(builder)
    if (!styled.plain || suggestions.sons.nonEmpty) {
      builder.withInsertTextFormat(InsertTextFormat.Snippet)
      if (suggestions.sons.nonEmpty)
        builder.withText(builder.getText + sonsToSnippet(suggestions.sons.map(_.newText)))
    }

    builder.build()
  }

  def sonsToSnippet(children: Seq[String]): String = {
    children.zipWithIndex.map {
      case (s, i) => { styleKey(s) } + "$" + (i + 1).toString
    } mkString
  }
}
