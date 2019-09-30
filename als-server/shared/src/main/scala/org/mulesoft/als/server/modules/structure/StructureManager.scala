package org.mulesoft.als.server.modules.structure

import java.util.UUID

import amf.core.model.document.BaseUnit
import amf.core.remote.Platform
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.server.RequestModule
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.modules.ast.{AstListener, AstManager}
import org.mulesoft.als.server.modules.common.LspConverter
import org.mulesoft.als.server.textsync.TextDocumentManager
import org.mulesoft.language.outline.structure.structureImpl.SymbolKind.SymbolKind
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

class StructureManager(private val textDocumentManager: TextDocumentManager,
                       private val astManager: AstManager,
                       private val telemetryProvider: TelemetryProvider,
                       private val logger: Logger,
                       private val platform: Platform)
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

  val onNewASTAvailableListener: AstListener = (uri: String, version: Int, ast: BaseUnit, uuid: String) =>
    StructureManager.this.newASTAvailable(uri, version, ast, uuid)

  val onDocumentStructureListener: String => Future[Seq[DocumentSymbol]] =
    onDocumentStructure

  override def initialize(): Future[Unit] = {
//    this.astManager.onNewASTAvailable(this.onNewASTAvailableListener)
    Future.successful()
  }

  def newASTAvailable(uri: String, astVersion: Int, ast: BaseUnit, telemetryUUID: String): Unit = {
    logger.debug("Got new AST:\n" + ast.toString, "StructureManager", "newASTAvailable")
//    val telemetryUUID: String = UUID.randomUUID().toString
    telemetryProvider.addTimedMessage("Begin Structure", MessageTypes.BEGIN_STRUCTURE, uri, telemetryUUID)
    val editor = textDocumentManager.getTextDocument(uri)

    if (editor.isDefined) {

      val struct = this.getStructureFromAST(ast, editor.get.cursorPosition)

      logger
        .debugDetail(s"Got result for url $uri of size ${struct.size}", "StructureManager", "onDocumentStructure")

      val structureReport = StructureReport(
        uri,
        astVersion,
        struct
      )
      telemetryProvider.addTimedMessage("End Structure", MessageTypes.END_STRUCTURE, uri, telemetryUUID)
      // not part oif the protocol, extend it?
//       this.connection.structureAvailable(structureReport)
    }
  }

  def onDocumentStructure(uri: String): Future[Seq[DocumentSymbol]] = {
    val telemetryUUID: String = UUID.randomUUID().toString
    val emptyDocumentSymbol = List(
      DocumentSymbol("",
                     SymbolKind(21),
                     false,
                     PositionRange(Position(0, 0), Position(0, 0)),
                     PositionRange(Position(0, 0), Position(0, 0)),
                     Nil))

    logger.debug("Asked for structure:\n" + uri, "StructureManager", "onDocumentStructure")
    telemetryProvider.addTimedMessage("Begin Structure", MessageTypes.BEGIN_STRUCTURE, uri, telemetryUUID)

    val editor = textDocumentManager.getTextDocument(uri)

    if (editor.isDefined) {

      this.astManager
        .forceGetCurrentAST(uri, telemetryUUID)
        .map(ast => {
          val result = StructureManager.this
            .getStructureFromAST(ast, editor.get.cursorPosition)

          logger
            .debugDetail(s"Got result for url $uri of size ${result.size}", "StructureManager", "onDocumentStructure")

          telemetryProvider.addTimedMessage("End Structure", MessageTypes.END_STRUCTURE, uri, telemetryUUID)
          result
        })
        .recoverWith {
          case t: Throwable =>
            logger
              .debugDetail(s"Got the following error in $uri => ${t.getMessage}",
                           "StructureManager",
                           "onDocumentStructure")
            telemetryProvider.addTimedMessage(s"End Structure: ${t.getMessage}",
                                              MessageTypes.END_STRUCTURE,
                                              uri,
                                              telemetryUUID)
            Future.successful(emptyDocumentSymbol)
        }

    } else {
      telemetryProvider.addTimedMessage("End Structure", MessageTypes.END_STRUCTURE, uri, telemetryUUID)
      Future.successful(emptyDocumentSymbol)
    }
  }

  def getStructureFromAST(ast: BaseUnit, position: Int): List[DocumentSymbol] = {
    StructureBuilder.listSymbols(ast)
  }
}
