package org.mulesoft.lsp.textsync

import org.mulesoft.lsp.InitializableModule
import org.mulesoft.lsp.textsync.TextDocumentSyncKind.TextDocumentSyncKind

import scala.concurrent.Future

trait TextDocumentSyncConsumer
    extends InitializableModule[
      SynchronizationClientCapabilities,
      Either[TextDocumentSyncKind, TextDocumentSyncOptions]
    ] {

  /** The document open notification is sent from the client to the server to signal newly opened text documents. The
    * document's truth is now managed by the client and the server must not try to read the document's truth using the
    * document's uri.
    *
    * Registration Options: TextDocumentRegistrationOptions
    */
  def didOpen(params: DidOpenTextDocumentParams): Future[Unit]

  /** The document change notification is sent from the client to the server to signal changes to a text document.
    *
    * Registration Options: TextDocumentChangeRegistrationOptions
    */
  def didChange(params: DidChangeTextDocumentParams): Future[Unit]

  /** The document close notification is sent from the client to the server when the document got closed in the client.
    * The document's truth now exists where the document's uri points to (e.g. if the document's uri is a file uri the
    * truth now exists on disk).
    *
    * Registration Options: TextDocumentRegistrationOptions
    */
  def didClose(params: DidCloseTextDocumentParams): Future[Unit]
}
