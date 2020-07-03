package org.mulesoft.lsp.feature.highlight

import org.mulesoft.lsp.feature.common.{Position, TextDocumentIdentifier, TextDocumentPositionParams}

case class DocumentHighlightParams(textDocument: TextDocumentIdentifier, position: Position)
    extends TextDocumentPositionParams
