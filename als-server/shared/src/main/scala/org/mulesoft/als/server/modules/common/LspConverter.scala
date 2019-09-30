package org.mulesoft.als.server.modules.common

import org.mulesoft.language.outline.structure.structureImpl.DocumentSymbol
import org.mulesoft.language.outline.structure.structureImpl.SymbolKind.SymbolKind
import org.mulesoft.lsp.convert.LspRangeConverter
import org.mulesoft.lsp.feature.documentsymbol.{DocumentSymbol => LspDocumentSymbol, SymbolKind => LspSymbolKind}

object LspConverter {

  def toLspDocumentSymbol(documentSymbol: DocumentSymbol): LspDocumentSymbol = LspDocumentSymbol(
    documentSymbol.name,
    toLspSymbolKind(documentSymbol.kind),
    LspRangeConverter.toLspRange(documentSymbol.range),
    LspRangeConverter.toLspRange(documentSymbol.selectionRange),
    documentSymbol.children.map(toLspDocumentSymbol),
    None,
    Some(documentSymbol.deprecated)
  )

  def toLspSymbolKind(kind: SymbolKind): LspSymbolKind.Value = LspSymbolKind(kind.index)

}
