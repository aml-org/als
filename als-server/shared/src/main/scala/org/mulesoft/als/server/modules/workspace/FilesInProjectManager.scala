package org.mulesoft.als.server.modules.workspace

import org.mulesoft.als.server.ClientNotifierModule
import org.mulesoft.als.server.client.AlsClientNotifier
import org.mulesoft.als.server.modules.ast.BaseUnitListener
import org.mulesoft.amfmanager.AmfParseResult
import org.mulesoft.als.server.feature.workspace.{
  FilesInProjectClientCapabilities,
  FilesInProjectConfigType,
  FilesInProjectParams,
  FilesInProjectServerOptions
}

import scala.concurrent.Future

class FilesInProjectManager(clientNotifier: AlsClientNotifier[_])
    extends ClientNotifierModule[FilesInProjectClientCapabilities, FilesInProjectServerOptions]
    with BaseUnitListener {

  private var enabled = false

  override val `type`: FilesInProjectConfigType.type = FilesInProjectConfigType

  override def initialize(): Future[Unit] = Future.unit

  /**
    * Called on new AST available
    *
    * @param ast  - AST
    * @param uuid - telemetry UUID
    */
  override def onNewAst(ast: (AmfParseResult, Map[String, DiagnosticsBundle]), uuid: String): Unit =
    clientNotifier.notifyProjectFiles(FilesInProjectParams(ast._2.keySet))

  override def onRemoveFile(uri: String): Unit = {}

  override def applyConfig(config: Option[FilesInProjectClientCapabilities]): FilesInProjectServerOptions = {
    config.foreach(fip => enabled = fip.requiresNotification)
    FilesInProjectServerOptions(true)
  }

  override def closedWorkspace(includedFiles: List[String]): Unit = {}
}
