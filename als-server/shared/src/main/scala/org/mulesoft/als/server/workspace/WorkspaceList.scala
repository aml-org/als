package org.mulesoft.als.server.workspace

import org.mulesoft.als.logger.Logger
import org.mulesoft.als.server.modules.ast.{BaseUnitListener, NotificationKind, OPEN_FILE, WorkspaceContentListener}
import org.mulesoft.als.server.modules.configuration.ConfigurationProvider
import org.mulesoft.als.server.modules.workspace.{
  CompilableUnit,
  DummyWorkspaceFolderManager,
  ParsedUnit,
  ProjectConfigurationAdapter,
  WorkspaceContentManager,
  WorkspaceFolderManager
}
import org.mulesoft.als.server.textsync.EnvironmentProvider
import org.mulesoft.amfintegration.amfconfiguration.EditorConfiguration

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class WorkspaceList(
    environmentProvider: EnvironmentProvider,
    projectConfigurationProvider: ProjectConfigurationProvider,
    editorConfiguration: EditorConfiguration,
    subscribers: () => List[WorkspaceContentListener[_]],
    configurationProvider: ConfigurationProvider
) {

  private def switchWorkspace(folderUri: String): Unit =
    removeWorkspace(folderUri).foreach { _ => workspaces += new DummyWorkspaceFolderManager(folderUri) }

  private def buListenerSubscribers: List[BaseUnitListener] =
    subscribers().collect({ case buL: BaseUnitListener => buL })

  private val workspaces: mutable.Set[WorkspaceFolderManager] = new mutable.HashSet()

  private var defaultWorkspace: Future[WorkspaceContentManager] =
    buildDefaultWorkspaceManager()

  def buildDefaultWorkspaceManager(): Future[WorkspaceContentManager] = {
    Logger.debug(s"Default WorkspaceContentManager created", "WorkspaceList", "buildWorkspaceAt")
    WorkspaceContentManager(
      "",
      environmentProvider,
      subscribers,
      buildConfigurationAdapter("", IgnoreProjectConfigurationAdapter),
      switchWorkspace,
      configurationProvider.getHotReloadDialects,
      configurationProvider.getMaxFileSize
    )
  }

  private def resetDefaultWorkspace(): Unit = defaultWorkspace = buildDefaultWorkspaceManager()

  private def removeWorkspace(uri: String): Future[Unit] =
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

  private def getOrCreateWorkspaceAt(uri: String): Future[WorkspaceFolderManager] =
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
        subscribers,
        buildConfigurationAdapter(uri, projectConfigurationProvider),
        switchWorkspace,
        configurationProvider.getHotReloadDialects,
        configurationProvider.getMaxFileSize
      )
      _ <- Future.sequence(applicableFiles.map(wcm.stage(_, OPEN_FILE)))
    } yield {
      Logger.debug(s"WorkspaceContentManager created for $uri", "WorkspaceList", "buildWorkspaceAt")
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

  def findWorkspace(uri: String): Future[WorkspaceFolderManager] =
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

  def allWorkspaces(): Seq[WorkspaceFolderManager] = workspaces.toSeq
}
