package org.mulesoft.als.client.lsp.configuration

import org.mulesoft.lsp.configuration.{ClientCapabilities, WorkspaceClientCapabilities}

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

@JSExportAll
@JSExportTopLevel(name = "ClientCapabilities")
class ClientClientCapabilities(private val internal: ClientCapabilities) {
  def workspace: js.UndefOr[WorkspaceClientCapabilities]             = internal.workspace.orUndefined
  def textDocument: js.UndefOr[ClientTextDocumentClientCapabilities] = internal.textDocument.map().orUndefined
  def experimental: js.UndefOr[AnyRef]                               = internal.experimental.orUndefined
}
