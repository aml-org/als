package org.mulesoft.lsp.configuration

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

@JSExportAll
@JSExportTopLevel(name = "WorkspaceClientCapabilities")
case class WorkspaceClientCapabilities(
    applyEdit: Option[Boolean] = None,
    workspaceEdit: Option[WorkspaceEditClientCapabilities] = None,
    didChangeConfiguration: Option[DidChangeConfigurationClientCapabilities] = None,
    didChangeWatchedFilesClientCapabilities: Option[DidChangeWatchedFilesClientCapabilities] = None,
    symbol: Option[WorkspaceSymbolClientCapabilities] = None,
    executeCommand: Option[ExecuteCommandClientCapabilities] = None
)

case class WorkspaceEditClientCapabilities(documentChanges: Option[Boolean])

case class DidChangeConfigurationClientCapabilities(dynamicRegistration: Option[Boolean])

case class DidChangeWatchedFilesClientCapabilities(dynamicRegistration: Option[Boolean])

case class WorkspaceSymbolClientCapabilities(dynamicRegistration: Option[Boolean])

case class ExecuteCommandClientCapabilities(dynamicRegistration: Option[Boolean])
