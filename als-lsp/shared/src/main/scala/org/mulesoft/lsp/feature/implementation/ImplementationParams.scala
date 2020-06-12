package org.mulesoft.lsp.feature.implementation

import org.mulesoft.lsp.feature.common.{Position, TextDocumentIdentifier, TextDocumentPositionParams}

case class ImplementationParams(textDocument: TextDocumentIdentifier, position: Position)
    extends TextDocumentPositionParams
