package org.mulesoft.als.server.modules.actions

import java.util.UUID

import org.mulesoft.als.actions.fileusage.FindFileUsages
import org.mulesoft.als.server.RequestModule
import org.mulesoft.als.server.feature.fileusage.{
  FileUsageClientCapabilities,
  FileUsageConfigType,
  FileUsageOptions,
  FileUsageRequestType
}
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.workspace.WorkspaceManager
import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.feature.RequestHandler
import org.mulesoft.lsp.feature.common.{Location, TextDocumentIdentifier}
import org.mulesoft.lsp.feature.telemetry.TelemetryProvider

import scala.concurrent.Future

class FindFileUsageManager(val workspace: WorkspaceManager,
                           private val telemetryProvider: TelemetryProvider,
                           private val logger: Logger)
    extends RequestModule[FileUsageClientCapabilities, FileUsageOptions] {
  private var enabled: Boolean = true

  private var conf: Option[FileUsageClientCapabilities] = None

  override val `type`: ConfigType[FileUsageClientCapabilities, FileUsageOptions] =
    FileUsageConfigType

  override val getRequestHandlers: Seq[RequestHandler[_, _]] = Seq(
    new RequestHandler[TextDocumentIdentifier, Seq[Location]] {
      override def `type`: FileUsageRequestType.type = FileUsageRequestType

      override def apply(params: TextDocumentIdentifier): Future[Seq[Location]] =
        findFileUsage(params.uri)
    }
  )

  override def applyConfig(config: Option[FileUsageClientCapabilities]): FileUsageOptions = {
    config.foreach(c => enabled = c.fileUsageSupport)
    FileUsageOptions(true)
  }

  private def findFileUsage(uri: String): Future[Seq[Location]] =
    FindFileUsages.getUsages(uri, workspace.getAllDocumentLinks(uri, UUID.randomUUID().toString))

  override def initialize(): Future[Unit] =
    Future.successful()
}
