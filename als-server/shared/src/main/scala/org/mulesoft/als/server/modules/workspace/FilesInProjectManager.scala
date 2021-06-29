package org.mulesoft.als.server.modules.workspace

import org.mulesoft.als.server.ClientNotifierModule
import org.mulesoft.als.server.client.AlsClientNotifier
import org.mulesoft.als.server.feature.workspace.{
  FilesInProjectClientCapabilities,
  FilesInProjectConfigType,
  FilesInProjectParams,
  FilesInProjectServerOptions
}
import org.mulesoft.als.server.modules.ast.{BaseUnitListener, BaseUnitListenerParams}

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
  override def onNewAst(ast: BaseUnitListenerParams, uuid: String): Future[Unit] = synchronized {
    Future.successful {
      if (ast.tree) clientNotifier.notifyProjectFiles(FilesInProjectParams(ast.diagnosticsBundle.keySet))
    }
  }

  override def onRemoveFile(uri: String): Unit = {
    /* No action required */
  }

  override def applyConfig(config: Option[FilesInProjectClientCapabilities]): FilesInProjectServerOptions = {
    config.foreach(fip => enabled = fip.requiresNotification)
    FilesInProjectServerOptions(true)
  }
}
