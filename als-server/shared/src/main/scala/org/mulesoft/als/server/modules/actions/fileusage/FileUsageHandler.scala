package org.mulesoft.als.server.modules.actions.fileusage

import org.mulesoft.als.actions.fileusage.FindFileUsages
import org.mulesoft.als.server.feature.fileusage.FileUsageRequestType
import org.mulesoft.als.server.workspace.WorkspaceManager
import org.mulesoft.lsp.feature.TelemeteredRequestHandler
import org.mulesoft.lsp.feature.common.{Location, TextDocumentIdentifier}
import org.mulesoft.lsp.feature.telemetry.MessageTypes.MessageTypes
import org.mulesoft.lsp.feature.telemetry.{MessageTypes, TelemetryProvider}

import scala.concurrent.Future

class FileUsageHandler(workspace: WorkspaceManager)
    extends TelemeteredRequestHandler[TextDocumentIdentifier, Seq[Location]] {
  override def `type`: FileUsageRequestType.type = FileUsageRequestType

  override def task(params: TextDocumentIdentifier): Future[Seq[Location]] =
    FindFileUsages.getUsages(params.uri, workspace.getAllDocumentLinks(params.uri, uuid(params)))

  override protected def code(params: TextDocumentIdentifier): String = "FileUsageHandler"

  override protected def beginType(params: TextDocumentIdentifier): MessageTypes = MessageTypes.BEGIN_FILE_USAGE

  override protected def endType(params: TextDocumentIdentifier): MessageTypes = MessageTypes.END_FILE_USAGE

  override protected def msg(params: TextDocumentIdentifier): String = s"Request find usage for file: ${params.uri}"

  override protected def uri(params: TextDocumentIdentifier): String = params.uri

  /** If Some(_), this will be sent as a response as a default for a managed exception
    */
  override protected val empty: Option[Seq[Location]] = Some(Seq())
}
