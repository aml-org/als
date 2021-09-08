package org.mulesoft.als.server.workspace

import amf.core.internal.remote.Platform
import org.mulesoft.als.common.URIImplicits._
import org.mulesoft.als.configuration.{DefaultProjectConfigurationStyle, ProjectConfigurationStyle}
import org.mulesoft.als.server.AlsWorkspaceService
import org.mulesoft.als.logger.Logger
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
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class WorkspaceManager protected (environmentProvider: EnvironmentProvider,
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
  private val workspaces                           = new WorkspaceList(environmentProvider, telemetryProvider, allSubscribers, logger)

  def getWorkspace(uri: String): Future[WorkspaceContentManager] =
    workspaces.findWorkspace(uri)

  override def getUnit(uri: String, uuid: String): Future[CompilableUnit] =
    getWorkspace(uri.toAmfUri).flatMap(_.getUnit(uri.toAmfUri))

  override def getLastUnit(uri: String, uuid: String): Future[CompilableUnit] =
    getUnit(uri.toAmfUri, uuid).flatMap(cu => if (cu.isDirty) getLastCU(cu, uri, uuid) else Future.successful(cu))

  private def getLastCU(cu: CompilableUnit, uri: String, uuid: String) =
    cu.getLast.flatMap {
      case newCu if newCu.isDirty => getLastUnit(uri, uuid)
      case newCu                  => Future.successful(newCu)
    }

  override def notify(uri: String, kind: NotificationKind): Future[Unit] = getWorkspace(uri.toAmfUri).flatMap {
    manager =>
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
                                  profiles: Set[String],
                                  semanticExtensions: Set[String],
                                  reader: Option[ConfigReader]): Unit = {
    logger.debug(
      s"Workspace '${manager.folderUri}' new configuration { mainFile: $mainSubUri, dependencies: $dependencies, profiles: $profiles }",
      "WorkspaceManager",
      "contentManagerConfiguration"
    )
    manager
      .withConfiguration(
        DefaultWorkspaceConfigurationProvider(manager, mainSubUri, dependencies, profiles, semanticExtensions, reader))
      .stage(mainSubUri, CHANGE_CONFIG)
  }

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

  override def getProjectRootOf(uri: String): Future[Option[String]] =
    getWorkspace(uri).flatMap(_.getRootFolderFor(uri))

  override def initialize(workspaceFolders: List[WorkspaceFolder],
                          projectConfigurationStyle: ProjectConfigurationStyle): Future[Unit] =
    workspaces
      .reset(projectConfigurationStyle)
      .flatMap { _ => // Drop all old workspaces
        val newWorkspaces = extractCleanURIs(workspaceFolders)
        dependencies.foreach(d => d.withUnitAccessor(this))
        workspaces.changeWorkspaces(newWorkspaces, List())
      }
      .flatMap(_ => Future.sequence(workspaces.allWorkspaces().map(_.initialized)))
      .flatMap(_ => Future.unit)

  private def extractCleanURIs(workspaceFolders: List[WorkspaceFolder]) =
    workspaceFolders.flatMap(_.uri).sortBy(_.length).distinct

  override def didChangeWorkspaceFolders(params: DidChangeWorkspaceFoldersParams): Future[Unit] =
    changeWorkspaceFolders(params)

  def changeWorkspaceFolders(params: DidChangeWorkspaceFoldersParams): Future[Unit] =
    workspaces.changeWorkspaces(params.event.added.flatMap(_.uri), params.event.deleted.flatMap(_.uri))

  def getWorkspaceFolders: Seq[String] = workspaces.allWorkspaces().map(_.folderUri)

  override def getDocumentLinks(uri: String, uuid: String): Future[Seq[DocumentLink]] =
//    getLastUnit(uri.toAmfUri, uuid).flatMap(_ =>
    getWorkspace(uri.toAmfUri).flatMap(_.getRelationships(uri.toAmfUri)).map(_.getDocumentLinks(uri.toAmfUri))
  //)

  override def getAllDocumentLinks(uri: String, uuid: String): Future[Map[String, Seq[DocumentLink]]] =
    for {
      workspace   <- getWorkspace(uri)
      mainFileUri <- workspace.mainFileUri
      _           <- mainFileUri.map(getLastUnit(_, uuid)).getOrElse(Future.unit)
      allLinks <- mainFileUri
        .map(workspace.getRelationships(_).map(_.getAllDocumentLinks))
        .getOrElse(Future.successful(Map[String, Seq[DocumentLink]]()))
    } yield allLinks

  override def getAliases(uri: String, uuid: String): Future[Seq[AliasInfo]] =
    getLastUnit(uri, uuid).flatMap(_ => getWorkspace(uri)).flatMap(_.getRelationships(uri)).map(_.getAliases(uri))

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
    for {
      cu        <- getLastUnit(uri, uuid)
      workspace <- getWorkspace(uri)
      relationships <- workspace
        .getRelationships(uri)
    } yield {
      (cu, filterDuplicates(relationships.getRelationships(uri)))
    }

  override def isInMainTree(uri: String): Future[Boolean] =
    workspaces.findWorkspace(uri.toAmfUri).map(_.isInMainTree(uri))
}

class WorkspaceList(environmentProvider: EnvironmentProvider,
                    telemetryProvider: TelemetryProvider,
                    val allSubscribers: List[BaseUnitListener],
                    logger: Logger) {

  def subscribers: List[BaseUnitListener] = allSubscribers.filter(_.isActive)

  private val workspaces: mutable.Set[WorkspaceContentManager] = new mutable.HashSet()

  private var projectConfigurationStyle: Option[ProjectConfigurationStyle] = None

  private def configStyle: ProjectConfigurationStyle =
    projectConfigurationStyle.getOrElse(DefaultProjectConfigurationStyle)

  private val defaultWorkspace: Future[WorkspaceContentManager] = {
    logger.debug(s"created default WorkspaceContentManager", "WorkspaceList", "buildWorkspaceAt")
    WorkspaceContentManager("", environmentProvider, telemetryProvider, logger, subscribers, configStyle)
  }

  def removeWorkspace(uri: String): Future[Unit] =
    changeWorkspaces(List.empty, List(uri))

  def changeWorkspaces(added: List[String], deleted: List[String]): Future[Unit] = synchronized {
    Future.sequence(workspaces.map(_.initialized)).flatMap { _ =>
      logger.debug(s"Changing workspaces, added: $added, deleted: $deleted", "WorkspaceList", "changeWorkspace")
      val newWorkspaces = added.filterNot(uri => workspaces.exists(_.folderUri == uri)).map(getOrCreateWorkspaceAt)
      val oldWorkspaces = workspaces.filter(wcm => deleted.contains(wcm.folderUri)) ++
        workspaces.collect({
          case wcm: WorkspaceContentManager if added.exists(uri => wcm.folderUri.startsWith(uri)) => wcm
        })
      Future
        .sequence(oldWorkspaces.map(_.shutdown()))
        .flatMap(
          _ =>
            Future
              .sequence(newWorkspaces)
              .map { nw =>
                workspaces --= oldWorkspaces
                workspaces ++= nw
            })

    }
  }

  private def getOrCreateWorkspaceAt(uri: String): Future[WorkspaceContentManager] =
    Future
      .sequence(workspaces.map(_.initialized))
      .flatMap(
        _ =>
          workspaces
            .find(w => uri.startsWith(w.folderUri)) // if there is an existing WS containing the new one, do not create it
            .map(Future.successful)
            .getOrElse(buildWorkspaceAt(uri)))

  private def buildWorkspaceAt(uri: String): Future[WorkspaceContentManager] = {
    val applicableFiles = environmentProvider.openedFiles.filter(_.startsWith(uri))
    for {
      wcm <- WorkspaceContentManager(uri,
                                     environmentProvider.branch,
                                     telemetryProvider,
                                     logger,
                                     subscribers,
                                     configStyle)
      _ <- Future.sequence(applicableFiles.map(wcm.stage(_, OPEN_FILE)))
      _ <- wcm.initialized
    } yield {
      logger.debug(s"created WorkspaceContentManager for $uri", "WorkspaceList", "buildWorkspaceAt")
      wcm
    }
  }

  def findWorkspace(uri: String): Future[WorkspaceContentManager] =
    for {
      _ <- Future.sequence(workspaces.map(_.initialized))
      wcm <- workspaces.find(ws => ws.containsFile(uri)).map(Future.successful).getOrElse {
        logger.debug(s"getting default workspace", "WorkspaceList", "findWorkspace")
        defaultWorkspace
      }
    } yield wcm

  def clear(): Future[Unit] =
    Future
      .sequence(workspaces.map(w => removeWorkspace(w.folderUri)))
      .flatMap(_ => Future.unit)

  def reset(configuration: ProjectConfigurationStyle): Future[Unit] = {
    projectConfigurationStyle = Some(configuration)
    this.clear()
  }

  def allWorkspaces(): Seq[WorkspaceContentManager] = workspaces.toSeq
}

object WorkspaceManager {
  def apply(environmentProvider: EnvironmentProvider,
            telemetryProvider: TelemetryProvider,
            allSubscribers: List[BaseUnitListener],
            dependencies: List[AccessUnits[CompilableUnit]],
            logger: Logger): WorkspaceManager = {
    val wm = new WorkspaceManager(environmentProvider, telemetryProvider, allSubscribers, dependencies, logger)
    wm.dependencies.foreach(d => d.withUnitAccessor(wm))
    wm
  }
}
