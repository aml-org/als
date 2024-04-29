package org.mulesoft.lsp.workspace

import scala.scalajs.js
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientWorkspaceSymbolParams extends js.Object {
  def query: String = js.native
}

object ClientWorkspaceSymbolParams {
  def apply(internal: WorkspaceSymbolParams): ClientWorkspaceSymbolParams =
    js.Dynamic
      .literal(query = internal.query)
      .asInstanceOf[ClientWorkspaceSymbolParams]
}
// $COVERAGE-ON$
