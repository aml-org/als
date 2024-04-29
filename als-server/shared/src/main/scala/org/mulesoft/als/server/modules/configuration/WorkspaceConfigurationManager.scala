package org.mulesoft.als.server.modules.configuration

import org.mulesoft.als.configuration.ProjectConfiguration
import org.mulesoft.als.server.RequestModule
import org.mulesoft.als.server.feature.configuration.workspace._
import org.mulesoft.als.server.modules.workspace.{UnitNotFoundException, WorkspaceContentManager}
import org.mulesoft.als.server.workspace.WorkspaceManager
import org.mulesoft.amfintegration.amfconfiguration.ALSConfigurationState
import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.feature.telemetry.MessageTypes
import org.mulesoft.lsp.feature.telemetry.MessageTypes.MessageTypes
import org.mulesoft.lsp.feature.{RequestType, TelemeteredRequestHandler}
import org.mulesoft.lsp.textsync.KnownDependencyScopes._
import org.mulesoft.lsp.textsync.{DependencyConfiguration, DidChangeConfigurationNotificationParams}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class WorkspaceConfigurationManager(
    val workspaceManager: WorkspaceManager
) extends RequestModule[WorkspaceConfigurationClientCapabilities, WorkspaceConfigurationOptions]
    with WorkspaceConfigurationProvider {

  private var getEnabled = true
  override def getRequestHandlers: Seq[TelemeteredRequestHandler[_, _]] =
    Seq(new GetWorkspaceConfigurationRequestHandler(this))

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
      .flatMap {
        case wcm: WorkspaceContentManager => wcm.getConfigurationState.map(c => (wcm, c.projectState.config))
        case _                            => Future.failed(UnitNotFoundException(uri, ""))
      }

  def getConfigurationState(uri: String): Future[ALSConfigurationState] =
    workspaceManager
      .getWorkspace(uri)
      .flatMap {
        case wcm: WorkspaceContentManager => wcm.getConfigurationState
        case _                            => Future.failed(UnitNotFoundException(uri, ""))
      }
}

class GetWorkspaceConfigurationRequestHandler(
    val provider: WorkspaceConfigurationProvider
) extends TelemeteredRequestHandler[GetWorkspaceConfigurationParams, GetWorkspaceConfigurationResult] {
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
