package org.mulesoft.als.server.modules.actions.rename

import org.mulesoft.als.actions.rename.FindRenameLocations
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.server.workspace.WorkspaceManager
import org.mulesoft.lsp.edit.WorkspaceEdit
import org.mulesoft.lsp.feature.TelemeteredRequestHandler
import org.mulesoft.lsp.feature.rename.{RenameParams, RenameRequestType}
import org.mulesoft.lsp.feature.telemetry.MessageTypes.MessageTypes
import org.mulesoft.lsp.feature.telemetry.{MessageTypes, TelemetryProvider}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RenameHandler(telemetryProvider: TelemetryProvider, workspace: WorkspaceManager)
    extends TelemeteredRequestHandler[RenameParams, WorkspaceEdit] {
  override def `type`: RenameRequestType.type = RenameRequestType

  override def task(params: RenameParams): Future[WorkspaceEdit] =
    rename(params.textDocument.uri,
           Position(params.position.line, params.position.character),
           params.newName,
           uuid(params))

  override protected def telemetry: TelemetryProvider = telemetryProvider

  override protected def code(params: RenameParams): String = "RenameManager"

  override protected def beginType(params: RenameParams): MessageTypes = MessageTypes.BEGIN_RENAME

  override protected def endType(params: RenameParams): MessageTypes = MessageTypes.END_RENAME

  override protected def msg(params: RenameParams): String = s"Request for renaming on ${params.textDocument.uri}"

  override protected def uri(params: RenameParams): String = params.textDocument.uri

  def rename(uri: String, position: Position, newName: String, uuid: String): Future[WorkspaceEdit] =
    workspace
      .getLastUnit(uri, uuid)
      .flatMap(_.getLast)
      .flatMap(bu => {
        FindRenameLocations.changeDeclaredName(uri, position, newName, workspace.getRelationships(uri, uuid), bu.unit)
      })
}
