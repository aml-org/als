package org.mulesoft.als.server.workspace

import org.mulesoft.als.common.FileUtils
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.modules.ast.{BaseUnitListener, CHANGE_CONFIG, NotificationKind, TextListener}
import org.mulesoft.als.server.modules.workspace.{CompilableUnit, WorkspaceContentManager}
import org.mulesoft.als.server.textsync.EnvironmentProvider
import org.mulesoft.als.server.workspace.command._
import org.mulesoft.als.server.workspace.extract.{
  ConfigReader,
  DefaultWorkspaceConfigurationProvider,
  ReaderWorkspaceConfigurationProvider,
  WorkspaceRootHandler
}
import org.mulesoft.lsp.Initializable
import org.mulesoft.lsp.configuration.WorkspaceFolder
import org.mulesoft.lsp.feature.telemetry.TelemetryProvider
import org.mulesoft.lsp.workspace.{DidChangeWorkspaceFoldersParams, ExecuteCommandParams, WorkspaceService}

import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class WorkspaceManager(environmentProvider: EnvironmentProvider,
                       telemetryProvider: TelemetryProvider,
                       dependencies: List[BaseUnitListener],
                       logger: Logger)
    extends TextListener
    with UnitRepositoriesManager
    with WorkspaceService
    with Initializable {

  private val rootHandler                                     = new WorkspaceRootHandler(environmentProvider.platform)
  private val workspaces: ListBuffer[WorkspaceContentManager] = ListBuffer()

  def getWorkspace(uri: String): WorkspaceContentManager =
    workspaces.find(ws => uri.startsWith(ws.folder)).getOrElse(defaultWorkspace)

  private def initializeWSList(newWorkspaces: List[String]): Future[Unit] = {

    val currentWorkspaces = workspaces.map(_.folder)

    // filter newWorkspaces that are contained in currentWorkspaces
    val newFilteredWorkspaces =
      newWorkspaces.distinct.filterNot(f => currentWorkspaces.exists(p => { f.startsWith(p) }))

    if (newFilteredWorkspaces.nonEmpty) {
      val tuples: List[(String, List[WorkspaceContentManager])] = newFilteredWorkspaces.map(uri => {
        (uri, workspaces.filter(wsm => wsm.folder.startsWith(uri) && !uri.equals(wsm.folder)).toList)
      })
      Future.reduceLeft(
        tuples.map(t => {
          initializeWS(t._1, Some(t._2))
        })
      )((_, _) => ())
    } else Future.successful()
  }

  def initializeWS(root: String): Future[Unit] = initializeWS(root, None)

  private def initializeWS(root: String, replace: Option[List[WorkspaceContentManager]]): Future[Unit] =
    rootHandler.extractConfiguration(root).map { mainOption =>
      logger.debug("Adding workspace: " + root, "WorkspaceManager", "initializeWS")
      val workspace: WorkspaceContentManager =
        new WorkspaceContentManager(root, environmentProvider, telemetryProvider, logger, dependencies)
      if (replace.isDefined) {
        replace.get.foreach(w => {
          logger.debug("Replacing Workspace: " + w.folder + " due to " + workspace.folder,
                       "WorkspaceManager",
                       "initializeWS")
          shutdownWS(w)
        })
      }
      workspaces += workspace
      workspace.setConfigMainFile(mainOption)
      mainOption.foreach(conf =>
        contentManagerConfiguration(workspace, conf.mainFile, conf.cachables, mainOption.flatMap(_.configReader)))
    }

  def shutdownWS(workspace: WorkspaceContentManager): Unit = {
    logger.debug("Removing workspace: " + workspace.folder, "WorkspaceManager", "shutdownWS")
    workspaces -= workspace
    workspace.shutdown()
  }

  override def getCU(uri: String, uuid: String): Future[CompilableUnit] =
    getWorkspace(uri).getCompilableUnit(uri)

  override def getLastCU(uri: String, uuid: String): Future[CompilableUnit] = {
    getCU(uri, uuid).flatMap(CU => {
      if (CU.isDirty) getLastCU(uri, uuid) else Future.successful(CU)
    })
  }
  override def notify(uri: String, kind: NotificationKind): Unit = {
    val manager: WorkspaceContentManager = getWorkspace(uri)
    if (manager.configFile.map(FileUtils.getEncodedUri(_, environmentProvider.platform)).contains(uri)) {
      manager.withConfiguration(ReaderWorkspaceConfigurationProvider(manager))
      manager.changedFile(Some(uri), CHANGE_CONFIG)
    } else manager.changedFile(Some(uri), kind)
  }

  def contentManagerConfiguration(manager: WorkspaceContentManager,
                                  mainUri: String,
                                  dependencies: Set[String],
                                  reader: Option[ConfigReader]): Unit = {
    manager
      .withConfiguration(DefaultWorkspaceConfigurationProvider(manager, mainUri, dependencies, reader))
      .changedFile(Some(mainUri), CHANGE_CONFIG)
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
    Commands.INDEX_DIALECT            -> new IndexDialectCommandExecutor(logger, environmentProvider.amfConfiguration)
  )

  val defaultWorkspace =
    new WorkspaceContentManager("", environmentProvider, telemetryProvider, logger, dependencies)

  override def initialize(): Future[Unit] = {
    Future.successful(dependencies.foreach(d => d.withUnitAccessor(this)))
  }

  override def getRootOf(uri: String): Option[String] =
    getWorkspace(uri).workspaceConfiguration.map(c => s"${c.rootFolder}/")

  override def initialize(root: Option[String], workspaceFolders: Option[Seq[WorkspaceFolder]]): Future[Unit] = {
    // Drop all old workspaces
    workspaces.clear()

    if (root.isDefined) initializeWSList((workspaceFolders.getOrElse(List()).flatMap(_.uri) ++ root).toList)
    else Future.successful()
  }

  override def didChangeWorkspaceFolders(params: DidChangeWorkspaceFoldersParams): Unit = {
    val event          = params.event
    val deletedFolders = event.deleted.flatMap(_.uri)

    workspaces.filter(p => deletedFolders.contains(p.folder)).foreach(shutdownWS)

    val addedFolders: List[String] = event.added.flatMap(_.uri)
    if (addedFolders.nonEmpty) {
      initializeWSList(addedFolders)
    }
  }

}
