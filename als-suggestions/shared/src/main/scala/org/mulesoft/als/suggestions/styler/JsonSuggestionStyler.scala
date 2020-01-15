package org.mulesoft.als.suggestions.styler

import org.mulesoft.als.suggestions.styler.astbuilder.{AstRawBuilder, JsonAstRawBuilder}
import org.mulesoft.als.suggestions.{RawSuggestion, SuggestionStructure}
import org.yaml.render.{JsonRender, JsonRenderOptions}

case class JsonSuggestionStyler(override val params: StylerParams) extends SuggestionRender {
  override protected def render(options: SuggestionStructure, builder: AstRawBuilder): String = {
    val json = JsonRender.render(builder.ast, params.indentation, options = JsonRenderOptions().withoutNonAsciiEncode)
    if (json.endsWith("{}")) {
      builder.forSnippet()
      json.replace("{}", "{\n" + (" " * params.indentation) + "  \"$1\"\n" + (" " * params.indentation) + "}")
    } else json
  }

  override def astBuilder: RawSuggestion => AstRawBuilder =
    (raw: RawSuggestion) => new JsonAstRawBuilder(raw, false, params.yPartBranch)
}
