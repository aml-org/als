package org.mulesoft.als.server.protocol.textsync

import org.mulesoft.lsp.textsync.TextDocumentSyncConsumer

import scala.concurrent.Future
trait AlsTextDocumentSyncConsumer extends TextDocumentSyncConsumer {

  def didFocus(params: DidFocusParams): Future[Unit]

}
