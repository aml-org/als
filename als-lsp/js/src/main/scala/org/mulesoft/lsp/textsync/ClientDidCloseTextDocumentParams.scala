package org.mulesoft.lsp.textsync

import org.mulesoft.lsp.feature.common.ClientTextDocumentIdentifier
import org.mulesoft.lsp.convert.LspConvertersSharedToClient.ClientTextDocumentIdentifierConverter
import scala.scalajs.js
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

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

// $COVERAGE-ON$
