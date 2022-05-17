package org.mulesoft.lsp.textsync

import org.mulesoft.lsp.textsync.TextDocumentSyncKind.TextDocumentSyncKind

/** @param openClose
  *   Open and close notifications are sent to the server.
  * @param change
  *   Change notifications are sent to the server. See TextDocumentSyncKind.None, TextDocumentSyncKind.Full and
  *   TextDocumentSyncKind.Incremental. If omitted it defaults to TextDocumentSyncKind.None.
  * @param willSave
  *   Will save notifications are sent to the server.
  * @param willSaveWaitUntil
  *   Will save wait until requests are sent to the server.
  * @param save
  *   Save notifications are sent to the server.
  */

case class TextDocumentSyncOptions(
    openClose: Option[Boolean] = None,
    change: Option[TextDocumentSyncKind] = None,
    willSave: Option[Boolean] = None,
    willSaveWaitUntil: Option[Boolean] = None,
    save: Option[SaveOptions] = None
)
