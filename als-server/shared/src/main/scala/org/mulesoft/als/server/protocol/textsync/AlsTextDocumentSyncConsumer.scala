package org.mulesoft.als.server.protocol.textsync

import org.mulesoft.als.server.textsync.TextDocumentContainer
import org.mulesoft.lsp.textsync.TextDocumentSyncConsumer

import scala.concurrent.Future
trait AlsTextDocumentSyncConsumer extends TextDocumentSyncConsumer {
  val uriToEditor: TextDocumentContainer

  def didFocus(params: DidFocusParams): Future[Unit]

}
