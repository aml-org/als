package org.mulesoft.als.client.lsp.edit

import org.mulesoft.als.client.lsp.common.ClientVersionedTextDocumentIdentifier

import scala.scalajs.js

@js.native
trait ClientTextDocumentEdit extends js.Object {
  def textDocument: ClientVersionedTextDocumentIdentifier = js.native
  def edits: js.Array[ClientTextEdit]                     = js.native
}
