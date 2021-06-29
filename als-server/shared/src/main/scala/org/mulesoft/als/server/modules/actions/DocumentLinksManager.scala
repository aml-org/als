package org.mulesoft.als.server.modules.actions

import org.mulesoft.als.server.RequestModule
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.workspace.UnitWorkspaceManager
import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.feature.TelemeteredRequestHandler
import org.mulesoft.lsp.feature.link._
import org.mulesoft.lsp.feature.telemetry.MessageTypes.MessageTypes
import org.mulesoft.lsp.feature.telemetry.{MessageTypes, TelemetryProvider}

import scala.concurrent.Future

class DocumentLinksManager(val workspaceManager: UnitWorkspaceManager,
                           private val telemetryProvider: TelemetryProvider,
                           private val logger: Logger)
    extends RequestModule[DocumentLinkClientCapabilities, DocumentLinkOptions] {

  override val `type`: ConfigType[DocumentLinkClientCapabilities, DocumentLinkOptions] =
    DocumentLinkConfigType

  override val getRequestHandlers: Seq[TelemeteredRequestHandler[_, _]] = Seq(
    new TelemeteredRequestHandler[DocumentLinkParams, Seq[DocumentLink]] {
      override def `type`: DocumentLinkRequestType.type =
        DocumentLinkRequestType

      override def task(params: DocumentLinkParams): Future[Seq[DocumentLink]] =
        documentLinks(params.textDocument.uri, uuid(params))

      override protected def telemetry: TelemetryProvider                        = telemetryProvider
      override protected def code(params: DocumentLinkParams): String            = "DocumentLink"
      override protected def beginType(params: DocumentLinkParams): MessageTypes = MessageTypes.BEGIN_DOCUMENT_LINK
      override protected def endType(params: DocumentLinkParams): MessageTypes   = MessageTypes.END_DOCUMENT_LINK
      override protected def msg(params: DocumentLinkParams): String =
        s"request for document links on ${params.textDocument.uri}"
      override protected def uri(params: DocumentLinkParams): String = params.textDocument.uri

      /**
        * If Some(_), this will be sent as a response as a default for a managed exception
        */
      override protected val empty: Option[Seq[DocumentLink]] = Some(Seq())
    }
  )

  override def applyConfig(config: Option[DocumentLinkClientCapabilities]): DocumentLinkOptions =
    DocumentLinkOptions(config.flatMap(_.dynamicRegistration))

  val onDocumentLinks: (String, String) => Future[Seq[DocumentLink]] = documentLinks

  def documentLinks(uri: String, uuid: String): Future[Seq[DocumentLink]] =
    workspaceManager.getDocumentLinks(uri, uuid)

  override def initialize(): Future[Unit] = Future.successful()

}
