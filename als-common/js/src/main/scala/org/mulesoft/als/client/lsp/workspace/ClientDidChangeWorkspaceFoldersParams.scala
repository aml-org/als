package org.mulesoft.als.client.lsp.workspace

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import org.mulesoft.als.client.convert.LspConvertersSharedToClient._
import org.mulesoft.lsp.workspace.DidChangeWorkspaceFoldersParams
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientDidChangeWorkspaceFoldersParams extends js.Object {
  def event: ClientWorkspaceFoldersChangeEvent = js.native
}

object ClientDidChangeWorkspaceFoldersParams {
  def apply(internal: DidChangeWorkspaceFoldersParams): ClientDidChangeWorkspaceFoldersParams =
    js.Dynamic
      .literal(event = internal.event.toClient)
      .asInstanceOf[ClientDidChangeWorkspaceFoldersParams]
}

// $COVERAGE-ON$