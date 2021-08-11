package org.mulesoft.als.server.modules.common

import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.language.outline.structure.structureImpl.DocumentSymbol
import org.mulesoft.language.outline.structure.structureImpl.SymbolKinds.SymbolKind
import org.mulesoft.lsp.feature.documentsymbol
import org.mulesoft.lsp.feature.documentsymbol.{DocumentSymbol => LspDocumentSymbol, SymbolKind => LspSymbolKind}

object LspConverter {

  def toLspDocumentSymbol(documentSymbol: DocumentSymbol): documentsymbol.DocumentSymbol = LspDocumentSymbol(
    documentSymbol.name,
    toLspSymbolKind(documentSymbol.kind),
    LspRangeConverter.toLspRange(documentSymbol.range),
    LspRangeConverter.toLspRange(documentSymbol.selectionRange),
    documentSymbol.children.map(toLspDocumentSymbol),
    None,
    Some(documentSymbol.deprecated)
  )

  def toLspSymbolKind(kind: SymbolKind): documentsymbol.SymbolKind.Value = LspSymbolKind(kind.index)

}
