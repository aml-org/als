package org.mulesoft.lsp.workspace

import org.mulesoft.lsp.configuration.ClientWorkspaceFolder
import org.mulesoft.lsp.convert.LspConvertersSharedToClient._

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
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
