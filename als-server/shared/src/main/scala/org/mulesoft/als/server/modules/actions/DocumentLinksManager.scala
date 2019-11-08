package org.mulesoft.als.server.modules.actions

import amf.core.remote.Platform
import org.mulesoft.als.actions.links.FindLinks
import org.mulesoft.als.server.RequestModule
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.workspace.WorkspaceContentCollection
import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.feature.RequestHandler
import org.mulesoft.lsp.feature.link._
import org.mulesoft.lsp.feature.telemetry.TelemetryProvider

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DocumentLinksManager(val workspaceManager: WorkspaceContentCollection,
                           private val telemetryProvider: TelemetryProvider,
                           private val logger: Logger,
                           private val platform: Platform)
    extends RequestModule[DocumentLinkClientCapabilities, DocumentLinkOptions] {

  override val `type`: ConfigType[DocumentLinkClientCapabilities, DocumentLinkOptions] =
    DocumentLinkConfigType

  override val getRequestHandlers: Seq[RequestHandler[_, _]] = Seq(
    new RequestHandler[DocumentLinkParams, Seq[DocumentLink]] {
      override def `type`: DocumentLinkRequestType.type = DocumentLinkRequestType

      override def apply(params: DocumentLinkParams): Future[Seq[DocumentLink]] =
        documentLinks(params.textDocument.uri)
    }
  )

  override def applyConfig(config: Option[DocumentLinkClientCapabilities]): DocumentLinkOptions =
    DocumentLinkOptions(config.flatMap(_.dynamicRegistration))

  val onDocumentLinks: String => Future[Seq[DocumentLink]] = documentLinks

  def documentLinks(str: String): Future[Seq[DocumentLink]] =
    workspaceManager
      .getUnit(str)
      .map(bu => { FindLinks.getLinks(bu.unit, platform) })

  override def initialize(): Future[Unit] = Future.successful()

}
