package org.mulesoft.als.client.lsp.edit

import org.mulesoft.als.client.lsp.common.ClientVersionedTextDocumentIdentifier
import org.mulesoft.lsp.edit.TextDocumentEdit

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import org.mulesoft.als.client.convert.LspConvertersSharedToClient._

@js.native
trait ClientTextDocumentEdit extends js.Object {
  def textDocument: ClientVersionedTextDocumentIdentifier = js.native
  def edits: js.Array[ClientTextEdit]                     = js.native
}

object ClientTextDocumentEdit {
  def apply(internal: TextDocumentEdit): ClientTextDocumentEdit =
    js.Dynamic
      .literal(textDocument = internal.textDocument.toClient, edits = internal.edits.map(_.toClient).toJSArray)
      .asInstanceOf[ClientTextDocumentEdit]
}
