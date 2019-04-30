package org.mulesoft.als.server.modules.structure

import common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.modules.common.LspConverter
import org.mulesoft.als.server.RequestModule
import org.mulesoft.als.server.modules.hlast.{HlAstListener, HlAstManager}
import org.mulesoft.als.server.textsync.TextDocumentManager
import org.mulesoft.high.level.implementation.AlsPlatform
import org.mulesoft.high.level.interfaces.IParseResult
import org.mulesoft.language.outline.structure.structureImpl.SymbolKind.SymbolKind
import org.mulesoft.language.outline.structure.structureImpl.{ConfigFactory, DocumentSymbol, StructureBuilder}
import org.mulesoft.als.server.util.PathRefine
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

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class StructureManager(private val textDocumentManager: TextDocumentManager,
                       private val hlAstManager: HlAstManager,
                       private val platform: AlsPlatform,
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

  val onNewASTAvailableListener: HlAstListener = (uri: String, version: Int, ast: IParseResult) => {
    StructureManager.this.newASTAvailable(uri, version, ast)
  }

  val onDocumentStructureListener: String => Future[Seq[DocumentSymbol]] =
    onDocumentStructure

  override def initialize(): Future[Unit] = {
    this.hlAstManager.onNewASTAvailable(this.onNewASTAvailableListener)
    Future.successful()
  }

  def newASTAvailable(uri: String, astVersion: Int, ast: IParseResult): Unit = {
    logger.debug("Got new AST:\n" + ast.toString, "StructureManager", "newASTAvailable")

    val editor = textDocumentManager.getTextDocument(uri)

    if (editor.isDefined) {

      val struct = this.getStructureFromAST(ast, editor.get.language, editor.get.cursorPosition)

      logger
        .debugDetail(s"Got result for url $uri of size ${struct.size}", "StructureManager", "onDocumentStructure")

      val structureReport = StructureReport(
        uri,
        astVersion,
        struct
      )

      // not part oif the protocol, extend it?
      // this.connection.structureAvailable(structureReport)
    }
  }

  def onDocumentStructure(url: String): Future[Seq[DocumentSymbol]] = {
    val emptyDocumentSymbol = List(
      DocumentSymbol("",
                     SymbolKind(21),
                     false,
                     PositionRange(Position(0, 0), Position(0, 0)),
                     PositionRange(Position(0, 0), Position(0, 0)),
                     Nil))

    logger.debug("Asked for structure:\n" + url, "StructureManager", "onDocumentStructure")

    val editor = textDocumentManager.getTextDocument(url)

    if (editor.isDefined) {

      this.hlAstManager
        .forceGetCurrentAST(url)
        .map(ast => {
          val result = StructureManager.this
            .getStructureFromAST(ast.rootASTUnit.rootNode, editor.get.language, editor.get.cursorPosition)

          logger
            .debugDetail(s"Got result for url $url of size ${result.size}", "StructureManager", "onDocumentStructure")

          result
        })
        .recoverWith {
          case t: Throwable =>
            logger
              .debugDetail(s"Got the following error in $url => ${t.getMessage}",
                           "StructureManager",
                           "onDocumentStructure")
            Future.successful(emptyDocumentSymbol)
        }

    } else {
      Future.successful(emptyDocumentSymbol)
    }
  }

  def getStructureFromAST(ast: IParseResult, language: String, position: Int): List[DocumentSymbol] = {

    ConfigFactory.getConfig(new ASTProvider(ast, position, language)) match {
      case Some(config) => StructureBuilder.listSymbols(ast, config)
      case _            => Nil
    }
  }
}
