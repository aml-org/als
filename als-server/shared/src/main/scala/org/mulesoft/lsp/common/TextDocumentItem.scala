package org.mulesoft.lsp.common

/** An item to transfer a text document from the client to the server.
  *
  * @param uri        The text document's URI.
  * @param languageId The text document's language identifier.
  * @param version    The version number of this document (it will increase after each change, including undo/redo).
  * @param text       The content of the opened text document.
  */

case class TextDocumentItem(uri: String, languageId: String, version: Int, text: String)
