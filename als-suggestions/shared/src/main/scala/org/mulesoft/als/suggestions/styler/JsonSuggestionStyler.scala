package org.mulesoft.als.suggestions.styler

import org.mulesoft.als.suggestions.styler.astbuilder.{AstRawBuilder, JsonAstRawBuilder}
import org.mulesoft.als.suggestions.{RawSuggestion, SuggestionStructure}
import org.yaml.model.YPart
import org.yaml.render.{JsonRender, JsonRenderOptions}

case class JsonSuggestionStyler(override val params: SyamlStylerParams) extends FlowSuggestionRender {

  override protected val escapeChar: String = "\""

  override protected def render(options: SuggestionStructure, builder: AstRawBuilder): String =
    rawRender(builder)

  private def rawRender(builder: AstRawBuilder) =
    fix(builder, renderYPart(builder.ast))

  private def buildRenderOptions =
    JsonRenderOptions().withoutNonAsciiEncode.withPreferSpaces(useSpaces).withIndentationSize(tabSize)

  override protected def renderYPart(part: YPart, indentation: Option[Int] = None): String =
    JsonRender.render(part, indentation = indentation.getOrElse(0), buildRenderOptions)


  override def astBuilder: RawSuggestion => AstRawBuilder =
    (raw: RawSuggestion) => new JsonAstRawBuilder(raw, false, params.yPartBranch, params.supportSnippets)

}
