package org.mulesoft.als.server.modules.actions

import amf.core.remote.Platform
import org.mulesoft.als.server.RequestModule
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.workspace.UnitWorkspaceManager
import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.feature.TelemeteredRequestHandler
import org.mulesoft.lsp.feature.common.Position
import org.mulesoft.lsp.feature.highlight._
import org.mulesoft.lsp.feature.telemetry.MessageTypes.MessageTypes
import org.mulesoft.lsp.feature.telemetry.{MessageTypes, TelemetryProvider}

import scala.concurrent.Future

class DocumentHighlightManager(val workspaceManager: UnitWorkspaceManager,
                               private val telemetryProvider: TelemetryProvider,
                               platform: Platform,
                               private val logger: Logger)
    extends RequestModule[DocumentHighlightCapabilities, Either[Boolean, DocumentHighlightOptions]] {

  override val `type`: ConfigType[DocumentHighlightCapabilities, Either[Boolean, DocumentHighlightOptions]] =
    DocumentHighlightConfigType

  override val getRequestHandlers: Seq[TelemeteredRequestHandler[_, _]] = Seq(
    new TelemeteredRequestHandler[DocumentHighlightParams, Seq[DocumentHighlight]] {
      override def `type`: DocumentHighlightRequestType.type =
        DocumentHighlightRequestType

      override def task(params: DocumentHighlightParams): Future[Seq[DocumentHighlight]] =
        documentHighlights(params.textDocument.uri, uuid(params))

      override protected def telemetry: TelemetryProvider                  = telemetryProvider
      override protected def code(params: DocumentHighlightParams): String = "DocumentHighlight"
      override protected def beginType(params: DocumentHighlightParams): MessageTypes =
        MessageTypes.BEGIN_DOCUMENT_HIGHLIGHT
      override protected def endType(params: DocumentHighlightParams): MessageTypes =
        MessageTypes.END_DOCUMENT_HIGHLIGHT
      override protected def msg(params: DocumentHighlightParams): String =
        s"request for document links on ${params.textDocument.uri}"
      override protected def uri(params: DocumentHighlightParams): String = params.textDocument.uri
    }
  )

  override def applyConfig(config: Option[DocumentHighlightCapabilities]): Either[Boolean, DocumentHighlightOptions] =
//    Left(config.flatMap(_.dynamicRegistration).getOrElse(false)) // DocumentHighlightOptions not in LSP4J
    Left(true)

  val onDocumentHighlights: (String, String) => Future[Seq[DocumentHighlight]] = documentHighlights

  def documentHighlights(uri: String, uuid: String): Future[Seq[DocumentHighlight]] =
    Future.successful(Seq())

  override def initialize(): Future[Unit] = Future.successful()
}
