package org.mulesoft.lsp.feature.definition

import org.mulesoft.lsp.feature.common.{Position, TextDocumentIdentifier, TextDocumentPositionParams}

case class DefinitionParams(textDocument: TextDocumentIdentifier, position: Position) extends TextDocumentPositionParams
