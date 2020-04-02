package org.mulesoft.als.suggestions.styler

import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.suggestions.styler.astbuilder.{AstRawBuilder, JsonAstRawBuilder}
import org.mulesoft.als.suggestions.{RawSuggestion, SuggestionStructure}
import org.yaml.render.{JsonRender, JsonRenderOptions}

case class JsonSuggestionStyler(override val params: StylerParams) extends SuggestionRender {
  override protected def render(options: SuggestionStructure, builder: AstRawBuilder): String = {

    val json = rawRender(builder)

    if (hasBrotherAfterwards(params.yPartBranch)) json + ","
    else json
  }

  private def rawRender(builder: AstRawBuilder) = {
    val renderedJson =
      JsonRender.render(builder.ast, params.indentation, options = JsonRenderOptions().withoutNonAsciiEncode)
    if (renderedJson.endsWith("{}")) {
      builder.forSnippet()
      renderedJson.replace("{}", "{\n" + (" " * params.indentation) + "  \"$1\"\n" + (" " * params.indentation) + "}")
    } else renderedJson
  }

  override def astBuilder: RawSuggestion => AstRawBuilder =
    (raw: RawSuggestion) => new JsonAstRawBuilder(raw, false, params.yPartBranch)

  def hasBrotherAfterwards(yPartBranch: YPartBranch): Boolean = {
    val range = yPartBranch.node.range
    yPartBranch.brothers.exists(brother => brother.range.compareTo(range) > 0) && yPartBranch.isKey
  }
}
