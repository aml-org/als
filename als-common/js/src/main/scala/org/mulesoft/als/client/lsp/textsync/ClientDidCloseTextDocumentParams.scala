package org.mulesoft.als.client.lsp.textsync

import org.mulesoft.als.client.convert.LspConvertersSharedToClient._
import org.mulesoft.als.client.lsp.common.ClientTextDocumentIdentifier
import org.mulesoft.lsp.textsync.DidCloseTextDocumentParams

import scala.scalajs.js

@js.native
trait ClientDidCloseTextDocumentParams extends js.Object {
  def textDocument: ClientTextDocumentIdentifier = js.native
}

object ClientDidCloseTextDocumentParams {
  def apply(internal: DidCloseTextDocumentParams): ClientDidCloseTextDocumentParams =
    js.Dynamic
      .literal(textDocument = internal.textDocument.toClient)
      .asInstanceOf[ClientDidCloseTextDocumentParams]
}
