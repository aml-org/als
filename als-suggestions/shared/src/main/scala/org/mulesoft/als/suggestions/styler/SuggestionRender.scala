package org.mulesoft.als.suggestions.styler

import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.als.suggestions._
import org.mulesoft.als.suggestions.implementation.CompletionItemBuilder
import org.mulesoft.als.suggestions.styler.astbuilder.AstRawBuilder
import org.mulesoft.lsp.edit.TextEdit
import org.mulesoft.lsp.feature.completion.{CompletionItem, InsertTextFormat}
import org.yaml.model._

trait SuggestionRender {
  val params: StylerParams

  protected def astBuilder: RawSuggestion => AstRawBuilder

  lazy val stringIndentation: String = " " * params.indentation
  lazy val tabSize: Int              = params.formattingConfiguration.tabSize

  private def patchPath(builder: CompletionItemBuilder): Unit =
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

  private def isHeaderSuggestion: Boolean = params.position.line == 0 && params.prefix.startsWith("#%")

  private def keyRange: Option[PositionRange] =
    params.yPartBranch.node match {
      case n: YNode if n.value.isInstanceOf[YScalar] && params.yPartBranch.isJson =>
        Some(PositionRange(n.range))
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

  protected def renderYPart(part: YPart, indentation: Option[Int] = None): String

  private def toTextEdits(textEdits: Seq[Either[TextEdit, AdditionalSuggestion]]): Seq[TextEdit] =
    textEdits.map {
      case Left(te) => te
      case Right(AdditionalSuggestion(insert, Left(range))) =>
        val text = renderYPart(insert)
        TextEdit(LspRangeConverter.toLspRange(range), text)
      case Right(AdditionalSuggestion(insert, Right(parent))) =>
        val indentation: Int = parent.key.range.columnFrom + params.formattingConfiguration.tabSize
        val text             = s"\n${renderYPart(insert, Some(indentation))}"
        val position         = Position(parent.value.range.lineTo, parent.value.range.columnTo)
        TextEdit(LspRangeConverter.toLspRange(PositionRange(position, position)), text)
    }

  def rawToStyledSuggestion(suggestions: RawSuggestion): CompletionItem = {
    val builder = getBuilder(suggestions)
      .withDescription(suggestions.description)
      .withDisplayText(suggestions.displayText)
      .withCategory(suggestions.category)
      .withPrefix(params.prefix)
      .withMandatory(suggestions.options.isMandatory)
      .withIsTopLevel(suggestions.options.isTopLevel)
    if (suggestions.textEdits.nonEmpty)
      builder.withAdditionalTextEdits(toTextEdits(suggestions.textEdits))

    patchPath(builder)

    builder.build()
  }

  def style(raw: RawSuggestion): Styled =
    if (raw.options.rangeKind == PlainText)
      Styled(
        raw.newText,
        plain = true,
        raw.range.getOrElse(PositionRange(params.position.moveColumn(-params.prefix.length), params.position))
      )
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
