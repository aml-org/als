package org.mulesoft.als.server.workspace

import amf.core.internal.remote.Platform
import org.mulesoft.als.common.URIImplicits._
import org.mulesoft.als.logger.Logger
import org.mulesoft.als.server.AlsWorkspaceService
import org.mulesoft.als.server.modules.ast._
import org.mulesoft.als.server.modules.configuration.ConfigurationProvider
import org.mulesoft.als.server.modules.workspace.{
  CompilableUnit,
  UnitNotFoundException,
  WorkspaceContentManager,
  WorkspaceFolderManager
}
import org.mulesoft.als.server.textsync.EnvironmentProvider
import org.mulesoft.als.server.workspace.command._
import org.mulesoft.amfintegration.amfconfiguration.EditorConfiguration
import org.mulesoft.amfintegration.relationships.{AliasInfo, RelationshipLink}
import org.mulesoft.lsp.configuration.WorkspaceFolder
import org.mulesoft.lsp.feature.link.DocumentLink
import org.mulesoft.lsp.workspace.{DidChangeWatchedFilesParams, DidChangeWorkspaceFoldersParams, ExecuteCommandParams}

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class WorkspaceManager protected (
    val environmentProvider: EnvironmentProvider,
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
      subscribers,
      configurationProvider
    )
  def allWorkspaces(): Seq[WorkspaceFolderManager] = workspaces.allWorkspaces()

  def getWorkspace(uri: String): Future[WorkspaceFolderManager] =
    workspaces.findWorkspace(uri)

  override def getUnit(uri: String, uuid: String): Future[CompilableUnit] =
    getWorkspace(uri.toAmfUri).flatMap {
      case ws: WorkspaceContentManager => ws.getUnit(uri.toAmfUri)
      case _ =>
        Future.failed(UnitNotFoundException(uri, uuid))
    }

  override def getLastUnit(uri: String, uuid: String): Future[CompilableUnit] =
    getUnit(uri.toAmfUri, uuid).flatMap(cu => if (cu.isDirty) getLastCU(cu, uri, uuid) else Future.successful(cu))

  private def getLastCU(cu: CompilableUnit, uri: String, uuid: String) =
    cu.getLast.flatMap {
      case newCu if newCu.isDirty => getLastUnit(uri, uuid)
      case newCu                  => Future.successful(newCu)
    }

  override def notify(uri: String, kind: NotificationKind): Future[Unit] = getWorkspace(uri.toAmfUri).flatMap {
    case manager: WorkspaceContentManager => manager.stage(uri.toAmfUri, kind)
    case _                                => Future.successful()
  }

  override def didChangeWatchedFiles(params: DidChangeWatchedFilesParams): Future[Unit] = {
    params.changes.foreach { f =>
      Logger.debug(s"${f.`type`} : ${f.uri}", "WorkspaceManager", "didChangeWatchedFiles")
    }
    Future
      .sequence(params.changes.map(c => getWorkspace(c.uri)))
      .map(l => l.distinct)
      .map(l => l.foreach { case wcm: WorkspaceContentManager => wcm.stage(wcm.folderUri, CHANGE_CONFIG) })
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
    getWorkspace(uri).flatMap {
      case wcm: WorkspaceContentManager => wcm.getRootFolderFor(uri)
      case other                        => Future.successful(Option(other.folderUri))
    }

  override def initialize(workspaceFolders: List[WorkspaceFolder]): Future[Unit] = {
    val newWorkspaces = extractCleanURIs(workspaceFolders)
    val newProjects = Future.sequence(newWorkspaces.map(projectConfigurationProvider.getProjectsFromFolder)).map(_.flatten)
    newProjects.flatMap(projects =>
      workspaces
        .initialize(projects)
        .map { _ => // Drop all old workspaces
          dependencies.foreach(d => d.withUnitAccessor(this))
        }
    )
  }

  private def extractCleanURIs(workspaceFolders: List[WorkspaceFolder]) =
    workspaceFolders.flatMap(_.uri).sortBy(_.length).distinct

  override def didChangeWorkspaceFolders(params: DidChangeWorkspaceFoldersParams): Future[Unit] =
    changeWorkspaceFolders(params)

  def changeWorkspaceFolders(params: DidChangeWorkspaceFoldersParams): Future[Unit] =
    workspaces.changeWorkspaces(params.event.added.flatMap(_.uri), params.event.deleted.flatMap(_.uri))

  override def getWorkspaceFolders: Seq[String] = workspaces.allWorkspaces().map(_.folderUri)

  override def getDocumentLinks(uri: String, uuid: String): Future[Seq[DocumentLink]] =
    getWorkspace(uri.toAmfUri).flatMap {
      case wcm: WorkspaceContentManager => wcm.getRelationships(uri.toAmfUri).map(_.getDocumentLinks(uri.toAmfUri))
      case _                            => Future.successful(Seq.empty)
    }

  override def getAllDocumentLinks(uri: String, uuid: String): Future[Map[String, Seq[DocumentLink]]] =
    getWorkspace(uri).flatMap {
      case wcm: WorkspaceContentManager =>
        for {
          mainFileUri <- wcm.mainFileUri
          _           <- mainFileUri.map(getLastUnit(_, uuid)).getOrElse(Future.unit)
          allLinks <- mainFileUri
            .map(wcm.getRelationships(_).map(_.getAllDocumentLinks))
            .getOrElse(Future.successful(Map[String, Seq[DocumentLink]]()))
        } yield allLinks
      case _ => Future.successful(Map.empty)
    }

  override def getAliases(uri: String, uuid: String): Future[Seq[AliasInfo]] =
    getLastUnit(uri, uuid)
      .flatMap(_ => getWorkspace(uri))
      .flatMap { case wcm: WorkspaceContentManager =>
        wcm.getRelationships(uri)
      }
      .map(_.getAliases(uri))

  private def filterDuplicates(links: Seq[RelationshipLink]): Seq[RelationshipLink] = {
    val res = mutable.ListBuffer[RelationshipLink]()
    links.foreach { l =>
      if (!res.exists(_.relationshipIsEqual(l)))
        res += l
    }
    res
  }

  override def getRelationships(uri: String, uuid: String): Future[(CompilableUnit, Seq[RelationshipLink])] =
    for {
      cu        <- getLastUnit(uri, uuid)
      workspace <- getWorkspace(uri)
      relationships <- workspace match {
        case manager: WorkspaceContentManager => manager.getRelationships(uri)
        case _ => Future.failed(UnitNotFoundException(uri, uuid)) // should have failed in getLastUnit
      }
    } yield {
      (cu, filterDuplicates(relationships.getRelationships(uri)))
    }

  override def isInMainTree(uri: String): Future[Boolean] =
    workspaces.findWorkspace(uri.toAmfUri).map {
      case wcm: WorkspaceContentManager => wcm.isInMainTree(uri)
      case _                            => false
    }
}

object WorkspaceManager {
  def apply(
      environmentProvider: EnvironmentProvider,
      editorConfiguration: EditorConfiguration,
      projectConfigurationProvider: ProjectConfigurationProvider,
      allSubscribers: List[WorkspaceContentListener[_]],
      dependencies: List[AccessUnits[CompilableUnit]],
      configurationProvider: ConfigurationProvider
  ): WorkspaceManager = {
    val wm = new WorkspaceManager(
      environmentProvider,
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
