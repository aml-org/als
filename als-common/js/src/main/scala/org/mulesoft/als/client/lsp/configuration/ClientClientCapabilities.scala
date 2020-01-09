package org.mulesoft.als.client.lsp.configuration

import org.mulesoft.lsp.configuration.ClientCapabilities
import org.mulesoft.als.client.convert.LspConvertersSharedToClient._

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientClientCapabilities extends js.Object {
  def workspace: js.UndefOr[ClientWorkspaceClientCapabilities] = js.native

  def textDocument: js.UndefOr[ClientTextDocumentClientCapabilities] = js.native

  def experimental: js.UndefOr[js.Object] = js.native
}

object ClientClientCapabilities {
  def apply(internal: ClientCapabilities): ClientClientCapabilities = {
    val experimental: js.UndefOr[js.Object] = internal.experimental match {
      case Some(j: js.Object) => j
      case _                  => js.undefined
    }
    js.Dynamic
      .literal(
        workspace = internal.workspace.map(_.toClient).orUndefined,
        textDocument = internal.textDocument.map(_.toClient).orUndefined,
        experimental = experimental
      )
      .asInstanceOf[ClientClientCapabilities]
  }
}

// $COVERAGE-ON$