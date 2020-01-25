package org.mulesoft.als.client.lsp.textsync

import org.mulesoft.als.client.convert.LspConvertersSharedToClient._
import org.mulesoft.als.client.lsp.common.ClientTextDocumentItem
import org.mulesoft.lsp.textsync.DidOpenTextDocumentParams

import scala.scalajs.js
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientDidOpenTextDocumentParams extends js.Object {
  def textDocument: ClientTextDocumentItem = js.native
}

object ClientDidOpenTextDocumentParams {
  def apply(internal: DidOpenTextDocumentParams): ClientDidOpenTextDocumentParams =
    js.Dynamic
      .literal(textDocument = internal.textDocument.toClient)
      .asInstanceOf[ClientDidOpenTextDocumentParams]
}

// $COVERAGE-ON$