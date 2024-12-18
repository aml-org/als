package org.mulesoft.als.suggestions.styler.astbuilder

import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.suggestions.{RawSuggestion, SuggestionStructure}
import org.mulesoft.common.client.lexical.{Position => AmfPosition}
import org.yaml.model.{YMap, YNode, YPart}

class JsonAstRawBuilder(override val raw: RawSuggestion, val isSnippet: Boolean, val yPartBranch: YPartBranch, supportsSnippets: Boolean)
    extends AstRawBuilder(raw, isSnippet, yPartBranch, supportsSnippets) {
  override protected def newInstance: (RawSuggestion, Boolean) => AstRawBuilder =
    (raw: RawSuggestion, isSnippet: Boolean) =>
      new JsonAstRawBuilder(
        raw,
        isSnippet,
        YPartBranch(YMap.empty, AmfPosition.ZERO, Nil, strict = true),
        supportsSnippets
      )

  override def ast: YPart = {
    if (raw.options.isKey) emitKey()
    else value(raw.newText, raw.options)
  }

  override def emitEntryValue(options: SuggestionStructure): YNode = {
    if(supportsSnippets) {
      snippet = true
      value("$1", options)
    }
    else value("", options)
  }

  override def onlyKey(key: String): YPart = YNode(key)

  override def emptyNode(): YNode = YNode("")
}
