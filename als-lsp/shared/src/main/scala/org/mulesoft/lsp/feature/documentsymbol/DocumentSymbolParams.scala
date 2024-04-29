package org.mulesoft.lsp.feature.documentsymbol

import org.mulesoft.lsp.feature.common.TextDocumentIdentifier

/** The document to provide document links for.
  *
  * @param textDocument
  *   The text document.
  */
case class DocumentSymbolParams(textDocument: TextDocumentIdentifier)
