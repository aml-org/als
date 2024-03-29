package org.mulesoft.als.server.modules.workspace

import org.mulesoft.als.configuration.ProjectConfiguration
import org.mulesoft.als.logger.Logger
import org.mulesoft.als.server.modules.ast.{BaseUnitListener, BaseUnitListenerParams}
import org.mulesoft.als.server.textsync.EnvironmentProvider
import org.mulesoft.als.server.workspace.ProjectConfigurationProvider
import org.mulesoft.amfintegration.amfconfiguration._

import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ProjectConfigurationAdapter(
    val folder: String,
    private val projectConfigurationProvider: ProjectConfigurationProvider,
    editorConfiguration: EditorConfiguration,
    val environmentProvider: EnvironmentProvider,
    subscribers: List[BaseUnitListener]
) {

  var repository: Option[WorkspaceParserRepository] = None

  def withRepository(workspaceRepository: WorkspaceParserRepository): ProjectConfigurationAdapter = {
    repository = Some(workspaceRepository)
    this
  }

  private val emptyProject: ProjectConfigurationState = EmptyProjectConfigurationState(folder)

  def projectConfigurationState: Future[ProjectConfigurationState] =
    projectConfigurationProvider.getProjectInfo(folder).getOrElse(Future.successful(emptyProject))

  def getConfigurationState: Future[ALSConfigurationState] = {
    for {
      editorState  <- editorConfiguration.getState
      projectState <- projectConfigurationState
    } yield ALSConfigurationState(editorState, projectState, Some(environmentProvider.getResourceLoader))
  }

  def mainFile: Future[Option[String]] = projectConfigurationProvider.getMainFile(folder) match {
    case Some(value) => value.map(Some(_))
    case _           => Future.successful(None)
  }

  def rootFolder: Future[Option[String]] = projectConfigurationProvider.getProjectRoot(folder) match {
    case Some(value) => value.map(Some(_))
    case _           => Future.successful(None)
  }

  def getProjectConfiguration: Future[ProjectConfiguration] =
    projectConfigurationProvider
      .getProjectInfo(folder)
      .getOrElse(Future(emptyProject))
      .map(_.config)

  def newProjectConfiguration(projectConfiguration: ProjectConfiguration): Future[ProjectConfigurationState] = {
    Logger.debug(
      s"New configuration for $folder: $projectConfiguration",
      "ProjectConfigurationAdapter",
      "newProjectConfiguration"
    )
    projectConfigurationProvider
      .newProjectConfiguration(projectConfiguration)
      .flatMap(_ => notifyUnits())
  }

  def notifyUnits(): Future[ProjectConfigurationState] = {
    for {
      s <- getConfigurationState
    } yield {
      Logger.debug(
        "Notifying subscribers of dependencies parsing results",
        "ProjectConfigurationAdapter",
        "notifyUnits"
      )
      val subs = subscribers.filter(_.isActive)
      s.projectState.results
        .map(s.toResult("", _))
        .foreach(r => {
          repository.foreach(_.updateUnit(r))
          val p =
            BaseUnitListenerParams(r, Map.empty, tree = false, folder, isDependency = true)
          subs.foreach(_.onNewAst(p, UUID.randomUUID().toString))
        })
      s.projectState
    }
  }

  /** Notify the project configuration provider of the new tree in order to collect the cacheable units
    * @param tree
    *   the new tree
    */
  def newTree(tree: MainFileTree): Future[Unit] =
    projectConfigurationProvider.afterNewTree(folder, tree)

}
