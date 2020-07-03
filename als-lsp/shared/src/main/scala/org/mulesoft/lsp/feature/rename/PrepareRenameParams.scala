package org.mulesoft.lsp.feature.rename

import org.mulesoft.lsp.feature.common.{Position, TextDocumentIdentifier, TextDocumentPositionParams}

case class PrepareRenameParams(textDocument: TextDocumentIdentifier, position: Position)
    extends TextDocumentPositionParams
