package org.mulesoft.language.server.lsp4j

import java.util
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.completedFuture
import java.util.stream.Collectors

import org.eclipse.lsp4j.jsonrpc.messages
import org.eclipse.lsp4j.services.TextDocumentService
import org.eclipse.lsp4j.{CompletionItem, CompletionList, CompletionParams, Diagnostic, DiagnosticSeverity, DidChangeTextDocumentParams, DidCloseTextDocumentParams, DidOpenTextDocumentParams, DidSaveTextDocumentParams, PublishDiagnosticsParams}
import org.mulesoft.als.suggestions.interfaces.Suggestion
import org.mulesoft.language.common.dtoTypes._
import org.mulesoft.language.common.logger.{LoggerSettings, PrintLnLogger}
import org.mulesoft.language.server.core.connections.AbstractServerConnection
import org.mulesoft.language.server.internal.DefaultJVMFileSystem
import org.mulesoft.language.server.lsp4j.Lsp4JConversions._

import scala.collection.JavaConverters._
import scala.compat.java8.FutureConverters._
import scala.concurrent.Future

class TextDocumentServiceImpl(val settings: Option[LoggerSettings]) extends TextDocumentService
  with AbstractServerConnection with PrintLnLogger with DefaultJVMFileSystem with AbstractLanguageClientAware {

  private def toJava(completionItems: Seq[Suggestion]): util.List[CompletionItem] = {
    completionItems.asJava.stream()
      .map[CompletionItem]((item: Suggestion) => {
        val result = new CompletionItem(item.displayText)
        result.setInsertText(item.text)
        result.setDetail(item.category)

        result
      })
      .collect(Collectors.toList[CompletionItem])
  }

  override def completion(completionParams: CompletionParams): CompletableFuture[messages.Either[util.List[CompletionItem], CompletionList]] = {
    notifyDocumentCompletion(completionParams.getTextDocument.getUri, completionParams.getPosition)
      .toJava
      .thenApply[util.List[CompletionItem]](toJava)
      .thenApply[messages.Either[util.List[CompletionItem], CompletionList]](messages.Either.forLeft[util.List[CompletionItem], CompletionList])
      .toCompletableFuture
  }


  override def resolveCompletionItem(unresolved: CompletionItem): CompletableFuture[CompletionItem] = completedFuture(unresolved)

  override def didOpen(params: DidOpenTextDocumentParams): Unit = {
    val document = params.getTextDocument
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

  override def validated(report: ValidationReport): Unit = clients.foreach(client => {
    val diagnosticList = report.issues.map(issue => {
      new Diagnostic(issue.range, issue.text, DiagnosticSeverity.Error, issue.filePath)
    }).asJava.stream().collect(Collectors.toList[Diagnostic])
    client.publishDiagnostics(new PublishDiagnosticsParams(report.pointOfViewUri, diagnosticList))
  })

  override def structureAvailable(report: StructureReport): Unit = ???

  override def onDocumentDetails(listener: (String, Int) => Future[IDetailsItem], unsubscribe: Boolean): Unit = ???

  override def detailsAvailable(report: IDetailsReport): Unit = ???

  override def displayActionUI(uiDisplayRequest: IUIDisplayRequest): Future[Any] = ???
}
