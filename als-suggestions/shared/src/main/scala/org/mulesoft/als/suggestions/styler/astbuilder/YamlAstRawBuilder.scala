package org.mulesoft.als.suggestions.styler.astbuilder

import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.suggestions.{RawSuggestion, SuggestionStructure}
import org.mulesoft.common.client.lexical.{Position => AmfPosition}
import org.yaml.model.{YMap, YMapEntry, YNode, YPart}
class YamlAstRawBuilder(override val raw: RawSuggestion, val isSnippet: Boolean, val yPartBranch: YPartBranch)
    extends AstRawBuilder(raw, isSnippet, yPartBranch) {

  def ast: YPart =
    if (raw.options.isKey)
      if (yPartBranch.isInFlow) emitKey()
      else YNode(YMap(IndexedSeq(emitKey()), ""))
    else value(raw.newText, raw.options)

  override protected def newInstance: (RawSuggestion, Boolean) => AstRawBuilder =
    (raw: RawSuggestion, isSnippet: Boolean) =>
      new YamlAstRawBuilder(
        raw,
        isSnippet,
        YPartBranch(YMap.empty, AmfPosition.ZERO, Nil, isJson = false, isInFlow = false)
      )

  override def emitEntryValue(options: SuggestionStructure): YNode = value("", options)

  override def onlyKey(key: String): YPart = YMapEntry(key, YNode.Empty)

  override def emptyNode(): YNode = YNode.Empty
}
