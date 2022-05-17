package org.mulesoft.lsp.feature.link

import org.mulesoft.lsp.feature.common.TextDocumentIdentifier

/** The document symbol request is sent from the client to the server to return a flat list of all symbols found in a
  * given text document. Neither the symbol’s location range nor the symbol’s container name should be used to infer a
  * hierarchy
  *
  * @param textDocument
  *   The text document.
  */
case class DocumentLinkParams(textDocument: TextDocumentIdentifier)
