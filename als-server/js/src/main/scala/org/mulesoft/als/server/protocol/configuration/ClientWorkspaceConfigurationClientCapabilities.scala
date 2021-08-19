package org.mulesoft.als.server.protocol.configuration

import org.mulesoft.als.server.feature.configuration.workspace.WorkspaceConfigurationClientCapabilities

import scala.scalajs.js
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS
@js.native
trait ClientWorkspaceConfigurationClientCapabilities extends js.Object {
  def get: Boolean = js.native
}

object ClientWorkspaceConfigurationClientCapabilities {
  def apply(internal: WorkspaceConfigurationClientCapabilities): ClientWorkspaceConfigurationClientCapabilities = {
    js.Dynamic
      .literal(
        get = internal.get
      )
      .asInstanceOf[ClientWorkspaceConfigurationClientCapabilities]
  }
}
// $COVERAGE-ON$
