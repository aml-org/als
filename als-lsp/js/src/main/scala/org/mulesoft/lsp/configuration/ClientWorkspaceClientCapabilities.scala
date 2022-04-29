package org.mulesoft.lsp.configuration

import org.mulesoft.lsp.convert.LspConvertersSharedToClient._

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientWorkspaceClientCapabilities extends js.Object {
  def workspaceEdit: js.UndefOr[ClientWorkspaceEditClientCapabilities] = js.native
}

object ClientWorkspaceClientCapabilities {
  def apply(internal: WorkspaceClientCapabilities): ClientWorkspaceClientCapabilities =
    js.Dynamic
      .literal(
        workspaceEdit = internal.workspaceEdit.map(_.toClient).orUndefined
      )
      .asInstanceOf[ClientWorkspaceClientCapabilities]
}

// $COVERAGE-ON$
