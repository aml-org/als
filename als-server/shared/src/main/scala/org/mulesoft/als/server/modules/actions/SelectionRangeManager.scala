package org.mulesoft.als.server.modules.actions

import org.mulesoft.als.actions.selection.SelectionRangeFinder
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.als.server.RequestModule
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.workspace.WorkspaceManager
import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.configuration.StaticRegistrationOptions
import org.mulesoft.lsp.feature.TelemeteredRequestHandler
import org.mulesoft.lsp.feature.selectionRange._
import org.mulesoft.lsp.feature.telemetry.MessageTypes.MessageTypes
import org.mulesoft.lsp.feature.telemetry.{MessageTypes, TelemetryProvider}
import org.mulesoft.amfintegration.AmfImplicits._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
class SelectionRangeManager(val workspace: WorkspaceManager,
                            private val telemetryProvider: TelemetryProvider,
                            private val logger: Logger)
    extends RequestModule[SelectionRangeCapabilities, Either[Boolean, StaticRegistrationOptions]] {

  override val `type`: ConfigType[SelectionRangeCapabilities, Either[Boolean, StaticRegistrationOptions]] =
    SelectionRangeConfigType

  override def getRequestHandlers: Seq[TelemeteredRequestHandler[_, _]] =
    Seq(new TelemeteredRequestHandler[SelectionRangeParams, Seq[SelectionRange]] {
      override def `type`: SelectionRangeRequestType.type = SelectionRangeRequestType

      override protected def task(params: SelectionRangeParams): Future[Seq[SelectionRange]] =
        selectionRange(params.textDocument.uri,
                       params.positions.map(p => LspRangeConverter.toPosition(p)),
                       uuid(params))

      override protected def telemetry: TelemetryProvider = telemetryProvider

      override protected def code(params: SelectionRangeParams): String = "SelectionRange"

      override protected def beginType(params: SelectionRangeParams): MessageTypes =
        MessageTypes.BEGIN_SELECTION_RANGE

      override protected def endType(params: SelectionRangeParams): MessageTypes =
        MessageTypes.END_SELECTION_RANGE

      override protected def msg(params: SelectionRangeParams): String =
        s"request for document highlights on ${params.textDocument.uri} @ ${params.positions}"

      override protected def uri(params: SelectionRangeParams): String = params.textDocument.uri
    })

  def selectionRange(uri: String, positions: Seq[Position], uuid: String): Future[Seq[SelectionRange]] = {
    workspace
      .getLastUnit(uri, uuid)
      .flatMap(_.getLast)
      .map(_.unit.objWithAST.flatMap(_.annotations.ast()))
      .flatMap(ast => {
        Future {
          ast.flatMap(ypart => SelectionRangeFinder.findSelectionRange(ypart, positions)).getOrElse(Seq.empty)
        }
      })

  }

  override def initialize(): Future[Unit] = Future.successful()

  override def applyConfig(config: Option[SelectionRangeCapabilities]): Either[Boolean, StaticRegistrationOptions] =
    Left(true)
}
