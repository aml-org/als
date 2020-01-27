package org.mulesoft.als.server

import amf.core.unsafe.PlatformSecrets
import org.mulesoft.als.server.logger.{EmptyLogger, Logger}
import org.mulesoft.lsp.common.{TextDocumentIdentifier, TextDocumentItem, VersionedTextDocumentIdentifier}
import org.mulesoft.lsp.configuration.AlsInitializeParams
import org.mulesoft.lsp.feature.diagnostic.{
  CleanDiagnosticTreeParams,
  CleanDiagnosticTreeRequestType,
  PublishDiagnosticsParams
}
import org.mulesoft.lsp.feature.telemetry.TelemetryMessage
import org.mulesoft.lsp.server.LanguageServer
import org.mulesoft.lsp.textsync._
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

  def requestCleanDiagnostic(server: LanguageServer)(uri: String): Future[Seq[PublishDiagnosticsParams]] =
    server
      .resolveHandler(CleanDiagnosticTreeRequestType)
      .value
      .apply(CleanDiagnosticTreeParams(TextDocumentIdentifier(uri)))

  def focusNotification(server: LanguageServer)(file: String, version: Int): Future[Unit] = Future.successful {
    onFocus(server)(file, version)
  }

  def changeNotification(server: LanguageServer)(file: String, content: String, version: Int): Future[Unit] =
    Future.successful {
      changeFile(server)(file, content, version)
    }

  def buildServer(): LanguageServer

  def withServer[R](fn: LanguageServer => Future[R]): Future[R] = {
    val server = buildServer()
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
}
