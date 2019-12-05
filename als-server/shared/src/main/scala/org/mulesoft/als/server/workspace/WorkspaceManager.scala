package org.mulesoft.als.server.workspace

import amf.core.remote.Platform
import amf.internal.environment.Environment
import org.mulesoft.als.common.FileUtils
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.modules.ast.{BaseUnitListener, CHANGE_CONFIG, NotificationKind, TextListener}
import org.mulesoft.als.server.modules.workspace.{CompilableUnit, WorkspaceContentManager}
import org.mulesoft.als.server.textsync.EnvironmentProvider
import org.mulesoft.als.server.workspace.command.{
  CommandExecutor,
  Commands,
  DidFocusCommandExecutor,
  IndexDialectCommandExecutor
}
import org.mulesoft.als.server.workspace.extract.{WorkspaceConf, WorkspaceRootHandler}
import org.mulesoft.amfmanager.AmfInitializationHandler
import org.mulesoft.lsp.Initializable
import org.mulesoft.lsp.feature.telemetry.TelemetryProvider
import org.mulesoft.lsp.workspace.{ExecuteCommandParams, WorkspaceService}

import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class WorkspaceManager(environmentProvider: EnvironmentProvider,
                       telemetryProvider: TelemetryProvider,
                       dependencies: List[BaseUnitListener],
                       logger: Logger,
                       platform: Platform)
    extends TextListener
    with WorkspaceService
    with Initializable {

  private val rootHandler                                     = new WorkspaceRootHandler(environmentProvider.platform)
  private val workspaces: ListBuffer[WorkspaceContentManager] = ListBuffer()
  //  private var documentContainer:TextDocumentContainer = DefaultEnvironmentProvider

  def getWorkspace(uri: String): WorkspaceContentManager =
    workspaces.find(ws => uri.startsWith(ws.folder)).getOrElse(defaultWorkspace)

  def initializeWS(folder: String): Future[Unit] = rootHandler.extractConfiguration(folder).map { mainOption =>
    val workspace =
      new WorkspaceContentManager(folder,
                                  mainOption,
                                  environmentProvider,
                                  telemetryProvider,
                                  logger,
                                  dependencies,
                                  platform)
    workspaces += workspace
    workspace.initialize()
  }

  def getUnit(uri: String, uuid: String): Future[CompilableUnit] =
    getWorkspace(uri).getCompilableUnit(uri) // todo

  override def notify(uri: String, kind: NotificationKind): Unit = {
    val manager: WorkspaceContentManager = getWorkspace(uri)
    if (manager.configFile.map(FileUtils.getEncodedUri(_, platform)).contains(uri)) {
      manager.changeConfigurationProvider((platform: Platform, environment: Environment) => {
        manager.workspaceConfiguration match {
          case Some(conf) =>
            conf.configReader.readRoot(manager.folder, platform, environment)
          case _ => Future.successful(None)
        }
      })
      manager.changedFile(uri, CHANGE_CONFIG)
    } else manager.changedFile(uri, kind)
  }

  override def executeCommand(params: ExecuteCommandParams): Future[AnyRef] =
    Future {
      commandExecutors.get(params.command) match {
        case Some(exe) => exe.runCommand(params)
        case _ =>
          logger.error(s"Command [${params.command}] not recognized", getClass.getCanonicalName, "executeCommand")
      }
      Unit
    }

  private val commandExecutors: Map[String, CommandExecutor[_]] = Map(
    Commands.DID_FOCUS_CHANGE_COMMAND -> new DidFocusCommandExecutor(logger, this),
    Commands.INDEX_DIALECT            -> new IndexDialectCommandExecutor(logger, environmentProvider.platform)
  )

  val defaultWorkspace =
    new WorkspaceContentManager("", None, environmentProvider, telemetryProvider, logger, dependencies, platform)

  override def initialize(): Future[Unit] = AmfInitializationHandler.init()
}

trait WorkspaceConfigurationProvider {
  def obtainConfiguration(platform: Platform, environment: Environment): Future[Option[WorkspaceConf]]
}
