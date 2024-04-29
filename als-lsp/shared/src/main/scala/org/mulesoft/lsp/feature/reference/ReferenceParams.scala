package org.mulesoft.lsp.feature.reference

import org.mulesoft.lsp.feature.common.{Position, TextDocumentIdentifier, TextDocumentPositionParams}

/** The references request is sent from the client to the server to resolve project-wide references for the symbol
  * denoted by the given text document position.
  */

case class ReferenceParams(textDocument: TextDocumentIdentifier, position: Position, context: ReferenceContext)
    extends TextDocumentPositionParams
