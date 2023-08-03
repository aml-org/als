package org.mulesoft.als.server.protocol.textsync

import org.mulesoft.als.server.textsync.TextDocumentContainer
import org.mulesoft.lsp.textsync.TextDocumentSyncConsumer

import scala.concurrent.Future
trait AlsTextDocumentSyncConsumer extends TextDocumentSyncConsumer {
  val uriToEditor: TextDocumentContainer

  def didFocus(params: DidFocusParams): Future[Unit]

  def deleteFile(uri: String): Unit =
    uriToEditor.remove(uri)
  def changeFile(oldUri: String, newUri: String): Unit = {
    val maybeDocument = uriToEditor.get(oldUri)
    uriToEditor.remove(oldUri)
    maybeDocument.foreach { td =>
      uriToEditor + (newUri, td)
    }
  }

}
