package org.mulesoft.als.server.modules.actions

import amf.core.internal.remote.Platform
import org.mulesoft.als.actions.renamefile.RenameFileAction
import org.mulesoft.als.configuration.AlsConfigurationReader
import org.mulesoft.als.server.RequestModule
import org.mulesoft.als.server.feature.renamefile._
import org.mulesoft.als.logger.Logger
import org.mulesoft.als.server.workspace.WorkspaceManager
import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.feature.common.TextDocumentIdentifier
import org.mulesoft.lsp.feature.telemetry.MessageTypes.MessageTypes
import org.mulesoft.lsp.feature.telemetry.{MessageTypes, TelemetryProvider}
import org.mulesoft.lsp.feature.{RequestType, TelemeteredRequestHandler}

import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RenameFileActionManager(
    val workspace: WorkspaceManager,
    private val telemetryProvider: TelemetryProvider,
    private val logger: Logger,
    private val configReader: AlsConfigurationReader,
    private val platform: Platform
) extends RequestModule[RenameFileActionClientCapabilities, RenameFileActionOptions] {

  private var active = true

  override val `type`: ConfigType[RenameFileActionClientCapabilities, RenameFileActionOptions] =
    RenameFileConfigType

  override def applyConfig(config: Option[RenameFileActionClientCapabilities]): RenameFileActionOptions = {
    active = config.exists(_.enabled)
    RenameFileActionOptions(active)
  }

  override def initialize(): Future[Unit] = Future.successful()

  override def getRequestHandlers: Seq[TelemeteredRequestHandler[_, _]] = Seq(new RenameFileActionRequestHandler())

  class RenameFileActionRequestHandler
      extends TelemeteredRequestHandler[RenameFileActionParams, RenameFileActionResult] {
    override def `type`: RequestType[RenameFileActionParams, RenameFileActionResult] = RenameFileActionRequestType

    override protected def telemetry: TelemetryProvider = telemetryProvider

    override protected def task(params: RenameFileActionParams): Future[RenameFileActionResult] =
      rename(params.oldDocument, params.newDocument)

    override protected def code(params: RenameFileActionParams): String = "RenameFileAction"

    override protected def beginType(params: RenameFileActionParams): MessageTypes =
      MessageTypes.BEGIN_RENAME_FILE_ACTION

    override protected def endType(params: RenameFileActionParams): MessageTypes = MessageTypes.END_RENAME_FILE_ACTION

    override protected def msg(params: RenameFileActionParams): String =
      s"renaming file ${params.oldDocument.uri} to ${params.newDocument.uri}"

    override protected def uri(params: RenameFileActionParams): String = params.oldDocument.uri

    def rename(
        oldDocument: TextDocumentIdentifier,
        newDocument: TextDocumentIdentifier
    ): Future[RenameFileActionResult] = {

      val uuid = UUID.randomUUID().toString
      for {
        links <- workspace.getAllDocumentLinks(oldDocument.uri, uuid)
      } yield {
        logger.debug("got the following document links", "RenameFileActionManager", "rename")
        links.toSeq.foreach { tuple =>
          tuple._2.map(_.target).foreach { target =>
            logger.debug(s"${tuple._1} - $target", "RenameFileActionManager", "rename")
          }
        }

        val edit =
          RenameFileAction
            .renameFileEdits(oldDocument, newDocument, links, platform)
            .toWorkspaceEdit(configReader.supportsDocumentChanges)
        RenameFileActionResult(edit)
      }
    }

    /** If Some(_), this will be sent as a response as a default for a managed exception
      */
    override protected val empty: Option[RenameFileActionResult] = None
  }
}
