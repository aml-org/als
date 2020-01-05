package org.mulesoft.als.client.lsp.configuration

import org.mulesoft.lsp.configuration.WorkspaceServerCapabilities
import org.mulesoft.als.client.convert.LspConvertersSharedToClient._

import scala.scalajs.js
import scala.scalajs.js.JSConverters._


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


