package org.mulesoft.als.server.modules.diagnostic

import amf.core.client.common.validation.ProfileNames
import org.mulesoft.als.common.URIImplicits.StringUriImplicits
import org.mulesoft.als.server.client.platform.ClientNotifier
import org.mulesoft.als.server.modules.project.NewConfigurationListener
import org.mulesoft.amfintegration.amfconfiguration.ProjectConfigurationState
import org.mulesoft.amfintegration.platform.AlsPlatformSecrets

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ProjectDiagnosticManager(
    override protected val clientNotifier: ClientNotifier,
    override protected val validationGatherer: ValidationGatherer,
    override protected val optimizationKind: DiagnosticNotificationsKind
) extends NewConfigurationListener
    with DiagnosticManager
    with AlsPlatformSecrets {
  override protected val managerName: DiagnosticManagerKind = ProjectDiagnosticKind

  private val filesByWorkspace: mutable.Map[String, Set[String]] = mutable.Map.empty

  def updateWorkspaceFiles(workspace: String, files: Set[String]): Unit = synchronized {
    val clear = filesByWorkspace.get(workspace).map(prev => prev.diff(files))
    filesByWorkspace.update(workspace, files)
    clear.foreach(s => {
      s.foreach(uri => {
        validationGatherer.removeFile(uri, managerName)
        clientNotifier.notifyDiagnostic(AlsPublishDiagnosticsParams(uri, Nil, ProfileNames.AMF))
      })
    })
  }

  override def onNewAst(ast: ProjectConfigurationState, uuid: String): Future[Unit] = Future(for {
    mainFile <- ast.config.mainFile // todo: apb returning full uri would be nice
  } yield {
    val locations       = ast.projectErrors.flatMap(_.location).toSet
    val folder          = ast.config.folder.toAmfDecodedUri(platform)
    val correctedFolder = if (folder.endsWith("/")) folder else s"$folder/"
    val mainFileUri     = s"$correctedFolder$mainFile".toAmfUri(platform)
    validationGatherer.indexNewReport(
      ErrorsWithTree(
        mainFileUri,
        ast.projectErrors.map(new AlsValidationResult(_)),
        Some(locations ++ Set(mainFileUri))
      ),
      managerName,
      uuid
    )
    updateWorkspaceFiles(ast.config.folder, locations)
  })

  override def onRemoveFile(uri: String): Unit = {}
}
