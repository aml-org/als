package org.mulesoft.lsp.feature.common

import org.mulesoft.lsp.convert.LspConvertersSharedToClient._

import scala.scalajs.js
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientTextDocumentPositionParams extends js.Object {

  def textDocument: ClientTextDocumentIdentifier = js.native

  def position: ClientPosition = js.native
}

object ClientTextDocumentPositionParams {
  def apply(internal: TextDocumentPositionParams): ClientTextDocumentPositionParams =
    js.Dynamic
      .literal(textDocument = internal.textDocument.toClient, position = internal.position.toClient)
      .asInstanceOf[ClientTextDocumentPositionParams]
}

// $COVERAGE-ON$
