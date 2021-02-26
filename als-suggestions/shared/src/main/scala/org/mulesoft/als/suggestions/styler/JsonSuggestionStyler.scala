package org.mulesoft.als.suggestions.styler

import org.mulesoft.als.suggestions.styler.astbuilder.{AstRawBuilder, JsonAstRawBuilder}
import org.mulesoft.als.suggestions.{RawSuggestion, SuggestionStructure}
import org.yaml.render.{JsonRender, JsonRenderOptions}

case class JsonSuggestionStyler(override val params: StylerParams) extends FlowSuggestionRender {

  override protected val escapeChar: String = "\""

  override protected def render(options: SuggestionStructure, builder: AstRawBuilder): String =
    rawRender(builder)

  private def rawRender(builder: AstRawBuilder) = {
    val renderedJson =
      JsonRender.render(builder.ast, 0, options = buildRenderOptions)
    fix(builder, renderedJson)
  }

  private def buildRenderOptions =
    JsonRenderOptions().withoutNonAsciiEncode.withPreferSpaces(useSpaces).withIndentationSize(tabSize)

  override def astBuilder: RawSuggestion => AstRawBuilder =
    (raw: RawSuggestion) => new JsonAstRawBuilder(raw, false, params.yPartBranch)

}
