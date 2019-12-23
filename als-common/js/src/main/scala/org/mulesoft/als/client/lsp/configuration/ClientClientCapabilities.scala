package org.mulesoft.als.client.lsp.configuration

import org.mulesoft.lsp.configuration.WorkspaceClientCapabilities

import scala.scalajs.js

@js.native
trait ClientClientCapabilities extends js.Object {
  def workspace: js.UndefOr[WorkspaceClientCapabilities]             = js.native
  def textDocument: js.UndefOr[ClientTextDocumentClientCapabilities] = js.native
  def experimental: js.UndefOr[AnyRef]                               = js.native
}
