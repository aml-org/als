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

  def styleKey(key: String): String

  def patchPath(builder: CompletionItemBuilder): Unit = {
    val index =
      params.prefix.lastIndexOf(".").max(params.prefix.lastIndexOf("/"))
    if (index > 0 && builder.getText.startsWith(params.prefix))
      if (index == params.prefix.length)
        builder
          .withText(builder.getText.substring(index))
          .withDisplayText(builder.getDisplayText.split('.').last)
      else
        builder
          .withText(builder.getText.substring(index + 1))
          .withDisplayText(builder.getDisplayText.substring(index + 1))
          .withPrefix(params.prefix.substring(index + 1))
          .withRange(builder.getRange.copy(start = builder.getRange.start.moveColumn(index + 1)))
  }

  protected def indentIfInArrayItem(arrayItem: Boolean, singleIndent: String): String =
    if (arrayItem) singleIndent else ""

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
        builder.withText(
          builder.getText + sonsToSnippet(
            suggestions.sons,
            suggestions.whiteSpacesEnding + indentIfInArrayItem(suggestions.options.arrayItem,
                                                                singleIndentation(suggestions.whiteSpacesEnding))))
    }

    builder.build()
  }

  protected def singleIndentation(indentation: String): String = if (indentation.contains("\t")) "\t" else "  "
  protected def startWithEOL(indentation: String): String      = if (indentation.startsWith("\n")) "" else "\n"

  def sonsToSnippet(children: Seq[String], indentation: String): String = {
    val newIndentation = startWithEOL(indentation) + indentation + singleIndentation(indentation)

    children.zipWithIndex.map {
      case (s, i) => { newIndentation + styleKey(s) } + "$" + (i + 1).toString
    } mkString
  }
}
