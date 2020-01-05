package org.mulesoft.als.client.lsp.configuration

import org.mulesoft.als.client.convert.LspConvertersSharedToClient._
import org.mulesoft.lsp.configuration.InitializeParams

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.{UndefOr, |}

@js.native
trait ClientInitializeParams extends js.Object {
  def processId: Int = js.native // Nullable
  def rootPath: UndefOr[String] = js.native // Nullable
  def rootUri: UndefOr[String] = js.native // Nullable
  def capabilities: ClientClientCapabilities = js.native
  def initializationOptions: UndefOr[js.Any] = js.native
  def trace: UndefOr[String] = js.native
  def workspace: UndefOr[ClientWorkspaceServerCapabilities] = js.native
}

object ClientInitializeParams {
  def apply(internal: InitializeParams): ClientInitializeParams =
    js.Dynamic
      .literal(
        processId = internal.processId.getOrElse(null).asInstanceOf[js.Any],
        capabilities = internal.capabilities.toClient,
        trace = internal.trace.toString,
        rootUri = internal.rootUri.orUndefined,
        workspace = internal.workspace.map(_.toClient).orUndefined,
        rootPath = internal.rootPath.orUndefined,
        initializationOptions = internal.initializationOptions.collect{case js: js.Object => js}.orUndefined,
      )
      .asInstanceOf[ClientInitializeParams]
}
