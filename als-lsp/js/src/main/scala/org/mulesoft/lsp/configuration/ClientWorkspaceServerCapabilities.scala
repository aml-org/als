package org.mulesoft.lsp.configuration

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import org.mulesoft.lsp.convert.LspConvertersSharedToClient._
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientWorkspaceServerCapabilities extends js.Object {
  val workspaceFolders: js.UndefOr[ClientWorkspaceFolderServerCapabilities]
}

object ClientWorkspaceServerCapabilities {
  def apply(internal: WorkspaceServerCapabilities): ClientWorkspaceServerCapabilities =
    js.Dynamic
      .literal(
        workspaceFolders = internal.workspaceFolders.map(_.toClient).orUndefined
      )
      .asInstanceOf[ClientWorkspaceServerCapabilities]
}
// $COVERAGE-ON$
