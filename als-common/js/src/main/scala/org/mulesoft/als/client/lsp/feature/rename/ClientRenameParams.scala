package org.mulesoft.als.client.lsp.feature.rename

import org.mulesoft.als.client.convert.LspConvertersSharedToClient._
import org.mulesoft.als.client.lsp.common.{ClientPosition, ClientTextDocumentIdentifier}
import org.mulesoft.lsp.feature.rename.RenameParams

import scala.scalajs.js
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientRenameParams extends js.Object {
  def textDocument: ClientTextDocumentIdentifier = js.native
  def position: ClientPosition                   = js.native
  def newName: String                            = js.native
}

object ClientRenameParams {
  def apply(internal: RenameParams): ClientRenameParams =
    js.Dynamic
      .literal(
        textDocument = internal.textDocument.toClient,
        position = internal.position.toClient,
        newName = internal.newName
      )
      .asInstanceOf[ClientRenameParams]
}

// $COVERAGE-ON$