package org.mulesoft.als.server.workspace

import org.mulesoft.als.common.FileUtils
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.modules.ast.{BaseUnitListener, CHANGE_CONFIG, NotificationKind, TextListener}
import org.mulesoft.als.server.modules.serialization.SerializationManager
import org.mulesoft.als.server.modules.workspace.{CompilableUnit, WorkspaceContentManager}
import org.mulesoft.als.server.textsync.EnvironmentProvider
import org.mulesoft.als.server.workspace.command._
import org.mulesoft.als.server.workspace.extract.{
  ConfigReader,
  DefaultWorkspaceConfigurationProvider,
  ReaderWorkspaceConfigurationProvider,
  WorkspaceRootHandler
}
import org.mulesoft.amfmanager.AmfInitializationHandler
import org.mulesoft.lsp.Initializable
import org.mulesoft.lsp.feature.telemetry.TelemetryProvider
import org.mulesoft.lsp.server.LanguageServerSystemConf
import org.mulesoft.lsp.workspace.{ExecuteCommandParams, WorkspaceService}

import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class WorkspaceManager(environmentProvider: EnvironmentProvider,
                       telemetryProvider: TelemetryProvider,
                       dependencies: List[BaseUnitListener],
                       logger: Logger,
                       configuration: LanguageServerSystemConf)
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
      new WorkspaceContentManager(folder, environmentProvider, telemetryProvider, logger, dependencies, configuration)
    workspace.setConfigMainFile(mainOption)
    mainOption.foreach(conf =>
      contentManagerConfiguration(workspace, conf.mainFile, conf.cachables, mainOption.flatMap(_.configReader)))

    workspaces += workspace
  }

  def getUnit(uri: String, uuid: String): Future[CompilableUnit] =
    getWorkspace(uri).getCompilableUnit(uri)

  override def notify(uri: String, kind: NotificationKind): Unit = {
    val manager: WorkspaceContentManager = getWorkspace(uri)
    if (manager.configFile.map(FileUtils.getEncodedUri(_, configuration.platform)).contains(uri)) {
      manager.withConfiguration(ReaderWorkspaceConfigurationProvider(manager))
      manager.changedFile(uri, CHANGE_CONFIG)
    } else manager.changedFile(uri, kind)
  }

  def contentManagerConfiguration(manager: WorkspaceContentManager,
                                  mainUri: String,
                                  dependencies: Set[String],
                                  reader: Option[ConfigReader]): Unit = {
    manager
      .withConfiguration(DefaultWorkspaceConfigurationProvider(manager, mainUri, dependencies, reader))
      .changedFile(mainUri, CHANGE_CONFIG)
  }

  override def executeCommand(params: ExecuteCommandParams): Future[AnyRef] = {
    commandExecutors.get(params.command) match {
      case Some(exe) =>
        exe.runCommand(params)
      case _ =>
        logger.error(s"Command [${params.command}] not recognized", "WorkspaceManager", "executeCommand")
        Future.successful(Unit) // future failed?
    }
  }

  private val commandExecutors: Map[String, CommandExecutor[_, _]] = Map(
    Commands.DID_FOCUS_CHANGE_COMMAND -> new DidFocusCommandExecutor(logger, this),
    Commands.DID_CHANGE_CONFIGURATION -> new DidChangeConfigurationCommandExecutor(logger, this),
    Commands.INDEX_DIALECT            -> new IndexDialectCommandExecutor(logger, environmentProvider.platform),
    Commands.COMPILE                  -> new RequestCompileCommandExecutor(logger, this, environmentProvider.platform)
  )

  val defaultWorkspace =
    new WorkspaceContentManager("", environmentProvider, telemetryProvider, logger, dependencies, configuration)

  override def initialize(): Future[Unit] = AmfInitializationHandler.init()
}
