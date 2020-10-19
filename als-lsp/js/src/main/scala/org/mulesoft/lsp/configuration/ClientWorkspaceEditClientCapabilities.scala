package org.mulesoft.lsp.configuration

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientWorkspaceEditClientCapabilities extends js.Object {
  def documentChanges: js.UndefOr[Boolean] = js.native
}

object ClientWorkspaceEditClientCapabilities {
  def apply(internal: WorkspaceEditClientCapabilities): ClientWorkspaceEditClientCapabilities =
    js.Dynamic
      .literal(
        documentChanges = internal.documentChanges.orUndefined
      )
      .asInstanceOf[ClientWorkspaceEditClientCapabilities]
}
// $COVERAGE-ON$
