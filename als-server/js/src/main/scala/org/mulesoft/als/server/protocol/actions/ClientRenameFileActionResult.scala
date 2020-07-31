package org.mulesoft.als.server.protocol.actions

import org.mulesoft.als.server.feature.renameFile.RenameFileActionResult
import org.mulesoft.lsp.convert.LspConvertersSharedToClient._
import org.mulesoft.lsp.edit.ClientWorkspaceEdit

import scala.scalajs.js

// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientRenameFileActionResult extends js.Object {
  def edits: ClientWorkspaceEdit = js.native
}

object ClientRenameFileActionResult {
  def apply(internal: RenameFileActionResult): ClientRenameFileActionResult = {
    js.Dynamic
      .literal(
        edits = internal.edits.toClient
      )
      .asInstanceOf[ClientRenameFileActionResult]
  }
}

// $COVERAGE-ON$
