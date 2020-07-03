package org.mulesoft.lsp.feature.typedefinition

import org.mulesoft.lsp.feature.common.{Position, TextDocumentIdentifier, TextDocumentPositionParams}

case class TypeDefinitionParams(textDocument: TextDocumentIdentifier, position: Position)
    extends TextDocumentPositionParams
