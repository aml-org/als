package org.mulesoft.als.server.modules.diagnostic

import amf.core.client.common.validation.ProfileNames
import org.mulesoft.als.logger.Logger
import org.mulesoft.als.server.client.platform.ClientNotifier
import org.mulesoft.als.server.modules.ast.{BaseUnitListener, BaseUnitListenerParams}
import org.mulesoft.lsp.feature.telemetry.TelemetryProvider

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ProjectDiagnosticManager(override protected val telemetryProvider: TelemetryProvider,
                               override protected val clientNotifier: ClientNotifier,
                               override protected val logger: Logger,
                               override protected val validationGatherer: ValidationGatherer,
                               override protected val optimizationKind: DiagnosticNotificationsKind)
    extends BaseUnitListener
    with DiagnosticManager {
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

  override def onNewAst(ast: BaseUnitListenerParams, uuid: String): Future[Unit] = Future {
    if (ast.tree) {
      val uri                    = ast.parseResult.location
      val projectErrors          = ast.parseResult.context.state.projectState.projectErrors
      val locations: Set[String] = projectErrors.flatMap(_.location).toSet
      updateWorkspaceFiles(ast.workspace, locations)
      validationGatherer.indexNewReport(
        ErrorsWithTree(uri, projectErrors.map(new AlsValidationResult(_)), Some(locations ++ Set(uri))),
        managerName,
        uuid
      )
    }
  }

  override def onRemoveFile(uri: String): Unit = {}
}
