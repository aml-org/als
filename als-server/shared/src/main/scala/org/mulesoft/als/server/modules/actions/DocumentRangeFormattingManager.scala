package org.mulesoft.als.server.modules.actions

import java.util.UUID

import amf.core.model.document.BaseUnit
import org.mulesoft.als.actions.formatting.RangeFormatting
import org.mulesoft.als.common.YamlWrapper.AlsInputRange
import org.mulesoft.als.common.{NodeBranchBuilder, YPartBranch}
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.als.server.RequestModule
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.workspace.WorkspaceManager
import org.mulesoft.amfintegration.AmfImplicits.BaseUnitImp
import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.edit.TextEdit
import org.mulesoft.lsp.feature.TelemeteredRequestHandler
import org.mulesoft.lsp.feature.common.Range
import org.mulesoft.lsp.feature.documentRangeFormatting.{
  DocumentRangeFormattingClientCapabilities,
  DocumentRangeFormattingConfigType,
  DocumentRangeFormattingParams,
  DocumentRangeFormattingRequestType
}
import org.mulesoft.lsp.feature.telemetry.MessageTypes.MessageTypes
import org.mulesoft.lsp.feature.telemetry.{MessageTypes, TelemetryProvider}
import org.yaml.model.{YMapEntry, YPart}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DocumentRangeFormattingManager(val workspace: WorkspaceManager,
                                     private val telemetryProvider: TelemetryProvider,
                                     private val logger: Logger)
    extends RequestModule[DocumentRangeFormattingClientCapabilities, Boolean] {

  private var active = false

  override def getRequestHandlers: Seq[TelemeteredRequestHandler[_, _]] =
    Seq(new TelemeteredRequestHandler[DocumentRangeFormattingParams, Seq[TextEdit]] {
      override protected def telemetry: TelemetryProvider = telemetryProvider

      override protected def task(params: DocumentRangeFormattingParams): Future[Seq[TextEdit]] =
        onDocumentRangeFormatting(params)

      override protected def code(params: DocumentRangeFormattingParams): String = "DocumentRangeFormatting"

      override protected def beginType(params: DocumentRangeFormattingParams): MessageTypes =
        MessageTypes.BEGIN_DOCUMENT_RANGE_FORMATTING

      override protected def endType(params: DocumentRangeFormattingParams): MessageTypes =
        MessageTypes.END_DOCUMENT_RANGE_FORMATTING

      override protected def msg(params: DocumentRangeFormattingParams): String =
        s"Request for document formatting on ${params.textDocument.uri} with range ${params.range}"

      override protected def uri(params: DocumentRangeFormattingParams): String = params.textDocument.uri

      override def `type`: DocumentRangeFormattingRequestType.type = DocumentRangeFormattingRequestType
    })

  override val `type`: ConfigType[DocumentRangeFormattingClientCapabilities, Boolean] =
    DocumentRangeFormattingConfigType

  def onDocumentRangeFormatting(params: DocumentRangeFormattingParams): Future[Seq[TextEdit]] = {
    val uuid   = UUID.randomUUID().toString
    val isJson = params.textDocument.uri.endsWith(".json")
    workspace
      .getLastUnit(params.textDocument.uri, uuid)
      .map(w => {
        getParts(w.unit, params.range, isJson)
          .map(part =>
            RangeFormatting(part, params.options, w.unit.indentation(part.range.toPositionRange.start), isJson)
              .format())
          .getOrElse(Seq.empty)
      })
  }

  def getParts(unit: BaseUnit, range: Range, isJson: Boolean): Option[YPart] = {
    NodeBranchBuilder
      .astFromBaseUnit(unit)
      .map(
        ast =>
          NodeBranchBuilder.getAstForRange(ast,
                                           LspRangeConverter.toPosition(range.start).toAmfPosition,
                                           LspRangeConverter.toPosition(range.end).toAmfPosition,
                                           isJson))
  }

  override def applyConfig(config: Option[DocumentRangeFormattingClientCapabilities]): Boolean = {
    active = config.isDefined
    active
  }

  override def initialize(): Future[Unit] = Future.successful()

}
