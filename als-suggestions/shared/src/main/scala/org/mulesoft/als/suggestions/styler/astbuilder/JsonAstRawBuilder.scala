package org.mulesoft.als.suggestions.styler.astbuilder

import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.suggestions.{RawSuggestion, SuggestionStructure}
import org.yaml.model.{YMap, YMapEntry, YNode, YPart}

class JsonAstRawBuilder(val raw: RawSuggestion, val isSnippet: Boolean, val yPartBranch: YPartBranch)
    extends AstRawBuilder(raw, isSnippet, yPartBranch) {
  override protected def newInstance: (RawSuggestion, Boolean) => AstRawBuilder =
    (raw: RawSuggestion, isSnippet: Boolean) =>
      new JsonAstRawBuilder(raw, isSnippet, YPartBranch(YMap.empty, Position(0, 0).toAmfPosition, Nil))

  override def ast: YPart = {
    if (raw.options.isKey) emitRootKey
    else value(raw.newText, raw.options)
  }

  override def emitEntryValue(options: SuggestionStructure): YNode = {
    snippet = true
    value("$1", options)
  }

  override def onlyKey(key: String): YPart = YNode(key)
}
