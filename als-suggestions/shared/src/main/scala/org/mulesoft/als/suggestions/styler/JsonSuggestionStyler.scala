package org.mulesoft.als.suggestions.styler

import org.mulesoft.als.suggestions.styler.astbuilder.{AstRawBuilder, JsonAstRawBuilder}
import org.mulesoft.als.suggestions.{RawSuggestion, SuggestionStructure}
import org.yaml.model.YPart
import org.yaml.render.{JsonRender, JsonRenderOptions}

case class JsonSuggestionStyler(override val params: SyamlStylerParams) extends FlowSuggestionRender {

  override protected val escapeChar: String = "\""

  override protected def render(options: SuggestionStructure, builder: AstRawBuilder): String =
    rawRender(builder)

  private def rawRender(builder: AstRawBuilder) = {
    val fixedJson = fix(builder, renderYPart(builder.ast))
    if (!builder.raw.newText.contains('$'))
      fixedJson
    else
      escapeNonSnippets(fixedJson, builder.raw.newText)
  }

  private def buildRenderOptions =
    JsonRenderOptions().withoutNonAsciiEncode.withPreferSpaces(useSpaces).withIndentationSize(tabSize)

  override protected def renderYPart(part: YPart, indentation: Option[Int] = None): String =
    JsonRender.render(part, indentation = indentation.getOrElse(0), buildRenderOptions)

  /**
    * This method is used to escape the backslash character for non snippets.
    * @param fixedJson rendered and fixed suggestion
    * @param newText the original suggestion
    * @return the suggestion escaped
    */
  private def escapeNonSnippets(fixedJson: String, newText: String): String =
    fixedJson.replace(newText, "\\".concat(newText))

  override def astBuilder: RawSuggestion => AstRawBuilder =
    (raw: RawSuggestion) => new JsonAstRawBuilder(raw, false, params.yPartBranch)

}
