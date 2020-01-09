package org.mulesoft.als.client.lsp.workspace

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import org.mulesoft.als.client.convert.LspConvertersSharedToClient._
import org.mulesoft.als.client.lsp.configuration.ClientWorkspaceFolder
import org.mulesoft.lsp.workspace.WorkspaceFoldersChangeEvent
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientWorkspaceFoldersChangeEvent extends js.Object {
  def added: js.Array[ClientWorkspaceFolder]   = js.native
  def deleted: js.Array[ClientWorkspaceFolder] = js.native
}

object ClientWorkspaceFoldersChangeEvent {
  def apply(internal: WorkspaceFoldersChangeEvent): ClientWorkspaceFoldersChangeEvent =
    js.Dynamic
      .literal(added = internal.added.map(_.toClient).toJSArray, deleted = internal.deleted.map(_.toClient).toJSArray)
      .asInstanceOf[ClientWorkspaceFoldersChangeEvent]
}
// $COVERAGE-ON$
