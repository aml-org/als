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
import org.mulesoft.lsp.feature.telemetry.{MessageTypes, TelemetryProvider}
import org.mulesoft.lsp.feature.{RequestHandler, documentsymbol}

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

  override def getRequestHandlers: Seq[RequestHandler[_, _]] = Seq(
    new RequestHandler[DocumentSymbolParams, Either[Seq[SymbolInformation], Seq[documentsymbol.DocumentSymbol]]] {
      override def `type`: DocumentSymbolRequestType.type =
        DocumentSymbolRequestType

      override def apply(
          params: DocumentSymbolParams): Future[Either[Seq[SymbolInformation], Seq[documentsymbol.DocumentSymbol]]] =
        onDocumentStructure(params.textDocument.uri)
          .map(_.map(LspConverter.toLspDocumentSymbol))
          .map(Right.apply)
    }
  )

  val onDocumentStructureListener: String => Future[Seq[DocumentSymbol]] =
    onDocumentStructure

  override def initialize(): Future[Unit] =
    Future.successful()

  def onDocumentStructure(uri: String): Future[Seq[DocumentSymbol]] = {
    val telemetryUUID: String = UUID.randomUUID().toString
    logger.debug("Asked for structure:\n" + uri, "StructureManager", "onDocumentStructure")
    telemetryProvider.timeProcess(
      "Structure",
      MessageTypes.BEGIN_STRUCTURE,
      MessageTypes.END_STRUCTURE,
      "StructureManager : onDocumentStructure",
      uri,
      innerOnDocumentStructure(uri, telemetryUUID),
      telemetryUUID
    )
  }

  private def innerOnDocumentStructure(uri: String, telemetryUUID: String)() =
    unitAccesor
      .getLastUnit(uri, telemetryUUID)
      .flatMap(_.getLast)
      .map(cu => {
        val r = getStructureFromAST(cu.unit, telemetryUUID) // todo: if isn't resolved yet map future
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

  def getStructureFromAST(ast: BaseUnit, uuid: String): List[DocumentSymbol] =
    StructureBuilder.listSymbols(ast)
}
