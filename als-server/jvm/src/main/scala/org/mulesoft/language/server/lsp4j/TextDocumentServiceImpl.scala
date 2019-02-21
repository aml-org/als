package org.mulesoft.language.server.lsp4j

import java.util
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.completedFuture
import java.util.stream.Collectors

import org.eclipse.lsp4j.jsonrpc.messages
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
  SymbolInformation,
  SymbolKind,
  TextDocumentPositionParams
}
import org.mulesoft.als.suggestions.interfaces.Suggestion
import org.mulesoft.language.common.dtoTypes._
import org.mulesoft.language.common.logger.{LoggerSettings, PrintLnLogger}
import org.mulesoft.language.server.core.connections.AbstractServerConnection
import org.mulesoft.language.server.internal.DefaultJVMFileSystem
import org.mulesoft.language.server.lsp4j.Lsp4JConversions._

import scala.collection.JavaConverters._
import scala.compat.java8.FutureConverters._
import scala.concurrent.Future
import org.mulesoft.language.outline.structure.structureImpl.{DocumentSymbol => InternalDocumentSymbol}

class TextDocumentServiceImpl(val settings: Option[LoggerSettings])
    extends TextDocumentService
    with AbstractServerConnection
    with PrintLnLogger
    with DefaultJVMFileSystem
    with AbstractLanguageClientAware {

  private def toJavaSuggestion(completionItems: Seq[Suggestion]): util.List[CompletionItem] = {
    completionItems.asJava
      .stream()
      .map[CompletionItem]((item: Suggestion) => {
        val result = new CompletionItem(item.displayText)
        result.setInsertText(item.text)
        result.setDetail(item.category)

        result
      })
      .collect(Collectors.toList[CompletionItem])
  }

  private def toJavaSymbols(symbols: Seq[InternalDocumentSymbol]): util.List[DocumentSymbol] = {
    symbols.asJava
      .stream()
      .map[DocumentSymbol]((symbol: InternalDocumentSymbol) => {
        val result =
          new DocumentSymbol(symbol.name, SymbolKind.forValue(symbol.kind.index), symbol.range, symbol.selectionRange)
        result.setDeprecated(symbol.deprecated)
        result.setChildren(toJavaSymbols(symbol.children))

        result
      })
      .collect(Collectors.toList[DocumentSymbol])
  }

  private def toProtocol(
      symbols: Seq[InternalDocumentSymbol]): util.List[messages.Either[SymbolInformation, DocumentSymbol]] = {
    toJavaSymbols(symbols)
      .stream()
      .map[messages.Either[SymbolInformation, DocumentSymbol]]((symbol: DocumentSymbol) => {
        messages.Either.forRight(symbol)
      })
      .collect(Collectors.toList[messages.Either[SymbolInformation, DocumentSymbol]])
  }

  private def toJavaLocation(refItems: Seq[ILocation]): util.List[_ <: Location] = {
    refItems.asJava
      .stream()
      .map[Location]((item: ILocation) => {
        new Location(
          s"file://${item.uri}",
          new org.eclipse.lsp4j.Range(
            new org.eclipse.lsp4j.Position(item.posRange.start.line, item.posRange.start.column),
            new org.eclipse.lsp4j.Position(item.posRange.end.line, item.posRange.end.column)
          )
        )
      })
      .collect(Collectors.toList[Location])
  }

  override def references(params: ReferenceParams): CompletableFuture[util.List[_ <: Location]] = {
    notifyFindReferences(params.getTextDocument.getUri, params.getPosition).toJava
      .thenApply[util.List[_ <: Location]](toJavaLocation)
      .toCompletableFuture
  }

  override def definition(params: TextDocumentPositionParams): CompletableFuture[util.List[_ <: Location]] = {
    notifyOpenDeclaration(params.getTextDocument.getUri, params.getPosition).toJava
      .thenApply[util.List[_ <: Location]](toJavaLocation)
      .toCompletableFuture
  }

  override def completion(completionParams: CompletionParams)
    : CompletableFuture[messages.Either[util.List[CompletionItem], CompletionList]] = {
    notifyDocumentCompletion(completionParams.getTextDocument.getUri, completionParams.getPosition).toJava
      .thenApply[util.List[CompletionItem]](toJavaSuggestion)
      .thenApply[messages.Either[util.List[CompletionItem], CompletionList]](
        messages.Either.forLeft[util.List[CompletionItem], CompletionList])
      .toCompletableFuture
  }

  override def documentSymbol(params: DocumentSymbolParams)
    : CompletableFuture[util.List[messages.Either[SymbolInformation, DocumentSymbol]]] = {
    notifyDocumentStructure(params.getTextDocument.getUri).toJava
      .thenApply[util.List[messages.Either[SymbolInformation, DocumentSymbol]]](toProtocol)
      .toCompletableFuture
  }

  override def resolveCompletionItem(unresolved: CompletionItem): CompletableFuture[CompletionItem] =
    completedFuture(unresolved)

  override def didOpen(params: DidOpenTextDocumentParams): Unit = {
    val document       = params.getTextDocument
    val openedDocument = OpenedDocument(document.getUri, document.getVersion, document.getText)
    println(openedDocument)
    notifyDocumentOpened(openedDocument)
  }

  override def didChange(params: DidChangeTextDocumentParams): Unit = {
    val document = params.getTextDocument
    val texts = params.getContentChanges.asScala
      .map(event => TextEdit(Range(0, 0), event.getText))
    println(ChangedDocument(document.getUri, document.getVersion, Some(texts.head.text), None))
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
