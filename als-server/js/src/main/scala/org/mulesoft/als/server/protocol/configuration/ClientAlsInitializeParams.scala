package org.mulesoft.als.server.protocol.configuration

import org.mulesoft.lsp.configuration.ClientWorkspaceFolder

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.UndefOr
import org.mulesoft.lsp.convert.LspConvertersSharedToClient._
import org.mulesoft.als.server.protocol.convert.LspConvertersSharedToClient._
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientAlsInitializeParams extends js.Object {
  def processId: Int = js.native // Nullable
  def rootPath: UndefOr[String] = js.native // Nullable
  def rootUri: UndefOr[String] = js.native // Nullable
  def capabilities: ClientAlsClientCapabilities = js.native
  def initializationOptions: UndefOr[js.Any] = js.native
  def trace: UndefOr[String] = js.native
  def workspaceFolders: js.Array[ClientWorkspaceFolder] = js.native // Nullable
}

object ClientAlsInitializeParams{
  def apply(internal: AlsInitializeParams): ClientAlsInitializeParams = {
    js.Dynamic.literal(
      processId = internal.processId.getOrElse(null).asInstanceOf[js.Any],
      capabilities = internal.capabilities.toClient,
      trace = internal.trace.toString,
      rootUri = internal.rootUri.orUndefined,
      workspaceFolders = internal.workspaceFolders.map(_.map(_.toClient)).map(_.toJSArray).getOrElse(null).asInstanceOf[js.Any],
      rootPath = internal.rootPath.orUndefined,
      initializationOptions = internal.initializationOptions.collect{case js: js.Object => js}.orUndefined,
    ).asInstanceOf[ClientAlsInitializeParams]
  }
}
// $COVERAGE-ON$