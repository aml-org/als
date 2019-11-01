package org.mulesoft.als.server

import amf.core.remote.Platform
import amf.core.unsafe.PlatformSecrets
import amf.internal.environment.Environment
import org.mulesoft.als.common.{DirectoryResolver, PlatformDirectoryResolver}
import org.mulesoft.als.server.client.ClientNotifier
import org.mulesoft.als.server.logger.{EmptyLogger, Logger}
import org.mulesoft.als.server.modules.ManagersFactory
import org.mulesoft.als.server.textsync.{TextDocumentContainer, TextDocumentManager}
import org.mulesoft.lsp.common.{TextDocumentIdentifier, TextDocumentItem, VersionedTextDocumentIdentifier}
import org.mulesoft.lsp.configuration.InitializeParams
import org.mulesoft.lsp.feature.diagnostic.PublishDiagnosticsParams
import org.mulesoft.lsp.feature.telemetry.TelemetryMessage
import org.mulesoft.lsp.server.LanguageServer
import org.mulesoft.lsp.textsync._
import org.scalatest.{AsyncFunSuite, Matchers, OptionValues}

import scala.collection.mutable
import scala.concurrent.{Future, Promise}

abstract class LanguageServerBaseTest extends AsyncFunSuite with PlatformSecrets with Matchers with OptionValues {

  val logger: Logger = EmptyLogger

  protected val initializeParams: InitializeParams = InitializeParams.default
  private def sync(fn: () => Any): Any             = synchronized(fn())

  object MockDiagnosticClientNotifier extends ClientNotifier {
    val promises: mutable.Queue[Promise[PublishDiagnosticsParams]] = mutable.Queue.empty

    override def notifyTelemetry(params: TelemetryMessage): Unit = {}

    override def notifyDiagnostic(msg: PublishDiagnosticsParams): Unit =
      sync(
        () =>
          if (promises.forall(_.isCompleted)) promises.enqueue(Promise[PublishDiagnosticsParams].success(msg))
          else promises.dequeueFirst(!_.isCompleted).map(_.success(msg)))

    def nextCall: Future[PublishDiagnosticsParams] =
      sync(() =>
        if (promises.isEmpty) {
          val promise = Promise[PublishDiagnosticsParams]()
          promises.enqueue(promise)
          promise.future
        } else promises.dequeue().future) match {
        case r: Future[PublishDiagnosticsParams] => r
        case _                                   => Future.failed(new Exception("Wrong notification"))
      }
  }

  sealed class MockTelemetryClientNotifier extends ClientNotifier {
    val promises: mutable.Queue[Promise[TelemetryMessage]] = mutable.Queue.empty

    override def notifyTelemetry(msg: TelemetryMessage): Unit =
      sync(
        () =>
          if (promises.forall(_.isCompleted)) promises.enqueue(Promise[TelemetryMessage].success(msg))
          else promises.dequeueFirst(!_.isCompleted).map(_.success(msg)))

    def nextCall: Future[TelemetryMessage] =
      sync(() =>
        if (promises.isEmpty) {
          val promise = Promise[TelemetryMessage]()
          promises.enqueue(promise)
          promise.future
        } else promises.dequeue().future) match {
        case r: Future[TelemetryMessage] => r
        case _                           => Future.failed(new Exception("Wrong notification"))
      }

    override def notifyDiagnostic(params: PublishDiagnosticsParams): Unit = {}
  }

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

  def openFileNotification(server: LanguageServer)(file: String, content: String): Future[PublishDiagnosticsParams] = {
    openFile(server)(file, content)
    MockDiagnosticClientNotifier.nextCall
  }

  def focusNotification(server: LanguageServer)(file: String, version: Int): Future[PublishDiagnosticsParams] = {
    onFocus(server)(file, version)
    MockDiagnosticClientNotifier.nextCall
  }

  def changeNotification(
      server: LanguageServer)(file: String, content: String, version: Int): Future[PublishDiagnosticsParams] = {
    changeFile(server)(file, content, version)
    MockDiagnosticClientNotifier.nextCall
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
