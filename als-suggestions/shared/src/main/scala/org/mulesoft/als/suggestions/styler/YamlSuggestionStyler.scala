package org.mulesoft.als.suggestions.styler

import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.als.suggestions._
import org.mulesoft.als.suggestions.patcher.QuoteToken
import org.mulesoft.als.suggestions.styler.astbuilder.{AstRawBuilder, YamlAstRawBuilder}
import org.yaml.model.{YMap, YMapEntry, YPart, YScalar}
import org.yaml.render.{FlowYamlRender, YamlPartRender, YamlRender, YamlRenderOptions}

case class YamlSuggestionStyler(override val params: StylerParams) extends FlowSuggestionRender {

  override protected val useSpaces: Boolean = true

  private def fixPrefix(prefix: String, text: String) =
    if (prefix.isEmpty && text.startsWith(stringIndentation))
      text.stripPrefix(stringIndentation)
    else prefix + text

  override protected def render(options: SuggestionStructure, builder: AstRawBuilder): String = {
    val prefix =
      if (!options.isKey && ((options.isArray && !params.yPartBranch.isInArray) || options.isObject)) // never will suggest object in value as is not key. Suggestions should be empty
        "\n"
      else ""
    val ast         = builder.ast
    val indentation = 0 // We always want to indent relative to the parent node
    val rendered    = yamlRenderer.render(ast, indentation, buildYamlRenderOptions)
    fixPrefix(prefix, fix(builder, rendered))
  }

  def buildYamlRenderOptions: YamlRenderOptions =
    new YamlRenderOptions().withIndentationSize(params.formattingConfiguration.tabSize)

  def yamlRenderer: YamlPartRender = {
    if (params.yPartBranch.isInFlow) FlowYamlRender
    else YamlRender
  }

  override def style(raw: RawSuggestion): Styled = super.style(fixTokens(raw))

  private def fixTokens(raw: RawSuggestion): RawSuggestion =
    if (hasAddedQuotes)
      raw
        .withPositionRange(Some(innerSuggestionRange(raw)))
        .withStringKey
    else raw

  private def hasAddedQuotes =
    params.patchedContent.addedTokens.contains(QuoteToken)

  private def innerSuggestionRange(raw: RawSuggestion): PositionRange =
    if (hasAddedQuotes && raw.options.keyRange != StringScalarRange) { // it has added quotes, and was not originally a string
      val range = suggestionRange(raw)
      PositionRange(range.start.moveColumn(-1), range.end.moveColumn(1))
    } else suggestionRange(raw)

  override def astBuilder: RawSuggestion => AstRawBuilder =
    (raw: RawSuggestion) => new YamlAstRawBuilder(raw, false, params.yPartBranch)
}
