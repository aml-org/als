package org.mulesoft.als.suggestions.styler

import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.configuration.AlsConfiguration
import org.mulesoft.als.suggestions.styler.astbuilder.{AstRawBuilder, DummyAstRawBuilder}
import org.mulesoft.als.suggestions.{RawSuggestion, SuggestionStructure}
import org.yaml.model.{YMap, YPart}

case class DummySuggestionStyle(prefix: String, position: Position) extends SuggestionRender {
  override val params: SyamlStylerParams =
    SyamlStylerParams(
      prefix,
      position,
      YPartBranch(YMap.empty, position.toAmfPosition, Nil, strict = false),
      AlsConfiguration().getFormatOptionForMime("")
    )

  override protected def render(options: SuggestionStructure, builder: AstRawBuilder): String = builder.ast.toString

  override def astBuilder: RawSuggestion => AstRawBuilder = (raw: RawSuggestion) => new DummyAstRawBuilder(raw)

  override protected def renderYPart(part: YPart, indentation: Option[Int] = None): String = part.toString

  override def adaptRangeToPositionValue(r: PositionRange, options: SuggestionStructure): PositionRange = r
}
