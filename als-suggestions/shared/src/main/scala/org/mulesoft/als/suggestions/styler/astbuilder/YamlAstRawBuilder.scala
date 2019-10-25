package org.mulesoft.als.suggestions.styler.astbuilder

import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.suggestions.{RawSuggestion, SuggestionStructure}
import org.yaml.model.{YMap, YMapEntry, YNode, YPart}

class YamlAstRawBuilder(val raw: RawSuggestion, val isSnippet: Boolean, val yPartBranch: YPartBranch)
    extends AstRawBuilder(raw, isSnippet, yPartBranch) {

  def ast: YNode = {
    if (raw.options.isKey) YNode(YMap(IndexedSeq(emitRootKey), ""))
    else value(raw.newText, raw.options)
  }

  override protected def newInstance: (RawSuggestion, Boolean) => AstRawBuilder =
    (raw: RawSuggestion, isSnippet: Boolean) =>
      new YamlAstRawBuilder(raw, isSnippet, YPartBranch(YMap.empty, Position(0, 0), Nil))

  override def emitEntryValue(options: SuggestionStructure): YNode = value("", options)

  override def onlyKey(key: String): YPart = YMapEntry(key, YNode.Empty)
}
