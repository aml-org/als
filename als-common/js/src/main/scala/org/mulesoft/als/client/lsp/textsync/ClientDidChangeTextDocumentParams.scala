package org.mulesoft.als.client.lsp.textsync

import org.mulesoft.als.client.convert.LspConvertersSharedToClient._
import org.mulesoft.als.client.lsp.common.ClientVersionedTextDocumentIdentifier
import org.mulesoft.lsp.textsync.DidChangeTextDocumentParams

import scala.scalajs.js
import scala.scalajs.js.JSConverters._

@js.native
trait ClientDidChangeTextDocumentParams extends js.Object {
  def textDocument: ClientVersionedTextDocumentIdentifier            = js.native
  def contentChanges: js.Array[ClientTextDocumentContentChangeEvent] = js.native
}

object ClientDidChangeTextDocumentParams {
  def apply(internal: DidChangeTextDocumentParams): ClientDidChangeTextDocumentParams =
    js.Dynamic
      .literal(
        textDocument = internal.textDocument.toClient,
        contentChanges = internal.contentChanges.map(_.toClient).toJSArray
      )
      .asInstanceOf[ClientDidChangeTextDocumentParams]
}
