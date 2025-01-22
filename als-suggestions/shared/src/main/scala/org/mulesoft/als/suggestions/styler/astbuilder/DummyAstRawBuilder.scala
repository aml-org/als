package org.mulesoft.als.suggestions.styler.astbuilder

import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.suggestions.{RawSuggestion, SuggestionStructure}
import org.mulesoft.common.client.lexical.{Position => AmfPosition}
import org.yaml.model._

class DummyAstRawBuilder(override val raw: RawSuggestion)
    extends AstRawBuilder(
      raw,
      isSnippet = false,
      yPartBranch = YPartBranch(YMap.empty, AmfPosition.ZERO, Nil, strict = false)
    ) {

  override protected def newInstance: (RawSuggestion, Boolean) => AstRawBuilder =
    (raw: RawSuggestion, _: Boolean) => new DummyAstRawBuilder(raw)

  override def ast: YPart = emitEntryValue(SuggestionStructure())

  override def emitEntryValue(options: SuggestionStructure): YNode = YNode(raw.newText)

  override def onlyKey(key: String): YPart = YNode(key)

  override def emptyNode(): YNode = YNode("")
}
