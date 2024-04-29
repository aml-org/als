package org.mulesoft.lsp.feature.common

/** A parameter literal used in requests to pass a text document and a position inside that document.
  */

trait TextDocumentPositionParams {

  /** The text document.
    */
  val textDocument: TextDocumentIdentifier

  /** The position inside the text document.
    */
  val position: Position
}

object TextDocumentPositionParams {
  def apply(textDocumentIdentifier: TextDocumentIdentifier, somePosition: Position): TextDocumentPositionParams =
    new TextDocumentPositionParams() {
      override val textDocument: TextDocumentIdentifier = textDocumentIdentifier
      override val position: Position                   = somePosition
    }
}
