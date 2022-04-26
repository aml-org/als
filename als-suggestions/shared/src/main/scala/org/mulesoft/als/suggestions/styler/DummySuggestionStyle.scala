package org.mulesoft.als.suggestions.styler

import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.configuration.AlsConfiguration
import org.mulesoft.als.suggestions.styler.astbuilder.{AstRawBuilder, DummyAstRawBuilder}
import org.mulesoft.als.suggestions.{RawSuggestion, SuggestionStructure}
import org.yaml.model.{YMap, YPart}

case class DummySuggestionStyle(prefix: String, position: Position) extends SuggestionRender {
  override val params: StylerParams =
    StylerParams(
      prefix,
      position,
      YPartBranch(YMap.empty, position.toAmfPosition, Nil, isJson = false, isInFlow = false),
      AlsConfiguration().getFormatOptionForMime("")
    )

  override protected def render(options: SuggestionStructure, builder: AstRawBuilder): String = builder.ast.toString

  override def astBuilder: RawSuggestion => AstRawBuilder = (raw: RawSuggestion) => new DummyAstRawBuilder(raw)

  override protected def renderYPart(part: YPart): String = part.toString
}
