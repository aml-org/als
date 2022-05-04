package org.mulesoft.als.suggestions.styler.astbuilder

import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.suggestions.{RawSuggestion, SuggestionStructure}
import org.yaml.model._
import org.mulesoft.common.client.lexical.{Position => AmfPosition}

class DummyAstRawBuilder(val raw: RawSuggestion)
    extends AstRawBuilder(
      raw,
      false,
      YPartBranch(YMap.empty, AmfPosition.ZERO, Nil, isJson = false, isInFlow = false)
    ) {

  override protected def newInstance: (RawSuggestion, Boolean) => AstRawBuilder =
    (raw: RawSuggestion, _: Boolean) => new DummyAstRawBuilder(raw)

  override def ast: YPart = emitEntryValue(SuggestionStructure())

  override def emitEntryValue(options: SuggestionStructure): YNode = YNode(raw.newText)

  override def onlyKey(key: String): YPart = YNode(key)

  override def emptyNode(): YNode = YNode("")
}
