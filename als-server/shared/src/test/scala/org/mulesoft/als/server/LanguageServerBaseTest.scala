package org.mulesoft.als.server

import amf.core.remote.Platform
import amf.core.unsafe.PlatformSecrets
import amf.internal.environment.Environment
import org.mulesoft.als.common.{DirectoryResolver, PlatformDirectoryResolver}
import org.mulesoft.als.server.client.ClientNotifier
import org.mulesoft.als.server.logger.{EmptyLogger, Logger}
import org.mulesoft.als.server.textsync.TextDocumentManager
import org.mulesoft.lsp.common.{TextDocumentIdentifier, TextDocumentItem, VersionedTextDocumentIdentifier}
import org.mulesoft.lsp.configuration.InitializeParams
import org.mulesoft.lsp.feature.diagnostic.PublishDiagnosticsParams
import org.mulesoft.lsp.feature.telemetry.TelemetryMessage
import org.mulesoft.lsp.server.LanguageServer
import org.mulesoft.lsp.textsync.{
  DidChangeTextDocumentParams,
  DidCloseTextDocumentParams,
  DidFocusParams,
  DidOpenTextDocumentParams,
  TextDocumentContentChangeEvent
}
import org.scalatest.{AsyncFunSuite, Matchers, OptionValues}

import scala.concurrent.{Future, Promise}

abstract class LanguageServerBaseTest extends AsyncFunSuite with PlatformSecrets with Matchers with OptionValues {

  val logger: Logger = EmptyLogger

  protected val initializeParams = InitializeParams.default

  object MockClientNotifier extends ClientNotifier {
    var promise: Option[Promise[PublishDiagnosticsParams]] = None

    override def notifyTelemetry(params: TelemetryMessage): Unit = {}

    def nextCall: Future[PublishDiagnosticsParams] = {
      if (promise.isEmpty)
        promise = Some(Promise[PublishDiagnosticsParams]())
      promise.get.future
    }

    override def notifyDiagnostic(params: PublishDiagnosticsParams): Unit = {
      promise.foreach(_.success(params))
      promise = None
    }
  }

  def openFileNotification(server: LanguageServer)(file: String, content: String): Future[PublishDiagnosticsParams] = {
    openFile(server)(file, content)
    MockClientNotifier.nextCall
  }

  def focusNotification(server: LanguageServer)(file: String, version: Int): Future[PublishDiagnosticsParams] = {
    onFocus(server)(file, version)
    MockClientNotifier.nextCall
  }

  def changeNotification(
      server: LanguageServer)(file: String, content: String, version: Int): Future[PublishDiagnosticsParams] = {
    changeFile(server)(file, content, version)
    MockClientNotifier.nextCall
  }

  def addModules(documentManager: TextDocumentManager,
                 platform: Platform,
                 directoryResolver: DirectoryResolver,
                 baseEnvironment: Environment,
                 builder: LanguageServerBuilder): LanguageServerBuilder

  def withServer[R](fn: LanguageServer => Future[R]): Future[R] = {
    val documentManager = new TextDocumentManager(platform, logger)
    val builder = LanguageServerBuilder()
      .withTextDocumentSyncConsumer(documentManager)

    val directoryResolver = new PlatformDirectoryResolver(platform)
    val baseEnvironment   = Environment().add(new PlatformFileLoader(platform))
    val server            = addModules(documentManager, platform, directoryResolver, baseEnvironment, builder).build()

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
