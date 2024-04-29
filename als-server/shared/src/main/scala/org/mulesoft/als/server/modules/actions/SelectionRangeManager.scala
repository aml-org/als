package org.mulesoft.als.server.modules.actions

import org.mulesoft.als.actions.selection.SelectionRangeFinder
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.als.server.RequestModule
import org.mulesoft.als.server.workspace.WorkspaceManager
import org.mulesoft.amfintegration.AmfImplicits._
import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.configuration.WorkDoneProgressOptions
import org.mulesoft.lsp.feature.TelemeteredRequestHandler
import org.mulesoft.lsp.feature.selectionRange._
import org.mulesoft.lsp.feature.telemetry.MessageTypes.MessageTypes
import org.mulesoft.lsp.feature.telemetry.{MessageTypes, TelemetryProvider}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
class SelectionRangeManager(
    val workspace: WorkspaceManager
) extends RequestModule[SelectionRangeCapabilities, Either[Boolean, WorkDoneProgressOptions]] {

  override val `type`: ConfigType[SelectionRangeCapabilities, Either[Boolean, WorkDoneProgressOptions]] =
    SelectionRangeConfigType

  override def getRequestHandlers: Seq[TelemeteredRequestHandler[_, _]] =
    Seq(new TelemeteredRequestHandler[SelectionRangeParams, Seq[SelectionRange]] {
      override def `type`: SelectionRangeRequestType.type = SelectionRangeRequestType

      override protected def task(params: SelectionRangeParams): Future[Seq[SelectionRange]] =
        selectionRange(
          params.textDocument.uri,
          params.positions.map(p => LspRangeConverter.toPosition(p)),
          uuid(params)
        )

      override protected def code(params: SelectionRangeParams): String = "SelectionRange"

      override protected def beginType(params: SelectionRangeParams): MessageTypes =
        MessageTypes.BEGIN_SELECTION_RANGE

      override protected def endType(params: SelectionRangeParams): MessageTypes =
        MessageTypes.END_SELECTION_RANGE

      override protected def msg(params: SelectionRangeParams): String =
        s"request for selection range on ${params.textDocument.uri} @ ${params.positions} "

      override protected def uri(params: SelectionRangeParams): String = params.textDocument.uri

      /** If Some(_), this will be sent as a response as a default for a managed exception
        */
      override protected val empty: Option[Seq[SelectionRange]] = Some(Seq())
    })

  def selectionRange(uri: String, positions: Seq[Position], uuid: String): Future[Seq[SelectionRange]] = {
    workspace
      .getLastUnit(uri, uuid)
      .flatMap(_.getLast)
      .map(_.unit.objWithAST.flatMap(_.annotations.astElement()))
      .flatMap(ast => {
        Future {
          ast.map(ypart => SelectionRangeFinder.findSelectionRange(ypart, positions)).getOrElse(Seq.empty)
        }
      })

  }

  override def initialize(): Future[Unit] = Future.successful()

  override def applyConfig(config: Option[SelectionRangeCapabilities]): Either[Boolean, WorkDoneProgressOptions] =
    Left(true)
}
