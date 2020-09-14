package org.mulesoft.als.suggestions.styler

import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.suggestions.styler.astbuilder.{AstRawBuilder, JsonAstRawBuilder}
import org.mulesoft.als.suggestions.{RawSuggestion, SuggestionStructure}
import org.yaml.render.{JsonRender, JsonRenderOptions}

case class JsonSuggestionStyler(override val params: StylerParams) extends SuggestionRender {

  private val useSpaces: Boolean = params.formattingConfiguration.insertSpaces

  override protected def render(options: SuggestionStructure, builder: AstRawBuilder): String = {

    val json = rawRender(builder)

    if (hasBrotherAfterwards(params.yPartBranch)) json + ","
    else json
  }

  private def rawRender(builder: AstRawBuilder) = {
    val renderedJson =
      JsonRender.render(builder.ast, 0, options = buildRenderOptions)
    if (renderedJson.endsWith("{}")) {
      builder.forSnippet()
      renderedJson.replace("{}", "{\n" + (if (useSpaces) " " * tabSize else "\t") + "\"$1\"\n}")
    } else renderedJson
  }

  private def buildRenderOptions =
    JsonRenderOptions().withoutNonAsciiEncode.withPreferSpaces(useSpaces).withIndentationSize(tabSize)

  def initialIndentation(): String = {
    if (useSpaces) " " * initialIndentationSize * tabSize
    else "\t" * initialIndentationSize
  }
  override def astBuilder: RawSuggestion => AstRawBuilder =
    (raw: RawSuggestion) => new JsonAstRawBuilder(raw, false, params.yPartBranch)

  def hasBrotherAfterwards(yPartBranch: YPartBranch): Boolean = {
    val range = yPartBranch.node.range
    yPartBranch.brothers.exists(brother => brother.range.compareTo(range) > 0) && yPartBranch.isKey
  }
}
