package org.mulesoft.lsp.feature.rename

import org.mulesoft.lsp.feature.common.{Position, TextDocumentIdentifier}

/** The rename request is sent from the client to the server to perform a workspace-wide rename of a symbol.
  *
  * @param textDocument
  *   The document to rename.
  * @param position
  *   The position at which this request was sent.
  * @param newName
  *   The new name of the symbol. If the given name is not valid the request must return a
  *   [ResponseError](#ResponseError) with an appropriate message set.
  */

case class RenameParams(textDocument: TextDocumentIdentifier, position: Position, newName: String)
