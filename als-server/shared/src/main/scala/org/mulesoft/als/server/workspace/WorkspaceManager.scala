package org.mulesoft.als.server.workspace

import amf.core.internal.remote.Platform
import org.mulesoft.als.common.URIImplicits._
import org.mulesoft.als.logger.Logger
import org.mulesoft.als.server.AlsWorkspaceService
import org.mulesoft.als.server.modules.ast._
import org.mulesoft.als.server.modules.configuration.ConfigurationProvider
import org.mulesoft.als.server.modules.workspace.{CompilableUnit, ProjectConfigurationAdapter, WorkspaceContentManager}
import org.mulesoft.als.server.textsync.EnvironmentProvider
import org.mulesoft.als.server.workspace.command._
import org.mulesoft.amfintegration.amfconfiguration.EditorConfiguration
import org.mulesoft.amfintegration.relationships.{AliasInfo, RelationshipLink}
import org.mulesoft.lsp.configuration.WorkspaceFolder
import org.mulesoft.lsp.feature.link.DocumentLink
import org.mulesoft.lsp.feature.telemetry.TelemetryProvider
import org.mulesoft.lsp.workspace.{DidChangeWatchedFilesParams, DidChangeWorkspaceFoldersParams, ExecuteCommandParams}

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class WorkspaceManager protected (
    val environmentProvider: EnvironmentProvider,
    telemetryProvider: TelemetryProvider,
    val editorConfiguration: EditorConfiguration,
    projectConfigurationProvider: ProjectConfigurationProvider,
    val allSubscribers: List[WorkspaceContentListener[_]],
    override val dependencies: List[AccessUnits[CompilableUnit]],
    configurationProvider: ConfigurationProvider
) extends TextListener
    with UnitWorkspaceManager
    with UnitsManager[CompilableUnit, WorkspaceContentListener[_]]
    with AlsWorkspaceService {

  implicit val platform: Platform = environmentProvider.platform

  override def subscribers(): List[WorkspaceContentListener[_]] =
    allSubscribers.filter(_.isActive)

  private val workspaces =
    new WorkspaceList(
      environmentProvider,
      projectConfigurationProvider,
      editorConfiguration,
      telemetryProvider,
      subscribers,
      configurationProvider
    )
  def allWorkspaces(): Seq[WorkspaceContentManager] = workspaces.allWorkspaces()

  def getWorkspace(uri: String): Future[WorkspaceContentManager] =
    workspaces.findWorkspace(uri)

  override def getUnit(uri: String, uuid: String): Future[CompilableUnit] =
    getWorkspace(uri.toAmfUri).flatMap(ws => ws.getUnit(uri.toAmfUri))

  override def getLastUnit(uri: String, uuid: String): Future[CompilableUnit] =
    getUnit(uri.toAmfUri, uuid).flatMap(cu => if (cu.isDirty) getLastCU(cu, uri, uuid) else Future.successful(cu))

  private def getLastCU(cu: CompilableUnit, uri: String, uuid: String) =
    cu.getLast.flatMap {
      case newCu if newCu.isDirty => getLastUnit(uri, uuid)
      case newCu                  => Future.successful(newCu)
    }

  override def notify(uri: String, kind: NotificationKind): Future[Unit] = getWorkspace(uri.toAmfUri).flatMap {
    manager =>
      manager.stage(uri.toAmfUri, kind)
  }

  override def didChangeWatchedFiles(params: DidChangeWatchedFilesParams): Future[Unit] = {
    params.changes.foreach { f =>
      Logger.debug(s"${f.`type`} : ${f.uri}", "WorkspaceManager", "didChangeWatchedFiles")
      f.`type` match {
        case org.mulesoft.lsp.workspace.FileChangeType.Created => // nothing
        case org.mulesoft.lsp.workspace.FileChangeType.Changed => environmentProvider.filesInMemory
        case org.mulesoft.lsp.workspace.FileChangeType.Deleted => ???
      }
    }
    Future
      .sequence(params.changes.map(c => getWorkspace(c.uri)))
      .map(l => l.distinct)
      .map(l => l.foreach(wcm => wcm.stage(wcm.folderUri, CHANGE_CONFIG)))
  }

  override def executeCommand(params: ExecuteCommandParams): Future[AnyRef] =
    commandExecutors.get(params.command) match {
      case Some(exe) =>
        exe.runCommand(params)
      case _ =>
        Logger.error(s"Command [${params.command}] not recognized", "WorkspaceManager", "executeCommand")
        Future.successful(Unit) // future failed?
    }

  private val commandExecutors: Map[String, CommandExecutor[_, _]] = Map(
    Commands.DID_FOCUS_CHANGE_COMMAND -> new DidFocusCommandExecutor(this),
    Commands.DID_CHANGE_CONFIGURATION -> new DidChangeConfigurationCommandExecutor(this),
    Commands.INDEX_DIALECT            -> new IndexDialectCommandExecutor(this)
  )

  override def getProjectRootOf(uri: String): Future[Option[String]] =
    getWorkspace(uri).flatMap(_.getRootFolderFor(uri))

  override def initialize(workspaceFolders: List[WorkspaceFolder]): Future[Unit] = {
    val newWorkspaces = extractCleanURIs(workspaceFolders)
    workspaces
      .initialize(newWorkspaces)
      .map { _ => // Drop all old workspaces
        dependencies.foreach(d => d.withUnitAccessor(this))
      }
  }

  private def extractCleanURIs(workspaceFolders: List[WorkspaceFolder]) =
    workspaceFolders.flatMap(_.uri).sortBy(_.length).distinct

  override def didChangeWorkspaceFolders(params: DidChangeWorkspaceFoldersParams): Future[Unit] =
    changeWorkspaceFolders(params)

  def changeWorkspaceFolders(params: DidChangeWorkspaceFoldersParams): Future[Unit] =
    workspaces.changeWorkspaces(params.event.added.flatMap(_.uri), params.event.deleted.flatMap(_.uri))

  override def getWorkspaceFolders: Seq[String] = workspaces.allWorkspaces().map(_.folderUri)

  override def getDocumentLinks(uri: String, uuid: String): Future[Seq[DocumentLink]] =
    getWorkspace(uri.toAmfUri).flatMap(_.getRelationships(uri.toAmfUri)).map(_.getDocumentLinks(uri.toAmfUri))

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

class WorkspaceList(
    environmentProvider: EnvironmentProvider,
    projectConfigurationProvider: ProjectConfigurationProvider,
    editorConfiguration: EditorConfiguration,
    telemetryProvider: TelemetryProvider,
    subscribers: () => List[WorkspaceContentListener[_]],
    configurationProvider: ConfigurationProvider
) {

  private def buListenerSubscribers: List[BaseUnitListener] =
    subscribers().collect({ case buL: BaseUnitListener => buL })

  private val workspaces: mutable.Set[WorkspaceContentManager] = new mutable.HashSet()

  private var defaultWorkspace: Future[WorkspaceContentManager] =
    buildDefaultWorkspaceManager()

  def buildDefaultWorkspaceManager(): Future[WorkspaceContentManager] = {
    Logger.debug(s"created default WorkspaceContentManager", "WorkspaceList", "buildWorkspaceAt")
    WorkspaceContentManager(
      "",
      environmentProvider,
      telemetryProvider,
      subscribers,
      buildConfigurationAdapter("", IgnoreProjectConfigurationAdapter),
      configurationProvider.getHotReloadDialects
    )
  }

  private def resetDefaultWorkspace(): Unit = defaultWorkspace = buildDefaultWorkspaceManager()

  def removeWorkspace(uri: String): Future[Unit] =
    changeWorkspaces(List.empty, List(uri))

  def changeWorkspaces(added: List[String], deleted: List[String]): Future[Unit] = synchronized {
    Future.sequence(workspaces.map(_.initialized)).flatMap { _ =>
      Logger.debug(s"Changing workspaces, added: $added, deleted: $deleted", "WorkspaceList", "changeWorkspace")
      val newWorkspaces = added.filterNot(uri => workspaces.exists(_.folderUri == uri)).map(getOrCreateWorkspaceAt)
      val oldWorkspaces = workspaces.filter(wcm => deleted.contains(wcm.folderUri)) ++
        workspaces.collect({
          case wcm: WorkspaceContentManager if added.exists(uri => wcm.folderUri.startsWith(uri)) => wcm
        })
      Future
        .sequence(oldWorkspaces.map(_.shutdown()))
        .flatMap(_ =>
          Future
            .sequence(newWorkspaces)
            .map { nw =>
              workspaces --= oldWorkspaces
              workspaces ++= nw
            }
        )

    }
  }

  private def getOrCreateWorkspaceAt(uri: String): Future[WorkspaceContentManager] =
    Future
      .sequence(workspaces.map(_.initialized))
      .flatMap(_ =>
        workspaces
          .find(w => uri.startsWith(w.folderUri)) // if there is an existing WS containing the new one, do not create it
          .map(Future.successful)
          .getOrElse(buildWorkspaceAt(uri))
      )

  private def buildWorkspaceAt(uri: String): Future[WorkspaceContentManager] = {
    val applicableFiles = environmentProvider.openedFiles.filter(_.startsWith(uri))
    for {
      wcm <- WorkspaceContentManager(
        uri,
        environmentProvider,
        telemetryProvider,
        subscribers,
        buildConfigurationAdapter(uri, projectConfigurationProvider),
        configurationProvider.getHotReloadDialects
      )
      _ <- Future.sequence(applicableFiles.map(wcm.stage(_, OPEN_FILE)))
    } yield {
      Logger.debug(s"created WorkspaceContentManager for $uri", "WorkspaceList", "buildWorkspaceAt")
      wcm
    }
  }

  // TODO: move to an object
  private def buildConfigurationAdapter(
      folder: String,
      pcp: ProjectConfigurationProvider
  ): ProjectConfigurationAdapter =
    new ProjectConfigurationAdapter(
      folder,
      pcp,
      editorConfiguration,
      environmentProvider,
      buListenerSubscribers
    )

  def findWorkspace(uri: String): Future[WorkspaceContentManager] =
    for {
      _ <- defaultWorkspace.flatMap(_.initialized)
      _ <- Future.sequence(workspaces.map(_.initialized))
      wcmCandidate <- Future
        .sequence(workspaces.map(ws => ws.containsFile(uri).map((ws, _))))
        .map(set => set.find(t => t._2).map(_._1))
      wcm <- wcmCandidate.map(Future(_)).getOrElse {
        Logger.debug(s"Getting default workspace ($uri)", "WorkspaceList", "findWorkspace")
        defaultWorkspace
      }
    } yield wcm

  def clear(): Future[Unit] =
    Future
      .sequence(workspaces.map(w => removeWorkspace(w.folderUri)))
      .map(_ => {})

  def initialize(workspaces: List[String]): Future[Unit] = {
    for {
      _ <- this.clear()
      _ <- this.changeWorkspaces(workspaces, List())
    } yield {
      resetDefaultWorkspace()
    }
  }

  def allWorkspaces(): Seq[WorkspaceContentManager] = workspaces.toSeq
}

object WorkspaceManager {
  def apply(
      environmentProvider: EnvironmentProvider,
      telemetryProvider: TelemetryProvider,
      editorConfiguration: EditorConfiguration,
      projectConfigurationProvider: ProjectConfigurationProvider,
      allSubscribers: List[WorkspaceContentListener[_]],
      dependencies: List[AccessUnits[CompilableUnit]],
      configurationProvider: ConfigurationProvider
  ): WorkspaceManager = {
    val wm = new WorkspaceManager(
      environmentProvider,
      telemetryProvider,
      editorConfiguration,
      projectConfigurationProvider,
      allSubscribers,
      dependencies,
      configurationProvider
    )
    wm.dependencies.foreach(d => d.withUnitAccessor(wm))
    wm
  }
}
