package org.mulesoft.als.server.modules.common

import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.server.modules.common.interfaces.ILocation
import org.mulesoft.als.server.textsync.ChangedDocument
import org.mulesoft.language.outline.structure.structureImpl.DocumentSymbol
import org.mulesoft.language.outline.structure.structureImpl.SymbolKind.SymbolKind
import org.mulesoft.lsp.common.{Location => LspLocation, Position => LspPosition, Range => LspRange}
import org.mulesoft.lsp.edit.{WorkspaceEdit, TextEdit => LspTextEdit}
import org.mulesoft.lsp.feature.documentsymbol.{DocumentSymbol => LspDocumentSymbol, SymbolKind => LspSymbolKind}

object LspConverter {

  def toPosition(position: LspPosition): Position = Position(position.line, position.character)

  def toLspPosition(position: Position): LspPosition = LspPosition(position.line, position.column)

  def toLspRange(position: PositionRange): LspRange =
    LspRange(toLspPosition(position.start), toLspPosition(position.end))

  def toLspLocation(location: ILocation): LspLocation = LspLocation(location.uri, toLspRange(location.posRange))

  def toLspDocumentSymbol(documentSymbol: DocumentSymbol): LspDocumentSymbol = LspDocumentSymbol(
    documentSymbol.name,
    toLspSymbolKind(documentSymbol.kind),
    toLspRange(documentSymbol.range),
    toLspRange(documentSymbol.selectionRange),
    documentSymbol.children.map(toLspDocumentSymbol),
    None,
    Some(documentSymbol.deprecated)
  )

  def toLspSymbolKind(kind: SymbolKind): LspSymbolKind.Value = LspSymbolKind(kind.index)

  def toWorkspaceEdit(changes: Seq[ChangedDocument]): WorkspaceEdit = {
    val textEdits = changes
      .groupBy(change => change.uri)
      .mapValues(_.flatMap(a => a.textEdits.fold(Seq[LspTextEdit]())(toLspTextEdits)))
    WorkspaceEdit(textEdits, Seq())
  }

  def toLspTextEdits(textEdits: Seq[TextEdit]): Seq[LspTextEdit] = textEdits.map(toLspTextEdit)

  def toLspTextEdit(textEdit: TextEdit): LspTextEdit = LspTextEdit(toLspRange(textEdit.range), textEdit.text)
}
