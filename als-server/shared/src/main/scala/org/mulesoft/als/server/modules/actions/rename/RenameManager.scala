package org.mulesoft.als.server.modules.actions.rename

import amf.core.internal.remote.Platform
import org.mulesoft.als.configuration.AlsConfigurationReader
import org.mulesoft.als.server.RequestModule
import org.mulesoft.als.logger.Logger
import org.mulesoft.als.server.workspace.WorkspaceManager
import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.feature.TelemeteredRequestHandler
import org.mulesoft.lsp.feature.rename._
import org.mulesoft.lsp.feature.telemetry.TelemetryProvider

import scala.concurrent.Future

class RenameManager(val workspace: WorkspaceManager,
                    private val telemetryProvider: TelemetryProvider,
                    private val logger: Logger,
                    private val configurationReader: AlsConfigurationReader,
                    private val platform: Platform)
    extends RequestModule[RenameClientCapabilities, RenameOptions] {

  private var conf: Option[RenameClientCapabilities] = None

  override val `type`: ConfigType[RenameClientCapabilities, RenameOptions] =
    RenameConfigType

  override val getRequestHandlers: Seq[TelemeteredRequestHandler[_, _]] = Seq(
    new RenameHandler(telemetryProvider, workspace, configurationReader, logger, platform),
    new PrepareRenameHandler(telemetryProvider, workspace)
  )

  override def applyConfig(config: Option[RenameClientCapabilities]): RenameOptions = {
    conf = config
    RenameOptions(Some(true))
  }

  override def initialize(): Future[Unit] =
    Future.successful()
}
