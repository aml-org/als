package org.mulesoft.lsp.workspace

import org.mulesoft.lsp.convert.LspConvertersSharedToClient._

import scala.scalajs.js
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
