package org.mulesoft.lsp.feature.hover

import org.mulesoft.lsp.feature.common.{Position, TextDocumentIdentifier, TextDocumentPositionParams}

case class HoverParams(textDocument: TextDocumentIdentifier, position: Position) extends TextDocumentPositionParams
