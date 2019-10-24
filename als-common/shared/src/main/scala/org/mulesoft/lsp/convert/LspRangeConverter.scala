package org.mulesoft.lsp.convert

import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.lsp.common.{Location => LspLocation, Position => LspPosition, Range => LspRange}
import org.mulesoft.lsp.edit.{WorkspaceEdit, TextEdit => LspTextEdit}
import org.mulesoft.lsp.feature.documentsymbol.{DocumentSymbol => LspDocumentSymbol, SymbolKind => LspSymbolKind}

object LspRangeConverter {

  def toPosition(position: LspPosition): Position = Position(position.line, position.character)

  def toLspPosition(position: Position): LspPosition = LspPosition(position.line, position.column)

  def toLspRange(position: PositionRange): LspRange =
    LspRange(toLspPosition(position.start), toLspPosition(position.end))

}
