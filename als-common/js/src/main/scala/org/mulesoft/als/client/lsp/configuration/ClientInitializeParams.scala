package org.mulesoft.als.client.lsp.configuration

import org.mulesoft.als.client.convert.LspConvertersSharedToClient._
import org.mulesoft.lsp.configuration.{InitializeParams, WorkspaceFolder}

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.{UndefOr, |}
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientInitializeParams extends js.Object {
  def processId: Int = js.native // Nullable
  def rootPath: UndefOr[String] = js.native // Nullable
  def rootUri: UndefOr[String] = js.native // Nullable
  def capabilities: ClientClientCapabilities = js.native
  def initializationOptions: UndefOr[js.Any] = js.native
  def trace: UndefOr[String] = js.native
  def workspaceFolders: js.Array[ClientWorkspaceFolder] = js.native // Nullable
}

object ClientInitializeParams {
  def apply(internal: InitializeParams): ClientInitializeParams =
    js.Dynamic
      .literal(
        processId = internal.processId.getOrElse(null).asInstanceOf[js.Any],
        capabilities = internal.capabilities.toClient,
        trace = internal.trace.toString,
        rootUri = internal.rootUri.orUndefined,
        workspaceFolders = internal.workspaceFolders.map(_.map(_.toClient)).map(_.toJSArray).getOrElse(null).asInstanceOf[js.Any],
        rootPath = internal.rootPath.orUndefined,
        initializationOptions = internal.initializationOptions.collect{case js: js.Object => js}.orUndefined,
      )
      .asInstanceOf[ClientInitializeParams]
}

// $COVERAGE-ON$