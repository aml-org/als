package org.mulesoft.als.server.textsync

import amf.core.remote.Platform
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.protocol.textsync.{AlsTextDocumentSyncConsumer, DidFocusParams}
import org.mulesoft.als.server.modules.ast._
import org.mulesoft.lsp.textsync.TextDocumentSyncKind.TextDocumentSyncKind
import org.mulesoft.lsp.textsync._
import org.mulesoft.als.common.URIImplicits._

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

  def onOpenDocument(document: OpenedDocument): Unit = {

    logger.debug(s"Document is opened ${document.uri}", "EditorManager", "onOpenDocument")

    val syntax = determineSyntax(document.uri, document.text)

    this.uriToEditor + (document.uri, TextDocument(document.uri, document.version, document.text, syntax))

    this.dependencies.foreach(_.notify(document.uri, OPEN_FILE))
  }

  def documentWasChanged(document: ChangedDocument) {
    logger.debug(s"Document is changed ${document.uri}", "EditorManager", "onChangeDocument")

    uriToEditor
      .get(document.uri)
      .foreach(current => {
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

    dependencies.foreach(_.notify(document.uri, CHANGE_FILE))
  }

  def onCloseDocument(uri: String): Unit = {
    logger.debug(s"Document closed ${uri}", "EditorManager", "onCloseDocument")
    uriToEditor.remove(uri)
    dependencies.foreach(_.notify(uri, CLOSE_FILE))
  }

//  def onChangePosition(uri: String, position: Int): Unit = uriToEditor.get(uri).foreach(_.setCursorPosition(position))

  def determineSyntax(url: String, text: String): String =
    if (text.trim.startsWith("{")) "JSON" else "YAML"

  override def didOpen(params: DidOpenTextDocumentParams): Unit = {
    val uri = params.textDocument.uri
    if (!uri.isValidFileUri)
      logger.warning(s"Adding invalid URI file to manager: $uri", "TextDocumentManager", "didOpen")
    onOpenDocument(OpenedDocument(uri.toAmfUri, params.textDocument.version, params.textDocument.text))
  }

  override def didChange(params: DidChangeTextDocumentParams): Unit = {
    val document = params.textDocument
    val version  = document.version.getOrElse(0)
    val text     = params.contentChanges.headOption.map(_.text)
    val uri      = document.uri
    if (!uri.isValidFileUri)
      logger.warning(s"Editing invalid URI file to manager: $uri", "TextDocumentManager", "didChange")

    documentWasChanged(ChangedDocument(uri.toAmfUri, version, text, None))
  }

  override def didClose(params: DidCloseTextDocumentParams): Unit = {
    val uri = params.textDocument.uri
    if (!uri.isValidFileUri)
      logger.warning(s"Removing invalid URI file to manager: $uri", "TextDocumentManager", "didClose")
    onCloseDocument(uri.toAmfUri)
  }

  override def didFocus(params: DidFocusParams): Unit =
    uriToEditor
      .get(params.uri.toAmfUri)
      .foreach(_ =>
        dependencies
          .foreach(_.notify(params.uri, FOCUS_FILE)))
}
