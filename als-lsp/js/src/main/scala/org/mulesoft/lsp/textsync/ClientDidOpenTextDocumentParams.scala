package org.mulesoft.lsp.textsync

import org.mulesoft.lsp.feature.common.ClientTextDocumentItem
import org.mulesoft.lsp.convert.LspConvertersSharedToClient.ClientTextDocumentItemConverter
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
