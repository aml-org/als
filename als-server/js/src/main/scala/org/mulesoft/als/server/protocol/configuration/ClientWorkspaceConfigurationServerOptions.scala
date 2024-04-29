package org.mulesoft.als.server.protocol.configuration

import org.mulesoft.als.server.feature.configuration.workspace.WorkspaceConfigurationOptions

import scala.scalajs.js
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS
@js.native
trait ClientWorkspaceConfigurationServerOptions extends js.Object {
  def supported: Boolean = js.native
}

object ClientWorkspaceConfigurationServerOptions {
  def apply(internal: WorkspaceConfigurationOptions): ClientWorkspaceConfigurationServerOptions = {
    js.Dynamic
      .literal(
        supported = internal.supported
      )
      .asInstanceOf[ClientWorkspaceConfigurationServerOptions]
  }
}
// $COVERAGE-ON$
