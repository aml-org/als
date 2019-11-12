package org.mulesoft.als.suggestions.styler

import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.suggestions.patcher.PatchedContent
import org.mulesoft.als.suggestions.styler.astbuilder.{AstRawBuilder, DummyAstRawBuilder}
import org.mulesoft.als.suggestions.{RawSuggestion, SuggestionStructure}
import org.yaml.model.YMap

case class DummySuggestionStyle(prefix: String, position: Position) extends SuggestionRender {
  override val params: StylerParams =
    StylerParams(prefix,
                 PatchedContent("", "", Nil),
                 position,
                 YPartBranch(YMap.empty, position.toAmfPosition, Nil, isJson = false),
                 supportSnippets = true,
                 0)

  override protected def render(options: SuggestionStructure, builder: AstRawBuilder): String = builder.ast.toString

  override def astBuilder: RawSuggestion => AstRawBuilder = (raw: RawSuggestion) => new DummyAstRawBuilder(raw)
}
