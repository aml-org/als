package org.mulesoft.als.server.modules.actions

import amf.core.client.scala.model.document.BaseUnit
import org.mulesoft.als.actions.formatting.RangeFormatting
import org.mulesoft.als.common.{ElementWithIndentation, NodeBranchBuilder}
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.als.logger.Logger
import org.mulesoft.als.server.RequestModule
import org.mulesoft.als.server.workspace.WorkspaceManager
import org.mulesoft.amfintegration.AmfImplicits.BaseUnitImp
import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.configuration.WorkDoneProgressOptions
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
import org.yaml.model.YPart

import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DocumentRangeFormattingManager(
    val workspace: WorkspaceManager,
    private val telemetryProvider: TelemetryProvider,
    private val logger: Logger
) extends RequestModule[DocumentRangeFormattingClientCapabilities, Either[Boolean, WorkDoneProgressOptions]]
    with FormattingManager {

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

      /** If Some(_), this will be sent as a response as a default for a managed exception
        */
      override protected val empty: Option[Seq[TextEdit]] = Some(Seq())
    })

  override val `type`: ConfigType[DocumentRangeFormattingClientCapabilities, Either[Boolean, WorkDoneProgressOptions]] =
    DocumentRangeFormattingConfigType

  def onDocumentRangeFormatting(params: DocumentRangeFormattingParams): Future[Seq[TextEdit]] = {
    val uuid   = UUID.randomUUID().toString
    val isJson = params.textDocument.uri.endsWith(".json")
    logger.debug(
      "Document formatting for " + params.textDocument.uri + " range: " + params.range,
      "DocumentRangeFormattingManager",
      "onDocumentRangeFormatting"
    )
    workspace
      .getLastUnit(params.textDocument.uri, uuid)
      .map(cu => {
        (getParentPart(cu.unit, params.range, strict = true) match {
          case ElementWithIndentation(yPart: YPart, Some(indentation)) =>
            Some((yPart, indentation / params.options.tabSize + 1))
          case ElementWithIndentation(yPart: YPart, None) => Some((yPart, 0))
          case _                                          => None
        }).map(t =>
          RangeFormatting(
            t._1,
            params.options,
            isJson,
            getSyntaxErrors(cu.errorsCollected, params.textDocument.uri),
            cu.unit.raw,
            t._2
          )
            .format()
        ).getOrElse(Seq.empty)
      })
  }

  def getParentPart(unit: BaseUnit, range: Range, strict: Boolean): ElementWithIndentation =
    NodeBranchBuilder.getAstForRange(
      unit.ast.getOrElse(NodeBranchBuilder.astFromBaseUnit(unit)),
      LspRangeConverter.toPosition(range.start).toAmfPosition,
      LspRangeConverter.toPosition(range.end).toAmfPosition,
      strict
    )

  override def applyConfig(
      config: Option[DocumentRangeFormattingClientCapabilities]
  ): Either[Boolean, WorkDoneProgressOptions] = {
    active = config.isDefined
    Left(active)
  }

  override def initialize(): Future[Unit] = Future.successful()

}
