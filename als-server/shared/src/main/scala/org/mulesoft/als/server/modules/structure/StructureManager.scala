package org.mulesoft.als.server.modules.structure

import org.mulesoft.als.server.RequestModule
import org.mulesoft.als.logger.Logger
import org.mulesoft.als.server.modules.common.LspConverter
import org.mulesoft.als.server.modules.workspace.CompilableUnit
import org.mulesoft.als.server.workspace.UnitAccessor
import org.mulesoft.language.outline.structure.structureImpl.{DocumentSymbol, StructureBuilder}
import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.configuration.WorkDoneProgressOptions
import org.mulesoft.lsp.feature.documentsymbol._
import org.mulesoft.lsp.feature.telemetry.MessageTypes.MessageTypes
import org.mulesoft.lsp.feature.telemetry.{MessageTypes, TelemetryProvider}
import org.mulesoft.lsp.feature.{TelemeteredRequestHandler, documentsymbol}

import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class StructureManager(
    val unitAccesor: UnitAccessor[CompilableUnit],
    private val telemetryProvider: TelemetryProvider
) extends RequestModule[DocumentSymbolClientCapabilities, Either[Boolean, WorkDoneProgressOptions]] {

  override val `type`: ConfigType[DocumentSymbolClientCapabilities, Either[Boolean, WorkDoneProgressOptions]] =
    DocumentSymbolConfigType

  override def applyConfig(
      config: Option[DocumentSymbolClientCapabilities]
  ): Either[Boolean, WorkDoneProgressOptions] = {
    // todo: use DocumentSymbolClientCapabilities <- SymbolKindClientCapabilities to avoid sending unsupported symbols
    Left(true)
  }

  override def getRequestHandlers: Seq[
    TelemeteredRequestHandler[DocumentSymbolParams, Either[Seq[SymbolInformation], Seq[documentsymbol.DocumentSymbol]]]
  ] = Seq(
    new TelemeteredRequestHandler[DocumentSymbolParams, Either[Seq[SymbolInformation], Seq[
      documentsymbol.DocumentSymbol
    ]]] {
      override def `type`: DocumentSymbolRequestType.type =
        DocumentSymbolRequestType

      override def task(
          params: DocumentSymbolParams
      ): Future[Either[Seq[SymbolInformation], Seq[documentsymbol.DocumentSymbol]]] =
        onDocumentStructure(params.textDocument.uri)
          .map(_.map(LspConverter.toLspDocumentSymbol))
          .map(Right.apply)

      override protected def telemetry: TelemetryProvider = telemetryProvider

      override protected def code(params: DocumentSymbolParams): String = "StructureManager"

      override protected def beginType(params: DocumentSymbolParams): MessageTypes = MessageTypes.BEGIN_STRUCTURE

      override protected def endType(params: DocumentSymbolParams): MessageTypes = MessageTypes.END_STRUCTURE

      override protected def msg(params: DocumentSymbolParams): String =
        s"Requested structure for ${params.textDocument.uri}"

      override protected def uri(params: DocumentSymbolParams): String = params.textDocument.uri

      override protected val empty: Option[Either[Seq[SymbolInformation], Seq[documentsymbol.DocumentSymbol]]] =
        Some(Right(Seq()))
    }
  )

  override def initialize(): Future[Unit] =
    Future.successful()

  def onDocumentStructure(uri: String): Future[Seq[DocumentSymbol]] = {
    val telemetryUUID: String = UUID.randomUUID().toString
    unitAccesor
      .getLastUnit(uri, telemetryUUID)
      .flatMap(_.getLast)
      .map(cu => {
        val r = getStructureFromAST(cu, telemetryUUID) // todo: if isn't resolved yet map future
        Logger
          .debug(s"Got result for url $uri of size ${r.size}", "StructureManager", "onDocumentStructure")
        r
      })
  }

  def getStructureFromAST(cu: CompilableUnit, uuid: String): List[DocumentSymbol] =
    StructureBuilder.listSymbols(cu.unit, cu.definedBy)

}
