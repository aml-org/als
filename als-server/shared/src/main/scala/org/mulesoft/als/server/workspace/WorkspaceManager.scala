package org.mulesoft.als.server.workspace

import amf.core.remote.Platform
import org.mulesoft.als.common.URIImplicits._
import org.mulesoft.als.server.AlsWorkspaceService
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.modules.ast._
import org.mulesoft.als.server.modules.workspace.{CompilableUnit, WorkspaceContentManager}
import org.mulesoft.als.server.textsync.EnvironmentProvider
import org.mulesoft.als.server.workspace.command._
import org.mulesoft.als.server.workspace.extract._
import org.mulesoft.amfintegration.relationships.{AliasInfo, RelationshipLink}
import org.mulesoft.lsp.configuration.WorkspaceFolder
import org.mulesoft.lsp.feature.link.DocumentLink
import org.mulesoft.lsp.feature.telemetry.TelemetryProvider
import org.mulesoft.lsp.workspace.{DidChangeWorkspaceFoldersParams, ExecuteCommandParams}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class WorkspaceManager(environmentProvider: EnvironmentProvider,
                       telemetryProvider: TelemetryProvider,
                       val allSubscribers: List[BaseUnitListener],
                       override val dependencies: List[AccessUnits[CompilableUnit]],
                       logger: Logger)
    extends TextListener
    with UnitWorkspaceManager
    with UnitsManager[CompilableUnit, BaseUnitListenerParams]
    with AlsWorkspaceService {

  implicit val platform: Platform                  = environmentProvider.platform
  override def subscribers: List[BaseUnitListener] = allSubscribers.filter(_.isActive)
  private val rootHandler =
    new WorkspaceRootHandler(environmentProvider.platform, environmentProvider.environmentSnapshot())
  private val workspaces: ListBuffer[WorkspaceContentManager] = ListBuffer()

  def getWorkspace(uri: String): WorkspaceContentManager =
    workspaces.find(ws => ws.containsFile(uri)).getOrElse(defaultWorkspace)

  def initializeWS(root: String): Future[Unit] =
    rootHandler.extractConfiguration(root, logger).flatMap { mainOption =>
      if (!workspaces.exists(w => root.startsWith(w.folder))) { // if there is an existing WS containing the new one, dont add it
        logger
          .debug("Adding workspace: " + root, "WorkspaceManager", "initializeWS")
        val workspace: WorkspaceContentManager =
          new WorkspaceContentManager(root, environmentProvider, telemetryProvider, logger, subscribers)
        Future.sequence {
          replaceWorkspaces(root)
        } map (_ => {
          addWorkspace(mainOption, workspace)
        })
      } else Future.unit
    }

  private def addWorkspace(mainOption: Option[WorkspaceConf], workspace: WorkspaceContentManager): Unit = {
    this.synchronized {
      workspaces += workspace
    }
    workspace.setConfigMainFile(mainOption)
    mainOption.foreach(conf =>
      contentManagerConfiguration(workspace, conf.mainFile, conf.cachables, mainOption.flatMap(_.configReader)))
  }

  private def replaceWorkspaces(root: String) = {
    workspaces
      .filter(ws => ws.folder.startsWith(root))
      .map(w => {
        // We remove every workspace that is a subdirectory of the one being added
        logger.debug("Replacing Workspace: " + w.folder + " due to " + root, "WorkspaceManager", "initializeWS")
        shutdownWS(w)
      })
  }

  def shutdownWS(workspace: WorkspaceContentManager): Future[Unit] = {
    logger.debug("Removing workspace: " + workspace.folder, "WorkspaceManager", "shutdownWS")
    workspace.shutdown().map(_ => { this.synchronized { workspaces -= workspace } })
  }

  override def getUnit(uri: String, uuid: String): Future[CompilableUnit] =
    getWorkspace(uri.toAmfUri).getUnit(uri.toAmfUri)

  override def getLastUnit(uri: String, uuid: String): Future[CompilableUnit] =
    getUnit(uri.toAmfUri, uuid).flatMap(cu => if (cu.isDirty) getLastCU(cu, uri, uuid) else Future.successful(cu))

  private def getLastCU(cu: CompilableUnit, uri: String, uuid: String) =
    cu.getLast.flatMap {
      case newCu if newCu.isDirty => getLastUnit(uri, uuid)
      case newCu                  => Future.successful(newCu)
    }

  override def notify(uri: String, kind: NotificationKind): Unit = {
    val manager: WorkspaceContentManager = getWorkspace(uri.toAmfUri)
    if (manager.configFile
          .map(_.toAmfUri)
          .contains(uri.toAmfUri)) {
      manager.withConfiguration(ReaderWorkspaceConfigurationProvider(manager))
      manager.stage(uri.toAmfUri, CHANGE_CONFIG)
    } else manager.stage(uri.toAmfUri, kind)
  }

  def contentManagerConfiguration(manager: WorkspaceContentManager,
                                  mainSubUri: String,
                                  dependencies: Set[String],
                                  reader: Option[ConfigReader]): Unit =
    manager
      .withConfiguration(DefaultWorkspaceConfigurationProvider(manager, mainSubUri, dependencies, reader))
      .stage(mainSubUri, CHANGE_CONFIG)

  override def executeCommand(params: ExecuteCommandParams): Future[AnyRef] =
    commandExecutors.get(params.command) match {
      case Some(exe) =>
        exe.runCommand(params)
      case _ =>
        logger.error(s"Command [${params.command}] not recognized", "WorkspaceManager", "executeCommand")
        Future.successful(Unit) // future failed?
    }

  private val commandExecutors: Map[String, CommandExecutor[_, _]] = Map(
    Commands.DID_FOCUS_CHANGE_COMMAND -> new DidFocusCommandExecutor(logger, this),
    Commands.DID_CHANGE_CONFIGURATION -> new DidChangeConfigurationCommandExecutor(logger, this),
    Commands.INDEX_DIALECT            -> new IndexDialectCommandExecutor(logger, environmentProvider.amfConfiguration)
  )

  val defaultWorkspace =
    new WorkspaceContentManager("", environmentProvider, telemetryProvider, logger, subscribers)

  override def getProjectRootOf(uri: String): Option[String] =
    getWorkspace(uri).getRootFolderFor(uri)

  override def initialize(workspaceFolders: List[WorkspaceFolder]): Future[Unit] = {
    // Drop all old workspaces
    workspaces.clear()
    val newWorkspaces = extractCleanURIs(workspaceFolders)
    dependencies.foreach(d => d.withUnitAccessor(this))
    Future.sequence(newWorkspaces.map(initializeWS)) map (_ => {})
  }

  private def extractCleanURIs(workspaceFolders: List[WorkspaceFolder]) =
    workspaceFolders.flatMap(_.uri).sortBy(_.length).distinct

  override def didChangeWorkspaceFolders(params: DidChangeWorkspaceFoldersParams): Unit = {
    val event          = params.event
    val deletedFolders = event.deleted.flatMap(_.uri)

    workspaces
      .filter(p => deletedFolders.contains(p.folder))
      .foreach(shutdownWS)

    event.added.flatMap(_.uri).map(initializeWS)
  }

  dependencies.foreach(d => d.withUnitAccessor(this))

  override def getDocumentLinks(uri: String, uuid: String): Future[Seq[DocumentLink]] =
    getLastUnit(uri.toAmfUri, uuid).flatMap(_ =>
      getWorkspace(uri.toAmfUri).getRelationships(uri.toAmfUri).getDocumentLinks(uri.toAmfUri))

  override def getAllDocumentLinks(uri: String, uuid: String): Future[Map[String, Seq[DocumentLink]]] = {
    val workspace = getWorkspace(uri)
    workspace.mainFileUri match {
      case Some(mf) =>
        getLastUnit(mf, uuid)
          .flatMap(_ => workspace.getRelationships(mf).getAllDocumentLinks)
      case _ => Future.successful(Map.empty)
    }
  }

  override def getAliases(uri: String, uuid: String): Future[Seq[AliasInfo]] =
    getLastUnit(uri, uuid).flatMap(_ => getWorkspace(uri).getRelationships(uri).getAliases(uri))

  private def filterDuplicates(links: Seq[RelationshipLink]): Seq[RelationshipLink] = {
    val res = mutable.ListBuffer[RelationshipLink]()
    links.foreach { l =>
      if (!res.exists(_.relationshipIsEqual(l)))
        res += l
    }
    res
  }

  // tepm until have class for all relationships from visitors result associated to CU.
  override def getRelationships(uri: String, uuid: String): Future[(CompilableUnit, Seq[RelationshipLink])] =
    getLastUnit(uri, uuid)
      .flatMap(
        cu =>
          getWorkspace(uri)
            .getRelationships(uri)
            .getRelationships(uri)
            .map(rl => (cu, filterDuplicates(rl))))

  override def isInMainTree(uri: String): Boolean =
    getWorkspace(uri).isInMainTree(uri)
}
