package org.mulesoft.als.server

import amf.core.unsafe.PlatformSecrets
import org.mulesoft.als.server.feature.diagnostic.{CleanDiagnosticTreeParams, CleanDiagnosticTreeRequestType}
import org.mulesoft.als.server.logger.{EmptyLogger, Logger}
import org.mulesoft.als.server.modules.diagnostic.AlsPublishDiagnosticsParams
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.protocol.configuration.AlsInitializeParams
import org.mulesoft.als.server.protocol.textsync.DidFocusParams
import org.mulesoft.lsp.configuration.WorkspaceFolder
import org.mulesoft.lsp.feature.common.{TextDocumentIdentifier, TextDocumentItem, VersionedTextDocumentIdentifier}
import org.mulesoft.lsp.feature.documentsymbol.{
  DocumentSymbol,
  DocumentSymbolParams,
  DocumentSymbolRequestType,
  SymbolInformation
}
import org.mulesoft.lsp.feature.telemetry.TelemetryMessage
import org.mulesoft.lsp.textsync._
import org.mulesoft.lsp.workspace.{DidChangeWorkspaceFoldersParams, WorkspaceFoldersChangeEvent}
import org.scalatest.{AsyncFunSuite, Matchers, OptionValues}

import scala.concurrent.Future

abstract class LanguageServerBaseTest extends AsyncFunSuite with PlatformSecrets with Matchers with OptionValues {

  val logger: Logger = EmptyLogger

  protected val initializeParams: AlsInitializeParams = AlsInitializeParams.default

  private def telemetryNotifications(mockTelemetryClientNotifier: MockTelemetryClientNotifier)(
      qty: Int,
      previous: Seq[TelemetryMessage]): Future[Seq[TelemetryMessage]] = {
    if (qty < 0) Future(previous)
    else if (qty > 0)
      mockTelemetryClientNotifier.nextCall.flatMap(nc =>
        telemetryNotifications(mockTelemetryClientNotifier)(qty - 1, previous :+ nc))
    else
      mockTelemetryClientNotifier.nextCall.map(nc => previous :+ nc)
  }

  def withTelemetry(mockTelemetryClientNotifier: MockTelemetryClientNotifier)(
      qty: Int,
      fn: () => Unit): Future[Seq[TelemetryMessage]] = {
    fn()
    telemetryNotifications(mockTelemetryClientNotifier)(qty - 1, Nil)
  }

  def openFileNotification(server: LanguageServer)(file: String, content: String): Future[Unit] = Future.successful {
    openFile(server)(file, content)
  }

  def requestCleanDiagnostic(server: LanguageServer)(uri: String): Future[Seq[AlsPublishDiagnosticsParams]] =
    server
      .resolveHandler(CleanDiagnosticTreeRequestType)
      .value
      .apply(CleanDiagnosticTreeParams(TextDocumentIdentifier(uri)))

  def requestDocumentSymbol(server: LanguageServer)(
      uri: String): Future[Either[Seq[SymbolInformation], Seq[DocumentSymbol]]] =
    server
      .resolveHandler(DocumentSymbolRequestType)
      .value
      .apply(DocumentSymbolParams(TextDocumentIdentifier(uri)))

  def focusNotification(server: LanguageServer)(file: String, version: Int): Future[Unit] = Future.successful {
    onFocus(server)(file, version)
  }

  def changeNotification(server: LanguageServer)(file: String, content: String, version: Int): Future[Unit] =
    Future.successful {
      changeFile(server)(file, content, version)
    }

  def withServer[R](server: LanguageServer)(fn: LanguageServer => Future[R]): Future[R] = {
    server
      .initialize(initializeParams)
      .flatMap(_ => {
        server.initialized()
        fn(server)
      })
  }

  def openFile(server: LanguageServer)(uri: String, text: String): Unit =
    server.textDocumentSyncConsumer.didOpen(DidOpenTextDocumentParams(TextDocumentItem(uri, "", 0, text)))

  def onFocus(server: LanguageServer)(uri: String, version: Int): Unit =
    server.textDocumentSyncConsumer.didFocus(DidFocusParams(uri, version))

  def closeFile(server: LanguageServer)(uri: String): Unit =
    server.textDocumentSyncConsumer.didClose(DidCloseTextDocumentParams(TextDocumentIdentifier(uri)))

  def changeFile(server: LanguageServer)(uri: String, text: String, version: Int): Unit =
    server.textDocumentSyncConsumer.didChange(
      DidChangeTextDocumentParams(
        VersionedTextDocumentIdentifier(uri, Some(version)),
        Seq(TextDocumentContentChangeEvent(text))
      ))

  def rootPath: String

  def filePath(path: String): String = {
    s"file://als-server/shared/src/test/resources/$rootPath/$path"
      .replace('\\', '/')
      .replace("null/", "")
  }

  def addWorkspaceFolder(server: LanguageServer)(ws: WorkspaceFolder): Future[Unit] = {
    server.workspaceService.didChangeWorkspaceFolders(
      params = DidChangeWorkspaceFoldersParams(WorkspaceFoldersChangeEvent(List(ws), List()))
    )
    Future.successful()
  }

  def removeWorkspaceFolder(server: LanguageServer)(ws: WorkspaceFolder): Future[Unit] = {
    server.workspaceService.didChangeWorkspaceFolders(
      params = DidChangeWorkspaceFoldersParams(WorkspaceFoldersChangeEvent(List(), List(ws)))
    )
    Future.successful()
  }

  def didChangeWorkspaceFolders(server: LanguageServer)(added: List[WorkspaceFolder],
                                                        removed: List[WorkspaceFolder]): Future[Unit] = {
    server.workspaceService.didChangeWorkspaceFolders(
      params = DidChangeWorkspaceFoldersParams(WorkspaceFoldersChangeEvent(added, removed))
    )
    Future.successful()
  }
}
