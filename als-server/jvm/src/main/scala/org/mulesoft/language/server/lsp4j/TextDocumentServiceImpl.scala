package org.mulesoft.language.server.lsp4j

import java.util
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.completedFuture
import java.util.stream.Collectors

import common.dtoTypes.EmptyPositionRange
import org.eclipse.lsp4j.jsonrpc.messages
import org.eclipse.lsp4j.jsonrpc.messages.Either.{forLeft, forRight}
import org.eclipse.lsp4j.services.TextDocumentService
import org.eclipse.lsp4j.{
  CompletionItem,
  CompletionList,
  CompletionParams,
  Diagnostic,
  DiagnosticSeverity,
  DidChangeTextDocumentParams,
  DidCloseTextDocumentParams,
  DidOpenTextDocumentParams,
  DidSaveTextDocumentParams,
  DocumentSymbol,
  DocumentSymbolParams,
  Location,
  PublishDiagnosticsParams,
  ReferenceParams,
  RenameParams,
  SymbolInformation,
  TextDocumentPositionParams,
  WorkspaceEdit
}
import org.mulesoft.als.suggestions.interfaces.Suggestion
import org.mulesoft.language.common.dtoTypes._
import org.mulesoft.language.common.logger.{LoggerSettings, PrintLnLogger}
import org.mulesoft.language.outline.structure.structureImpl.{DocumentSymbol => InternalDocumentSymbol}
import org.mulesoft.language.server.core.connections.AbstractServerConnection
import org.mulesoft.language.server.internal.DefaultJVMFileSystem
import org.mulesoft.language.server.lsp4j.Lsp4JConversions._

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TextDocumentServiceImpl(val settings: Option[LoggerSettings])
    extends TextDocumentService
    with AbstractServerConnection
    with PrintLnLogger
    with DefaultJVMFileSystem
    with AbstractLanguageClientAware {

  private def toProtocol(
      symbols: Seq[InternalDocumentSymbol]): util.List[messages.Either[SymbolInformation, DocumentSymbol]] =
    lsp4JDocumentSymbols(symbols)
      .stream()
      .map[messages.Either[SymbolInformation, DocumentSymbol]](symbol =>
        forRight[SymbolInformation, DocumentSymbol](symbol))
      .collect(Collectors.toList[messages.Either[SymbolInformation, DocumentSymbol]])

  override def references(params: ReferenceParams): CompletableFuture[util.List[_ <: Location]] =
    javaFuture(notifyFindReferences(params.getTextDocument.getUri, params.getPosition), lsp4JLocations)

  override def definition(params: TextDocumentPositionParams): CompletableFuture[util.List[_ <: Location]] =
    javaFuture(notifyOpenDeclaration(params.getTextDocument.getUri, params.getPosition), lsp4JLocations)

  override def completion(completionParams: CompletionParams)
    : CompletableFuture[messages.Either[util.List[CompletionItem], CompletionList]] =
    javaFuture[Seq[Suggestion], messages.Either[util.List[CompletionItem], CompletionList]](
      notifyDocumentCompletion(completionParams.getTextDocument.getUri, completionParams.getPosition),
      items => forLeft[util.List[CompletionItem], CompletionList](completionItems(items))
    )

  override def rename(params: RenameParams): CompletableFuture[WorkspaceEdit] = {
    javaFuture(notifyRename(params.getTextDocument.getUri, params.getPosition, params.getNewName), lsp4JWorkspaceEdit)

  }

  override def documentSymbol(
      params: DocumentSymbolParams): CompletableFuture[util.List[messages.Either[SymbolInformation, DocumentSymbol]]] =
    javaFuture(notifyDocumentStructure(params.getTextDocument.getUri), toProtocol)

  override def resolveCompletionItem(unresolved: CompletionItem): CompletableFuture[CompletionItem] =
    completedFuture(unresolved)

  override def didOpen(params: DidOpenTextDocumentParams): Unit = {
    val document       = params.getTextDocument
    val openedDocument = OpenedDocument(document.getUri, document.getVersion, document.getText)
    notifyDocumentOpened(openedDocument)
  }

  override def didChange(params: DidChangeTextDocumentParams): Unit = {
    val document = params.getTextDocument
    val texts = params.getContentChanges.asScala
      .map(event => TextEdit(EmptyPositionRange, event.getText))
    notifyDocumentChanged(ChangedDocument(document.getUri, document.getVersion, Some(texts.head.text), None))
  }

  override def didClose(params: DidCloseTextDocumentParams): Unit = {
    val document = params.getTextDocument
    notifyDocumentClosed(document.getUri)
  }

  override def didSave(params: DidSaveTextDocumentParams): Unit = {}

  override def withSettings(settings: LoggerSettings): this.type = this

  override def validated(report: ValidationReport): Unit =
    clients.foreach(client => {
      val diagnosticList = report.issues
        .map(issue => {
          new Diagnostic(issue.range, issue.text, DiagnosticSeverity.Error, issue.filePath)
        })
        .asJava
        .stream()
        .collect(Collectors.toList[Diagnostic])
      client.publishDiagnostics(new PublishDiagnosticsParams(report.pointOfViewUri, diagnosticList))
    })

  override def onDocumentDetails(listener: (String, Int) => Future[IDetailsItem], unsubscribe: Boolean): Unit = ???

  override def detailsAvailable(report: IDetailsReport): Unit = ???

  override def displayActionUI(uiDisplayRequest: IUIDisplayRequest): Future[Any] = ???
}
