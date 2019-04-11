package org.mulesoft.als.server.lsp4j

import java.util
import java.util.concurrent.CompletableFuture

import org.eclipse.lsp4j.jsonrpc.messages
import org.eclipse.lsp4j.services.TextDocumentService
import org.eclipse.lsp4j.{CompletionItem, CompletionList, CompletionParams, DidChangeTextDocumentParams, DidCloseTextDocumentParams, DidOpenTextDocumentParams, DidSaveTextDocumentParams, DocumentSymbol, DocumentSymbolParams, Location, ReferenceParams, RenameParams, SymbolInformation, TextDocumentPositionParams, WorkspaceEdit}
import org.mulesoft.lsp.server
import org.mulesoft.als.server.lsp4j.Lsp4JConversions._
import org.mulesoft.als.server.lsp4j.LspConversions._
import org.mulesoft.lsp.feature.{RequestHandler, RequestType, reference}
import org.mulesoft.lsp.feature.completion.CompletionRequestType
import org.mulesoft.lsp.feature.definition.DefinitionRequestType
import org.mulesoft.lsp.feature.documentsymbol.DocumentSymbolRequestType
import org.mulesoft.lsp.feature.reference.ReferenceRequestType
import org.mulesoft.lsp.feature.rename.RenameRequestType
import org.mulesoft.lsp.common

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TextDocumentServiceImpl(private val inner: server.LanguageServer)
    extends TextDocumentService {

  private val textDocumentSyncConsumer = inner.textDocumentSyncConsumer

  override def didOpen(params: DidOpenTextDocumentParams): Unit =
    textDocumentSyncConsumer.didOpen(params)

  override def didChange(params: DidChangeTextDocumentParams): Unit =
    textDocumentSyncConsumer.didChange(params)

  override def didClose(params: DidCloseTextDocumentParams): Unit =
    textDocumentSyncConsumer.didClose(params)

  override def didSave(params: DidSaveTextDocumentParams): Unit = {}

  private def resolveHandler[P, R](`type`: RequestType[P, R]): RequestHandler[P, R] = {
    val maybeHandler = inner.resolveHandler(`type`)
    if (maybeHandler.isEmpty) throw new UnsupportedOperationException else maybeHandler.get
  }

  override def references(params: ReferenceParams): CompletableFuture[util.List[_ <: Location]] =
    javaFuture(resolveHandler(ReferenceRequestType)(params), lsp4JLocations)

  override def definition(params: TextDocumentPositionParams): CompletableFuture[util.List[_ <: Location]] =
    javaFuture(resolveHandler(DefinitionRequestType)(params), lsp4JLocations)

  override def completion(params: CompletionParams): CompletableFuture[messages.Either[util.List[CompletionItem], CompletionList]] =
    javaFuture(resolveHandler(CompletionRequestType)(params), lsp4JCompletionEither)

  override def rename(params: RenameParams): CompletableFuture[WorkspaceEdit] =
    javaFuture(resolveHandler(RenameRequestType)(params), lsp4JWorkspaceEdit)

  override def documentSymbol(params: DocumentSymbolParams): CompletableFuture[util.List[messages.Either[SymbolInformation, DocumentSymbol]]] =
    javaFuture(resolveHandler(DocumentSymbolRequestType)(params), lsp4JDocumentSymbolsResult)
}
