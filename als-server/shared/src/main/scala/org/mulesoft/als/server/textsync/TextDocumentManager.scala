package org.mulesoft.als.server.textsync

import amf.core.internal.remote.Platform
import org.mulesoft.als.common.URIImplicits._
import org.mulesoft.als.logger.Logger
import org.mulesoft.als.server.modules.ast._
import org.mulesoft.als.server.protocol.textsync.{AlsTextDocumentSyncConsumer, DidFocusParams}
import org.mulesoft.lsp.textsync.TextDocumentSyncKind.TextDocumentSyncKind
import org.mulesoft.lsp.textsync._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.experimental.macros

class TextDocumentManager(val uriToEditor: TextDocumentContainer,
                          val dependencies: List[TextListener],
                          private val logger: Logger)
    extends AlsTextDocumentSyncConsumer {

  implicit private val platform: Platform = this.uriToEditor.platform

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

  override def initialize(): Future[Unit] = uriToEditor.initialize()

  def onOpenDocument(document: OpenedDocument): Future[Unit] = {

    logger.debug(s"Document is opened ${document.uri}", "EditorManager", "onOpenDocument")

    val syntax = determineSyntax(document.uri, document.text)

    this.uriToEditor + (document.uri, TextDocument(document.uri, document.version, document.text, syntax))

    Future.sequence(this.dependencies.map(_.notify(document.uri, OPEN_FILE))).flatMap(_ => Future.unit)
  }

  def documentWasChanged(document: ChangedDocument): Future[Unit] = {
    logger.debug(s"Document is changed ${document.uri}", "EditorManager", "onChangeDocument")

    val currentDocument = uriToEditor.get(document.uri)

    currentDocument.foreach(current => {
      val currentVersion = current.version
      val currentText    = current.text

      if (currentVersion == document.version)
        this.logger.debug(s"Version of the reported change is equal to the previous one at ${document.uri}",
                          "EditorManager",
                          "onChangeDocument")

      if (document.version < currentVersion && document.text.contains(currentText))
        this.logger.debug(s"No changes detected for ${document.uri}", "EditorManager", "onChangeDocument")

    })

    val syntax = this.determineSyntax(document.uri, document.text.get)

    uriToEditor + (document.uri, TextDocument(document.uri, document.version, document.text.get, syntax))
    val notificationKind =
      if (currentDocument.exists(c => c.version < document.version)) CHANGE_FILE_NEW_VERSION else CHANGE_FILE

    Future.sequence(this.dependencies.map(_.notify(document.uri, notificationKind))).flatMap(_ => Future.unit)
  }

  def onCloseDocument(uri: String): Future[Unit] = {
    logger.debug(s"Document closed $uri", "EditorManager", "onCloseDocument")
    uriToEditor.remove(uri)
    Future.sequence(this.dependencies.map(_.notify(uri, CLOSE_FILE))).flatMap(_ => Future.unit)
  }

//  def onChangePosition(uri: String, position: Int): Unit = uriToEditor.get(uri).foreach(_.setCursorPosition(position))

  def determineSyntax(url: String, text: String): String =
    if (text.trim.startsWith("{")) "JSON" else "YAML"

  override def didOpen(params: DidOpenTextDocumentParams): Future[Unit] = {
    val uri = params.textDocument.uri
    if (!uri.isValidFileUri)
      logger.warning(s"Adding invalid URI file to manager: $uri", "TextDocumentManager", "didOpen")
    onOpenDocument(OpenedDocument(uri.toAmfUri, params.textDocument.version, params.textDocument.text))
  }

  override def didChange(params: DidChangeTextDocumentParams): Future[Unit] = {
    val document = params.textDocument
    val version  = document.version.getOrElse(0)
    val text     = params.contentChanges.headOption.map(_.text)
    val uri      = document.uri
    if (!uri.isValidFileUri)
      logger.warning(s"Editing invalid URI file to manager: $uri", "TextDocumentManager", "didChange")

    documentWasChanged(ChangedDocument(uri.toAmfUri, version, text, None))
  }

  override def didClose(params: DidCloseTextDocumentParams): Future[Unit] = {
    val uri = params.textDocument.uri
    if (!uri.isValidFileUri)
      logger.warning(s"Removing invalid URI file to manager: $uri", "TextDocumentManager", "didClose")
    onCloseDocument(uri.toAmfUri)
  }

  override def didFocus(params: DidFocusParams): Future[Unit] =
    uriToEditor
      .get(params.uri.toAmfUri)
      .map(
        _ =>
          Future
            .sequence(dependencies
              .map(_.notify(params.uri, FOCUS_FILE)))
            .flatMap(_ => Future.unit))
      .getOrElse(Future.unit)
}
