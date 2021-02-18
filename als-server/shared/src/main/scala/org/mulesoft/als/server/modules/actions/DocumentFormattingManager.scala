package org.mulesoft.als.server.modules.actions

import amf.core.model.document.BaseUnit
import org.mulesoft.als.actions.formatting.RangeFormatting
import org.mulesoft.als.server.RequestModule
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.workspace.WorkspaceManager
import org.mulesoft.amfintegration.AmfImplicits.BaseUnitImp
import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.edit.TextEdit
import org.mulesoft.lsp.feature.TelemeteredRequestHandler
import org.mulesoft.lsp.feature.documentFormatting.{
  DocumentFormattingClientCapabilities,
  DocumentFormattingConfigType,
  DocumentFormattingParams,
  DocumentFormattingRequestType
}
import org.mulesoft.lsp.feature.telemetry.MessageTypes.MessageTypes
import org.mulesoft.lsp.feature.telemetry.{MessageTypes, TelemetryProvider}
import org.yaml.model.YPart

import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DocumentFormattingManager(val workspace: WorkspaceManager,
                                private val telemetryProvider: TelemetryProvider,
                                private val logger: Logger)
    extends RequestModule[DocumentFormattingClientCapabilities, Boolean]
    with FormattingManager {

  private var active = false

  override def getRequestHandlers: Seq[TelemeteredRequestHandler[_, _]] =
    Seq(new TelemeteredRequestHandler[DocumentFormattingParams, Seq[TextEdit]] {
      override protected def telemetry: TelemetryProvider = telemetryProvider

      override protected def task(params: DocumentFormattingParams): Future[Seq[TextEdit]] =
        onDocumentFormatting(params)

      override protected def code(params: DocumentFormattingParams): String = "DocumentFormatting"

      override protected def beginType(params: DocumentFormattingParams): MessageTypes =
        MessageTypes.BEGIN_DOCUMENT_FORMATTING

      override protected def endType(params: DocumentFormattingParams): MessageTypes =
        MessageTypes.END_DOCUMENT_FORMATTING

      override protected def msg(params: DocumentFormattingParams): String =
        s"Request for document formatting on ${params.textDocument.uri}"

      override protected def uri(params: DocumentFormattingParams): String = params.textDocument.uri

      override def `type`: DocumentFormattingRequestType.type = DocumentFormattingRequestType
    })

  override val `type`: ConfigType[DocumentFormattingClientCapabilities, Boolean] =
    DocumentFormattingConfigType

  def onDocumentFormatting(params: DocumentFormattingParams): Future[Seq[TextEdit]] = {
    val uuid   = UUID.randomUUID().toString
    val isJson = params.textDocument.uri.endsWith(".json")
    logger.debug("Document formatting for " + params.textDocument.uri,
                 "DocumentFormattingManager",
                 "onDocumentFormatting")
    workspace
      .getLastUnit(params.textDocument.uri, uuid)
      .map(cu => {
        getParts(cu.unit)
          .map(
            part =>
              RangeFormatting(part,
                              params.options,
                              isJson,
                              getSyntaxErrors(cu.errorsCollected, params.textDocument.uri),
                              cu.unit.raw)
                .format())
          .getOrElse(Seq.empty)
      })
  }

  def getParts(unit: BaseUnit): Option[YPart] = unit.ast

  override def applyConfig(config: Option[DocumentFormattingClientCapabilities]): Boolean = {
    active = config.isDefined
    active
  }

  override def initialize(): Future[Unit] = Future.successful()
}
