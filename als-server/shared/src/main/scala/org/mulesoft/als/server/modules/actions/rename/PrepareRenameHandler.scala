package org.mulesoft.als.server.modules.actions.rename

import amf.core.model.document.Document
import org.mulesoft.als.common.YamlUtils
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.als.server.workspace.WorkspaceManager
import org.mulesoft.amfintegration.AmfImplicits.{AmfAnnotationsImp, AmfObjectImp, BaseUnitImp}
import org.mulesoft.lsp.feature.TelemeteredRequestHandler
import org.mulesoft.lsp.feature.common.Range
import org.mulesoft.lsp.feature.rename.{PrepareRenameParams, PrepareRenameRequestType, PrepareRenameResult}
import org.mulesoft.lsp.feature.telemetry.MessageTypes.MessageTypes
import org.mulesoft.lsp.feature.telemetry.{MessageTypes, TelemetryProvider}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class PrepareRenameHandler(telemetryProvider: TelemetryProvider, workspace: WorkspaceManager)
    extends TelemeteredRequestHandler[PrepareRenameParams, Option[Either[Range, PrepareRenameResult]]]
    with RenameTools {
  override def `type`: PrepareRenameRequestType.type = PrepareRenameRequestType

  override def task(params: PrepareRenameParams): Future[Option[Either[Range, PrepareRenameResult]]] =
    // check if enabled?
    prepareRename(params.textDocument.uri, Position(params.position.line, params.position.character), uuid(params))

  override protected def telemetry: TelemetryProvider = telemetryProvider

  override protected def code(params: PrepareRenameParams): String = "PrepareRename"

  override protected def beginType(params: PrepareRenameParams): MessageTypes = MessageTypes.BEGIN_PREP_RENAME

  override protected def endType(params: PrepareRenameParams): MessageTypes = MessageTypes.END_PREP_RENAME

  override protected def msg(params: PrepareRenameParams): String =
    s"Request for prepare renaming on ${params.textDocument.uri}"

  override protected def uri(params: PrepareRenameParams): String = params.textDocument.uri

  def prepareRename(uri: String,
                    position: Position,
                    uuid: String): Future[Option[Either[Range, PrepareRenameResult]]] =
    workspace
      .getLastUnit(uri, uuid)
      .flatMap(_.getLast)
      .map(bu => {
        if (isDeclarableKey(bu, position, uri))
          bu.unit.objWithAST
            .flatMap(_.annotations.ast())
            .map(YamlUtils.getNodeByPosition(_, position.toAmfPosition))
            .map(p => LspRangeConverter.toLspRange(PositionRange(p.range)))
            .map(r => Left(r))
        else None
      })
}
