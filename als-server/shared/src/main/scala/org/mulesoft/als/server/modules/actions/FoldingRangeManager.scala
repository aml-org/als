package org.mulesoft.als.server.modules.actions

import org.mulesoft.als.actions.folding.FileRanges
import org.mulesoft.als.server.RequestModule
import org.mulesoft.als.logger.Logger
import org.mulesoft.als.server.workspace.WorkspaceManager
import org.mulesoft.amfintegration.AmfImplicits._
import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.feature.TelemeteredRequestHandler
import org.mulesoft.lsp.feature.folding._
import org.mulesoft.lsp.feature.telemetry.MessageTypes.MessageTypes
import org.mulesoft.lsp.feature.telemetry.{MessageTypes, TelemetryProvider}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class FoldingRangeManager(
    val workspace: WorkspaceManager,
    private val telemetryProvider: TelemetryProvider,
    private val logger: Logger
) extends RequestModule[FoldingRangeCapabilities, Boolean] {

  private var active = false

  override val `type`: ConfigType[FoldingRangeCapabilities, Boolean] =
    FoldingRangeConfigType

  override val getRequestHandlers: Seq[TelemeteredRequestHandler[_, _]] = Seq(
    new TelemeteredRequestHandler[FoldingRangeParams, Seq[FoldingRange]] {
      override def `type`: FoldingRangeRequestType.type =
        FoldingRangeRequestType

      override def task(params: FoldingRangeParams): Future[Seq[FoldingRange]] =
        foldingRange(params.textDocument.uri, uuid(params))

      override protected def telemetry: TelemetryProvider = telemetryProvider
      override protected def code(params: FoldingRangeParams): String =
        "FoldingRange"
      override protected def beginType(params: FoldingRangeParams): MessageTypes =
        MessageTypes.BEGIN_FOLDING
      override protected def endType(params: FoldingRangeParams): MessageTypes =
        MessageTypes.END_FOLDING
      override protected def msg(params: FoldingRangeParams): String =
        s"request for document highlights on ${params.textDocument.uri}"
      override protected def uri(params: FoldingRangeParams): String =
        params.textDocument.uri

      /** If Some(_), this will be sent as a response as a default for a managed exception
        */
      override protected val empty: Option[Seq[FoldingRange]] = Some(Seq())
    }
  )

  override def applyConfig(config: Option[FoldingRangeCapabilities]): Boolean = {
    active = config.isDefined
    active
  }

  def foldingRange(uri: String, uuid: String): Future[Seq[FoldingRange]] =
    workspace
      .getLastUnit(uri, uuid)
      .flatMap(_.getLast)
      .map(
        _.unit.objWithAST
          .flatMap(_.annotations.astElement())
          .map(FileRanges.ranges)
          .getOrElse(Seq.empty)
      )

  override def initialize(): Future[Unit] = Future.successful()
}
