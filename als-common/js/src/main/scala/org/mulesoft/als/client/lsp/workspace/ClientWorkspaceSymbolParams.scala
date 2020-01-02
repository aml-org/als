package org.mulesoft.als.client.lsp.workspace

import org.mulesoft.lsp.workspace.WorkspaceSymbolParams

import scala.scalajs.js

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
