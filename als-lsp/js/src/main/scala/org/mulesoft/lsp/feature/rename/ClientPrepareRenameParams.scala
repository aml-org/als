package org.mulesoft.lsp.feature.rename

import org.mulesoft.lsp.feature.common.{ClientPosition, ClientTextDocumentIdentifier}

import scala.scalajs.js
import org.mulesoft.lsp.convert.LspConvertersSharedToClient._
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientPrepareRenameParams extends js.Object {
  def textDocument: ClientTextDocumentIdentifier = js.native
  def position: ClientPosition                   = js.native
}

object ClientPrepareRenameParams {
  def apply(internal: PrepareRenameParams): ClientRenameParams =
    js.Dynamic
      .literal(
        textDocument = internal.textDocument.toClient,
        position = internal.position.toClient
      )
      .asInstanceOf[ClientRenameParams]
}

// $COVERAGE-ON$
