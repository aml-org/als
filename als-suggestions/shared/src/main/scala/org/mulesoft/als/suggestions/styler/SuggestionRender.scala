package org.mulesoft.als.suggestions.styler

import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.als.suggestions._
import org.mulesoft.als.suggestions.implementation.CompletionItemBuilder
import org.mulesoft.als.suggestions.styler.astbuilder.AstRawBuilder
import org.mulesoft.lsp.feature.completion.{CompletionItem, InsertTextFormat}
import org.yaml.model._

trait SuggestionRender {
  val params: StylerParams

  def astBuilder: RawSuggestion => AstRawBuilder

  lazy val stringIndentation: String   = " " * params.indentation
  lazy val initialIndentationSize: Int = params.indentation / 2
  lazy val tabSize: Int                = params.formattingConfiguration.tabSize

  def patchPath(builder: CompletionItemBuilder): Unit = {
    if (!isHeaderSuggestion) {
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
  }

  private def isHeaderSuggestion: Boolean = params.position.line == 0 && params.prefix.startsWith("#%")

  private def keyRange: Option[PositionRange] =
    params.yPartBranch.node match {
      case n: YNode if n.value.isInstanceOf[YScalar] && params.yPartBranch.isJson =>
        if (params.yPartBranch.isKey) {
          params.yPartBranch.parentEntry
            .map(_.range)
            .map(
              r =>
                if (r.lineTo == r.lineFrom)
                  r.copy(columnTo = r.columnTo - params.patchedContent.addedTokens.foldLeft(0)((a, t) => a + t.size))
                else r)
            .orElse(Some(n.range))
            .map(PositionRange(_))
        } else Some(PositionRange(n.range))
      case _ => None
    }

  private def getBuilder(suggestions: RawSuggestion): CompletionItemBuilder = {
    val styled  = style(suggestions)
    val builder = new CompletionItemBuilder(styled.replacementRange)
    if (styled.plain)
      builder.withInsertTextFormat(InsertTextFormat.PlainText)
    else builder.withInsertTextFormat(InsertTextFormat.Snippet)
    if (suggestions.children.nonEmpty) builder.withTemplate()

    builder
      .withText(styled.text)
  }

  def rawToStyledSuggestion(suggestions: RawSuggestion): CompletionItem = {
    val builder = getBuilder(suggestions)
    builder
      .withDescription(suggestions.description)
      .withDisplayText(suggestions.displayText)
      .withCategory(suggestions.category)
      .withPrefix(params.prefix)
      .withMandatory(suggestions.options.isMandatory)
      .withIsTopLevel(suggestions.options.isTopLevel)

    patchPath(builder)

    builder.build()
  }

  def style(raw: RawSuggestion): Styled =
    if (raw.options.rangeKind == PlainText)
      Styled(raw.newText,
             plain = true,
             raw.range.getOrElse(PositionRange(params.position.moveColumn(-params.prefix.length), params.position)))
    else {
      val builder = astBuilder(raw)
      val text    = render(raw.options, builder)

      Styled(text, plain = !builder.asSnippet, suggestionRange(raw))
    }

  protected def suggestionRange(raw: RawSuggestion): PositionRange =
    raw.range
      .orElse(keyRange)
      .getOrElse(PositionRange(params.position.moveColumn(-params.prefix.length), params.position))

  protected def render(options: SuggestionStructure, builder: AstRawBuilder): String

}
