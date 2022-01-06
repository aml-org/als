package org.mulesoft.als.server.modules.configuration

import org.mulesoft.als.configuration.ProjectConfiguration
import org.mulesoft.als.server.RequestModule
import org.mulesoft.als.server.feature.configuration.workspace._
import org.mulesoft.als.logger.Logger
import org.mulesoft.als.server.modules.workspace.WorkspaceContentManager
import org.mulesoft.als.server.workspace.WorkspaceManager
import org.mulesoft.amfintegration.amfconfiguration.ALSConfigurationState
import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.feature.telemetry.MessageTypes.MessageTypes
import org.mulesoft.lsp.feature.telemetry.{MessageTypes, TelemetryProvider}
import org.mulesoft.lsp.feature.{RequestType, TelemeteredRequestHandler}
import org.mulesoft.lsp.textsync.KnownDependencyScopes._
import org.mulesoft.lsp.textsync.{DependencyConfiguration, DidChangeConfigurationNotificationParams}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class WorkspaceConfigurationManager(val workspaceManager: WorkspaceManager,
                                    private val telemetryProvider: TelemetryProvider,
                                    private val logger: Logger)
    extends RequestModule[WorkspaceConfigurationClientCapabilities, WorkspaceConfigurationOptions]
    with WorkspaceConfigurationProvider {

  private var getEnabled = true
  override def getRequestHandlers: Seq[TelemeteredRequestHandler[_, _]] =
    Seq(new GetWorkspaceConfigurationRequestHandler(this, telemetryProvider))

  override def applyConfig(config: Option[WorkspaceConfigurationClientCapabilities]): WorkspaceConfigurationOptions = {
    getEnabled = config.forall(_.get)
    WorkspaceConfigurationOptions(true)
  }
  override val `type`: ConfigType[WorkspaceConfigurationClientCapabilities, WorkspaceConfigurationOptions] =
    WorkspaceConfigurationConfigType

  override def initialize(): Future[Unit] = Future.successful()

  def getWorkspaceConfiguration(uri: String): Future[(WorkspaceContentManager, ProjectConfiguration)] =
    workspaceManager
      .getWorkspace(uri)
      .flatMap(w => w.getConfigurationState.map(c => (w, c.projectState.config)))

  def getConfigurationState(uri: String): Future[ALSConfigurationState] =
    workspaceManager
      .getWorkspace(uri)
      .flatMap(w => w.getConfigurationState)
}

class GetWorkspaceConfigurationRequestHandler(val provider: WorkspaceConfigurationProvider,
                                              private val telemetryProvider: TelemetryProvider)
    extends TelemeteredRequestHandler[GetWorkspaceConfigurationParams, GetWorkspaceConfigurationResult] {

  override protected def telemetry: TelemetryProvider = telemetryProvider

  override protected def task(params: GetWorkspaceConfigurationParams): Future[GetWorkspaceConfigurationResult] =
    provider
      .getWorkspaceConfiguration(params.textDocument.uri)
      .map(t => {
        val manager = t._1
        val config  = t._2
        GetWorkspaceConfigurationResult(
          manager.folderUri,
          DidChangeConfigurationNotificationParams(
            config.mainFile,
            manager.folderUri,
            // todo: missing dialects?
            config.designDependency.map(Left(_))
              ++ config.validationDependency.map(p => Right(DependencyConfiguration(p, CUSTOM_VALIDATION)))
              ++ config.extensionDependency.map(p => Right(DependencyConfiguration(p, SEMANTIC_EXTENSION)))
          )
        )
      })

  override protected def code(params: GetWorkspaceConfigurationParams): String = "GetWorkspaceConfigurationRequest"

  override protected def beginType(params: GetWorkspaceConfigurationParams): MessageTypes =
    MessageTypes.BEGIN_GET_WORKSPACE_CONFIGURATION

  override protected def endType(params: GetWorkspaceConfigurationParams): MessageTypes =
    MessageTypes.END_GET_WORKSPACE_CONFIGURATION

  override protected def msg(params: GetWorkspaceConfigurationParams): String =
    s"Getting workspace configuration for workspace containing uri ${params.textDocument.uri}"

  override protected def uri(params: GetWorkspaceConfigurationParams): String = params.textDocument.uri

  override def `type`: RequestType[GetWorkspaceConfigurationParams, GetWorkspaceConfigurationResult] =
    GetWorkspaceConfigurationRequestType

  override protected val empty: Option[GetWorkspaceConfigurationResult] = None
}

private class EmptyConfigurationParams(workspaceFolder: String)
    extends DidChangeConfigurationNotificationParams(None, workspaceFolder, Set.empty)
