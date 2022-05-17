package org.mulesoft.lsp.textsync

/** Defines how the host (editor) should sync document changes to the language server.
  */
case object TextDocumentSyncKind extends Enumeration {
  type TextDocumentSyncKind = Value

  /** Documents should not be synced at all.
    */
  val None: Value = Value(0)

  /** Documents are synced by always sending the full content of the document.
    */
  val Full: Value = Value(1)

  /** Documents are synced by sending the full content on open. After that only incremental updates to the document are
    * send.
    */
  val Incremental: Value = Value(2)
}
