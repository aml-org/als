package org.mulesoft.als.server.modules.structure

import java.util.UUID

import amf.core.model.document.BaseUnit
import org.mulesoft.als.server.RequestModule
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.modules.common.LspConverter
import org.mulesoft.als.server.workspace.WorkspaceManager
import org.mulesoft.language.outline.structure.structureImpl.{DocumentSymbol, StructureBuilder}
import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.feature.RequestHandler
import org.mulesoft.lsp.feature.documentsymbol.{
  DocumentSymbolClientCapabilities,
  DocumentSymbolConfigType,
  DocumentSymbolParams,
  DocumentSymbolRequestType,
  SymbolInformation,
  DocumentSymbol => LspDocumentSymbol
}
import org.mulesoft.lsp.feature.telemetry.{MessageTypes, TelemetryProvider}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class StructureManager(val workspaceManager: WorkspaceManager,
                       private val telemetryProvider: TelemetryProvider,
                       private val logger: Logger)
    extends RequestModule[DocumentSymbolClientCapabilities, Unit] {

  override val `type`: ConfigType[DocumentSymbolClientCapabilities, Unit] = DocumentSymbolConfigType

  override def applyConfig(config: Option[DocumentSymbolClientCapabilities]): Unit = {}

  override def getRequestHandlers: Seq[RequestHandler[_, _]] = Seq(
    new RequestHandler[DocumentSymbolParams, Either[Seq[SymbolInformation], Seq[LspDocumentSymbol]]] {
      override def `type`: DocumentSymbolRequestType.type = DocumentSymbolRequestType

      override def apply(
          params: DocumentSymbolParams): Future[Either[Seq[SymbolInformation], Seq[LspDocumentSymbol]]] = {
        onDocumentStructure(params.textDocument.uri)
          .map(_.map(LspConverter.toLspDocumentSymbol))
          .map(Right.apply)
      }
    }
  )

  val onDocumentStructureListener: String => Future[Seq[DocumentSymbol]] =
    onDocumentStructure

  override def initialize(): Future[Unit] = {
    Future.successful()
  }

  def onDocumentStructure(uri: String): Future[Seq[DocumentSymbol]] = {
    val telemetryUUID: String = UUID.randomUUID().toString

    logger.debug("Asked for structure:\n" + uri, "StructureManager", "onDocumentStructure")
    telemetryProvider.addTimedMessage("Begin Structure", MessageTypes.BEGIN_STRUCTURE, uri, telemetryUUID)
    val results = workspaceManager
      .getLast(uri, telemetryUUID)
      .map(cu => {
        val r = getStructureFromAST(cu.unit, telemetryUUID) // todo: if isn't resolved yet map future
        logger
          .debugDetail(s"Got result for url $uri of size ${r.size}", "StructureManager", "onDocumentStructure")
        r
      })
      .recoverWith({
        case e: Exception =>
          logger
            .debugDetail(s"Got error for $uri message: ${e.getMessage}", "StructureManager", "onDocumentStructure")
          Future.successful(List.empty)
      })

    results.foreach(_ =>
      telemetryProvider.addTimedMessage("End Structure", MessageTypes.END_STRUCTURE, uri, telemetryUUID))
    results
  }

  def getStructureFromAST(ast: BaseUnit, uuid: String): List[DocumentSymbol] = StructureBuilder.listSymbols(ast)

}
