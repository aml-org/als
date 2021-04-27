package org.mulesoft.als.server.modules.structure

import java.util.UUID

import amf.core.model.document.BaseUnit
import org.mulesoft.als.server.RequestModule
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.modules.common.LspConverter
import org.mulesoft.als.server.modules.workspace.CompilableUnit
import org.mulesoft.als.server.workspace.UnitAccessor
import org.mulesoft.language.outline.structure.structureImpl.{DocumentSymbol, StructureBuilder}
import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.feature.documentsymbol._
import org.mulesoft.lsp.feature.telemetry.MessageTypes.MessageTypes
import org.mulesoft.lsp.feature.telemetry.{MessageTypes, TelemetryProvider}
import org.mulesoft.lsp.feature.{RequestHandler, TelemeteredRequestHandler, documentsymbol}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class StructureManager(val unitAccesor: UnitAccessor[CompilableUnit],
                       private val telemetryProvider: TelemetryProvider,
                       private val logger: Logger)
    extends RequestModule[DocumentSymbolClientCapabilities, Unit] {

  override val `type`: ConfigType[DocumentSymbolClientCapabilities, Unit] =
    DocumentSymbolConfigType

  override def applyConfig(config: Option[DocumentSymbolClientCapabilities]): Unit = {
    // todo: use DocumentSymbolClientCapabilities <- SymbolKindClientCapabilities to avoid sending unsupported symbols
  }

  override def getRequestHandlers: Seq[TelemeteredRequestHandler[_, _]] = Seq(
    new TelemeteredRequestHandler[DocumentSymbolParams,
                                  Either[Seq[SymbolInformation], Seq[documentsymbol.DocumentSymbol]]] {
      override def `type`: DocumentSymbolRequestType.type =
        DocumentSymbolRequestType

      override def task(
          params: DocumentSymbolParams): Future[Either[Seq[SymbolInformation], Seq[documentsymbol.DocumentSymbol]]] =
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

  val onDocumentStructureListener: String => Future[Seq[DocumentSymbol]] =
    onDocumentStructure

  override def initialize(): Future[Unit] =
    Future.successful()

  def onDocumentStructure(uri: String): Future[Seq[DocumentSymbol]] = {
    val telemetryUUID: String = UUID.randomUUID().toString
    unitAccesor
      .getLastUnit(uri, telemetryUUID)
      .flatMap(_.getLast)
      .map(cu => {
        val r = getStructureFromAST(cu, telemetryUUID) // todo: if isn't resolved yet map future
        logger
          .debug(s"Got result for url $uri of size ${r.size}", "StructureManager", "onDocumentStructure")
        r
      })
      .recoverWith({
        case e: Exception =>
          logger
            .error(s"Got error for $uri message: ${e.getMessage}", "StructureManager", "onDocumentStructure")
          Future.successful(List.empty)
      })
  }

  def getStructureFromAST(cu: CompilableUnit, uuid: String): List[DocumentSymbol] =
    StructureBuilder.listSymbols(cu.unit, cu.definedBy)

}
