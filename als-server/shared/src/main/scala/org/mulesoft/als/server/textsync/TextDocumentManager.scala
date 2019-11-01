package org.mulesoft.als.server.textsync

import java.util.UUID

import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.modules.ast.TextListener
import org.mulesoft.amfmanager.ParserHelper
import org.mulesoft.lsp.feature.telemetry.MessageTypes
import org.mulesoft.lsp.textsync.TextDocumentSyncKind.TextDocumentSyncKind
import org.mulesoft.lsp.textsync._

import scala.concurrent.Future
import scala.language.experimental.macros

class TextDocumentManager(val uriToEditor: TextDocumentContainer,
                          val dependencies: List[TextListener],
                          private val logger: Logger)
    extends TextDocumentSyncConsumer {

  override val `type`: TextDocumentSyncConfigType.type =
    TextDocumentSyncConfigType

  override def applyConfig(
      config: Option[SynchronizationClientCapabilities]): Either[TextDocumentSyncKind, TextDocumentSyncOptions] = {
    logger.debug("Config applied", "TextDocumentManager", "applyConfig")

    Right(
      TextDocumentSyncOptions(
        save = None,
        openClose = Some(true),
        change = Some(TextDocumentSyncKind.Full)
      ))
  }

  override def initialize(): Future[Unit] = Future.successful()

//  def getTextDocument(uri: String): Option[TextDocument] = {
//    logger.debugDetail(s"Asked for uri $uri, while having following editors registered: " +
//                         this.uriToEditor.uris.mkString(","),
//                       "EditorManager",
//                       "getTextDocument")
//
//    val directResult = uriToEditor.get(uri)
//
//    if (directResult.isDefined)
//      directResult
//    else if (uri.startsWith("file://") || uri.startsWith("FILE://")) {
//
//      var found: Option[TextDocument] = None
//
//      if (uri.startsWith("file:///") || uri.startsWith("FILE:///")) {
//        val path: String = uri.substring("file:///".length).replace("%5C", "\\")
//        val result       = this.uriToEditor.get(path)
//        if (result.isDefined)
//          found = result
//      }
//
//      if (found.isEmpty) {
//        val path: String = uri.substring("file://".length).replace("%5C", "\\")
//        val result       = this.uriToEditor.get(path)
//        if (result.isDefined)
//          found = result
//      }
//
//      found
//    } else None
//  }

  def onOpenDocument(document: OpenedDocument): Unit = {

    logger.debug("Document is opened", "EditorManager", "onOpenDocument")

    val syntax = determineSyntax(document.uri, document.text)

    this.uriToEditor + (document.uri,
    new TextDocument(document.uri, document.version, document.text, /* language, */ syntax, logger))

    this.dependencies.foreach(_.trigger(document.uri))
  }

  def documentWasChanged(document: ChangedDocument) {
    logger.debug("Document is changed", "EditorManager", "onChangeDocument")

    logger.debugDetail("Uri is:\n " + document.uri, "EditorManager", "onChangeDocument")
    logger.debugDetail("Text is:\n " + document.text, "EditorManager", "onChangeDocument")

    uriToEditor
      .get(document.uri)
      .foreach(current => {
        val currentVersion = current.version
        val currentText    = current.text

        if (currentVersion == document.version) {
          this.logger.debugDetail("Version of the reported change is equal to the previous one",
                                  "EditorManager",
                                  "onChangeDocument")

          return
        }

        if (document.version < currentVersion && document.text.contains(currentText)) {
          this.logger.debugDetail("No changes detected", "EditorManager", "onChangeDocument")

          return
        }

      })

    val syntax = this.determineSyntax(document.uri, document.text.get)

    uriToEditor + (document.uri,
    new TextDocument(document.uri, document.version, document.text.get, /* language, */ syntax, logger))

    dependencies.foreach(_.trigger(document.uri))
  }

  def onCloseDocument(uri: String): Unit = uriToEditor.remove(uri)

  def onChangePosition(uri: String, position: Int): Unit = uriToEditor.get(uri).foreach(_.setCursorPosition(position))

  def determineSyntax(url: String, text: String): String =
    if (text.trim.startsWith("{")) "JSON" else "YAML"

  override def didOpen(params: DidOpenTextDocumentParams): Unit =
    onOpenDocument(OpenedDocument(params.textDocument.uri, params.textDocument.version, params.textDocument.text))

  override def didChange(params: DidChangeTextDocumentParams): Unit = {
    val document = params.textDocument
    val version  = document.version.getOrElse(0)
    val text     = params.contentChanges.headOption.map(_.text)

    documentWasChanged(ChangedDocument(document.uri, version, text, None))
  }

  override def didClose(params: DidCloseTextDocumentParams): Unit =
    onCloseDocument(params.textDocument.uri)

  override def didFocus(params: DidFocusParams): Unit =
    uriToEditor
      .get(params.uri)
      .foreach(
        td =>
          dependencies
            .foreach(_.onFocus(params.uri)))

  override def indexDialect(params: IndexDialectParams): Unit =
    dependencies.foreach(_.indexDialect(params.uri, params.content))
}
