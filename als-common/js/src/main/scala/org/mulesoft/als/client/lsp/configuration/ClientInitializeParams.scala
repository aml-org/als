package org.mulesoft.als.client.lsp.configuration

import org.mulesoft.als.client.convert.LspConvertersSharedToClient._
import org.mulesoft.lsp.configuration.InitializeParams

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.UndefOr

@js.native
trait ClientInitializeParams extends js.Object {
  def capabilities: ClientClientCapabilities = js.native
  def trace: Int = js.native
  def rootUri: UndefOr[String] = js.native
  def processId: UndefOr[Int] = js.native
  def workspaceFolders: UndefOr[js.Array[ClientWorkspaceFolder]] = js.native
  def rootPath: UndefOr[String] = js.native
  def initializationOptions: UndefOr[js.Object] = js.native
}

object ClientInitializeParams {
  def apply(internal: InitializeParams): ClientInitializeParams =
    js.Dynamic
      .literal(
        capabilities = internal.capabilities.toClient,
        trace = internal.trace.id,
        rootUri = internal.rootUri.orUndefined,
        processId = internal.processId.orUndefined,
        workspaceFolders = internal.workspaceFolders.map(a => a.map(_.toClient).toJSArray).orUndefined,
        rootPath = internal.rootPath.orUndefined,
        initializationOptions = internal.initializationOptions.collect{case js: js.Object => js}.orUndefined,
      )
      .asInstanceOf[ClientInitializeParams]
}
