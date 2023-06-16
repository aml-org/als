package org.mulesoft.als.server.protocol.configuration

import org.mulesoft.als.server.protocol.convert.LspConvertersSharedToClient._
import org.mulesoft.lsp.configuration.ClientWorkspaceFolder
import org.mulesoft.lsp.convert.LspConvertersSharedToClient._

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.UndefOr
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientAlsInitializeParams extends js.Object {
  def processId: Int                                    = js.native // Nullable
  def locale: UndefOr[String]                           = js.native
  def rootPath: UndefOr[String]                         = js.native // Nullable
  def rootUri: UndefOr[String]                          = js.native // Nullable
  def capabilities: ClientAlsClientCapabilities         = js.native
  def configuration: UndefOr[ClientAlsConfiguration]    = js.native // Nullable
  def initializationOptions: UndefOr[js.Any]            = js.native
  def trace: UndefOr[String]                            = js.native
  def workspaceFolders: js.Array[ClientWorkspaceFolder] = js.native // Nullable
  def hotReload: UndefOr[Boolean]                       = js.native
  def disableValidationAllTraces: UndefOr[Boolean]      = js.native
}

object ClientAlsInitializeParams {
  def apply(internal: AlsInitializeParams): ClientAlsInitializeParams = {
    js.Dynamic
      .literal(
        processId = internal.processId.getOrElse(null).asInstanceOf[js.Any],
        locale = internal.locale.getOrElse(null),
        capabilities = internal.capabilities.toClient,
        configuration = internal.configuration.map(_.toClient).orUndefined,
        trace = internal.trace.toString,
        rootUri = internal.rootUri.orUndefined,
        workspaceFolders =
          internal.workspaceFolders.map(_.map(_.toClient)).map(_.toJSArray).getOrElse(null).asInstanceOf[js.Any],
        rootPath = internal.rootPath.orUndefined,
        initializationOptions = internal.initializationOptions.collect { case js: js.Object => js }.orUndefined,
        hotReload = internal.hotReload.orUndefined,
        disableValidationAllTraces = internal.disableValidationAllTraces.orUndefined
      )
      .asInstanceOf[ClientAlsInitializeParams]
  }
}
// $COVERAGE-ON$
