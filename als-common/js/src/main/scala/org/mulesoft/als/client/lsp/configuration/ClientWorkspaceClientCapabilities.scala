package org.mulesoft.als.client.lsp.configuration

import org.mulesoft.lsp.configuration.WorkspaceClientCapabilities

import scala.scalajs.js

@js.native
trait ClientWorkspaceClientCapabilities extends js.Object

object ClientWorkspaceClientCapabilities {
  def apply(internal: WorkspaceClientCapabilities): ClientWorkspaceClientCapabilities =
    js.Dynamic
      .literal()
      .asInstanceOf[ClientWorkspaceClientCapabilities]
}
