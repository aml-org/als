package org.mulesoft.als.server.textsync

import amf.core.remote._
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.util.PathRefine
import org.mulesoft.lsp.textsync.TextDocumentSyncKind.TextDocumentSyncKind
import org.mulesoft.lsp.textsync._

import scala.collection.mutable
import scala.concurrent.Future
import scala.language.experimental.macros

class TextDocumentManager(private val logger: Logger, private val platform: Platform)
    extends TextDocumentSyncConsumer {

  override val `type`: TextDocumentSyncConfigType.type = TextDocumentSyncConfigType

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

  var uriToEditor: mutable.Map[String, TextDocument] = mutable.HashMap()

  var documentChangeListeners: mutable.Set[ChangedDocument => Unit] = mutable.Set()

  var documentOpenListeners: mutable.Set[OpenedDocument => Unit] = mutable.Set()

  var documentCloseListeners: mutable.Set[String => Unit] = mutable.Set()

  var documentChangeExecutor: Option[IDocumentChangeExecutor] = None

  override def initialize(): Future[Unit] = Future.successful()

  def onChangeDocument(listener: ChangedDocument => Unit, unsubscribe: Boolean = false): Unit =
    if (unsubscribe) documentChangeListeners.remove(listener) else documentChangeListeners.add(listener)

  def onOpenedDocument(listener: OpenedDocument => Unit, unsubscribe: Boolean = false): Unit =
    if (unsubscribe) documentOpenListeners.remove(listener) else documentOpenListeners.add(listener)

  def onClosedDocument(listener: String => Unit, unsubscribe: Boolean = false): Unit =
    if (unsubscribe) documentCloseListeners.remove(listener) else documentCloseListeners.add(listener)

  def getTextDocument(uri: String): Option[TextDocument] = {
    logger.debugDetail(s"Asked for uri $uri, while having following editors registered: " +
                         this.uriToEditor.keys.mkString(","),
                       "EditorManager",
                       "getTextDocument")

    val directResult = uriToEditor.get(uri)

    if (directResult.isDefined) {

      directResult
    } else if (uri.startsWith("file://") || uri.startsWith("FILE://")) {

      var found: Option[TextDocument] = None

      if (uri.startsWith("file:///") || uri.startsWith("FILE:///")) {
        val path: String = uri.substring("file:///".length).replace("%5C", "\\")
        val result       = this.uriToEditor.get(path)
        if (result.isDefined) {
          found = result
        }
      }

      if (found.isEmpty) {
        val path: String = uri.substring("file://".length).replace("%5C", "\\")
        val result       = this.uriToEditor.get(path)
        if (result.isDefined) {
          found = result
        }
      }

      found
    } else {

      None
    }
  }

  def onOpenDocument(document: OpenedDocument): Unit = {

    logger.debug("Document is opened", "EditorManager", "onOpenDocument")

    val language = determineLanguage(document.uri, document.text)
    val syntax   = determineSyntax(document.uri, document.text)

    this.uriToEditor(document.uri) =
      new TextDocument(document.uri, document.version, document.text, language, syntax, logger)

    this.documentChangeListeners.foreach { listener =>
      listener(ChangedDocument(document.uri, document.version, Some(document.text), None))
    }
  }

  def setDocumentChangeExecutor(executor: IDocumentChangeExecutor): Unit = {
    this.documentChangeExecutor = Some(executor)
  }

  def getDocumentChangeExecutor: Option[IDocumentChangeExecutor] = {

    this.documentChangeExecutor
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

    val language = this.determineLanguage(document.uri, document.text.get)
    val syntax   = this.determineSyntax(document.uri, document.text.get)

    uriToEditor(document.uri) =
      new TextDocument(document.uri, document.version, document.text.get, language, syntax, logger)

    documentChangeListeners.foreach(listener => listener(document))
  }

  def onCloseDocument(uri: String): Unit = uriToEditor.remove(uri)

  def onChangePosition(uri: String, position: Int): Unit = {
    val editorOption = this.getTextDocument(uri)

    if (editorOption.isDefined) {
      editorOption.get.setCursorPosition(position)
    }
  }

  def determineLanguage(url: String, text: String): String = {

    if (url.endsWith(".raml")) {
      if (text.startsWith("#%RAML 1.0")) {
        Raml10.toString
      } else {
        Raml08.toString
      }
    } else if ((url.endsWith(".yaml") || url.endsWith(".yml")) && text.startsWith("#%")) {
      Aml.toString
    } else {
      Oas20.toString
    }
  }

  def determineSyntax(url: String, text: String): String =
    if (text.trim.startsWith("{")) "JSON" else "YAML"

  override def didOpen(params: DidOpenTextDocumentParams): Unit = {
    onOpenDocument(OpenedDocument(params.textDocument.uri, params.textDocument.version, params.textDocument.text))
  }

  override def didChange(params: DidChangeTextDocumentParams): Unit = {
    val document = params.textDocument
    val version  = document.version.getOrElse(0)
    val text     = params.contentChanges.headOption.map(_.text)

    documentWasChanged(ChangedDocument(document.uri, version, text, None))
  }

  override def didClose(params: DidCloseTextDocumentParams): Unit = {
    onCloseDocument(params.textDocument.uri)
  }
}
