package org.mulesoft.als.server.modules.actions.fileusage.filecontents

import org.mulesoft.als.server.feature.fileusage.filecontents.{FileContentsRequestType, FileContentsResponse}
import org.mulesoft.als.server.workspace.WorkspaceManager
import org.mulesoft.lsp.feature.TelemeteredRequestHandler
import org.mulesoft.lsp.feature.common.TextDocumentIdentifier
import org.mulesoft.lsp.feature.telemetry.MessageTypes.MessageTypes
import org.mulesoft.lsp.feature.telemetry.{MessageTypes, TelemetryProvider}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class FileContentsHandler(workspace: WorkspaceManager)
    extends TelemeteredRequestHandler[TextDocumentIdentifier, FileContentsResponse] {
  override def `type`: FileContentsRequestType.type = FileContentsRequestType

  override def task(params: TextDocumentIdentifier): Future[FileContentsResponse] =
    workspace.getLastUnit(params.uri, uuid(params)).flatMap { _ => // be sure the unit is done being parsed
      workspace
        .getWorkspace(params.uri)
        .map(wcm => wcm.repository.getAllFilesUrisWithContents)
        .map(fs => FileContentsResponse(fs))
    }

  override protected def code(params: TextDocumentIdentifier): String = "FileContentsHandler"

  override protected def beginType(params: TextDocumentIdentifier): MessageTypes = MessageTypes.BEGIN_FILE_CONTENTS

  override protected def endType(params: TextDocumentIdentifier): MessageTypes = MessageTypes.END_FILE_CONTENTS

  override protected def msg(params: TextDocumentIdentifier): String = s"Request find contents for file: ${params.uri}"

  override protected def uri(params: TextDocumentIdentifier): String = params.uri

  /** If Some(_), this will be sent as a response as a default for a managed exception
    */
  override protected val empty: Option[FileContentsResponse] = Some(FileContentsResponse(Map.empty))
}
