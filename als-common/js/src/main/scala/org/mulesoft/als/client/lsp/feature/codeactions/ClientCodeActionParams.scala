package org.mulesoft.als.client.lsp.feature.codeactions

import org.mulesoft.lsp.common.TextDocumentIdentifier

import scala.scalajs.js

@js.native
trait ClientCodeActionParams extends js.Object {
  def textDocument: TextDocumentIdentifier = js.native

  def range: Range = js.native

  def context: ClientCodeActionContext = js.native
}
