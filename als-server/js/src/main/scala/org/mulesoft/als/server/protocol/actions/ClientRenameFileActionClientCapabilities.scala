package org.mulesoft.als.server.protocol.actions

import org.mulesoft.als.server.feature.renamefile.RenameFileActionClientCapabilities

import scala.scalajs.js

// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientRenameFileActionClientCapabilities extends js.Object {
  def enabled: Boolean = js.native
}

object ClientRenameFileActionClientCapabilities {
  def apply(internal: RenameFileActionClientCapabilities): ClientRenameFileActionClientCapabilities = {
    js.Dynamic
      .literal(
        enabled = internal.enabled
      )
      .asInstanceOf[ClientRenameFileActionClientCapabilities]
  }
}
// $COVERAGE-ON$
