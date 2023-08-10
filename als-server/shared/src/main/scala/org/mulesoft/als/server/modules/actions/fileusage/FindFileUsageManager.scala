package org.mulesoft.als.server.modules.actions.fileusage

import org.mulesoft.als.server.RequestModule
import org.mulesoft.als.server.feature.fileusage.{FileUsageClientCapabilities, FileUsageConfigType, FileUsageOptions}
import org.mulesoft.als.logger.Logger
import org.mulesoft.als.server.modules.actions.fileusage.filecontents.FileContentsHandler
import org.mulesoft.als.server.workspace.WorkspaceManager
import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.feature.TelemeteredRequestHandler
import org.mulesoft.lsp.feature.telemetry.TelemetryProvider

import scala.concurrent.Future

class FindFileUsageManager(
    val workspace: WorkspaceManager,
    private val telemetryProvider: TelemetryProvider
) extends RequestModule[FileUsageClientCapabilities, FileUsageOptions] {
  private var enabled: Boolean = true

  private var conf: Option[FileUsageClientCapabilities] = None

  override val `type`: ConfigType[FileUsageClientCapabilities, FileUsageOptions] =
    FileUsageConfigType

  override val getRequestHandlers: Seq[TelemeteredRequestHandler[_, _]] = Seq(
    new FileUsageHandler(telemetryProvider, workspace),
    new FileContentsHandler(telemetryProvider, workspace)
  )

  override def applyConfig(config: Option[FileUsageClientCapabilities]): FileUsageOptions = {
    config.foreach(c => enabled = c.fileUsageSupport)
    conf = config
    FileUsageOptions(true)
  }

  override def initialize(): Future[Unit] =
    Future.successful()
}
