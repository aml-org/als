package org.mulesoft.als.server.modules.actions

import org.mulesoft.als.actions.hover.HoverAction
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.als.server.RequestModule
import org.mulesoft.als.server.workspace.WorkspaceManager
import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.feature.hover._
import org.mulesoft.lsp.feature.telemetry.MessageTypes.MessageTypes
import org.mulesoft.lsp.feature.telemetry.{MessageTypes, TelemetryProvider}
import org.mulesoft.lsp.feature.{RequestType, TelemeteredRequestHandler}

import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class HoverManager(wm: WorkspaceManager, telemetryProvider: TelemetryProvider)
    extends RequestModule[HoverClientCapabilities, Boolean] {
  private var active = true

  override val `type`: ConfigType[HoverClientCapabilities, Boolean] =
    HoverConfigType

  override def applyConfig(config: Option[HoverClientCapabilities]): Boolean = {
    // should check mark up?
    active = config.exists(_.contentFormat.contains(MarkupKind.Markdown)) || config.isEmpty
    true
  }

  override def initialize(): Future[Unit] = Future.successful()

  override def getRequestHandlers: Seq[TelemeteredRequestHandler[_, _]] = Seq(new HoverTelemeteredRequestHandler())

  class HoverTelemeteredRequestHandler() extends TelemeteredRequestHandler[HoverParams, Hover] {
    override def `type`: RequestType[HoverParams, Hover] = HoverRequestType

    override protected def telemetry: TelemetryProvider = telemetryProvider

    override protected def task(params: HoverParams): Future[Hover] = hover(params)

    override protected def code(params: HoverParams): String = "HoverManager"

    override protected def beginType(params: HoverParams): MessageTypes = MessageTypes.BEGIN_HOVER

    override protected def endType(params: HoverParams): MessageTypes = MessageTypes.END_HOVER

    override protected def msg(params: HoverParams): String =
      s"request for hover on ${params.textDocument.uri} and position ${params.position.toString}"

    override protected def uri(params: HoverParams): String = params.textDocument.uri

    private def hover(params: HoverParams): Future[Hover] = {
      val uuid = UUID.randomUUID().toString
      wm.getLastUnit(params.textDocument.uri, uuid).map { cu =>
        val dtoPosition: Position = LspRangeConverter.toPosition(params.position)
        HoverAction(
          cu.unit,
          cu.tree,
          cu.yPartBranch,
          dtoPosition,
          params.textDocument.uri,
          cu.context.state.editorState.vocabularyRegistry,
          cu.definedBy
        ).getHover
      // if sequence, we could show all the semantic hierarchy?
      }
    }

    /** If Some(_), this will be sent as a response as a default for a managed exception
      */
    override protected val empty: Option[Hover] = Some(Hover(Seq(), None))
  }
}
