package org.mulesoft.als.server

import amf.core.unsafe.PlatformSecrets
import org.mulesoft.als.server.logger.{EmptyLogger, Logger}
import org.mulesoft.als.server.platform.ServerPlatform
import org.mulesoft.als.server.textsync.TextDocumentManager
import org.mulesoft.lsp.common.{TextDocumentIdentifier, TextDocumentItem, VersionedTextDocumentIdentifier}
import org.mulesoft.lsp.configuration.{ClientCapabilities, InitializeParams, TraceKind}
import org.mulesoft.lsp.server.LanguageServer
import org.mulesoft.lsp.textsync.{
  DidChangeTextDocumentParams,
  DidCloseTextDocumentParams,
  DidOpenTextDocumentParams,
  TextDocumentContentChangeEvent
}
import org.scalatest.{AsyncFunSuite, Matchers, OptionValues}

import scala.concurrent.Future

abstract class LanguageServerBaseTest extends AsyncFunSuite with PlatformSecrets with Matchers with OptionValues {

  val logger: Logger = EmptyLogger

  val initializeParams = InitializeParams.default

  def addModules(documentManager: TextDocumentManager,
                 serverPlatform: ServerPlatform,
                 builder: LanguageServerBuilder): LanguageServerBuilder

  def withServer[R](fn: LanguageServer => Future[R]): Future[R] = {
    val documentManager                = new TextDocumentManager(logger, platform)
    val serverPlatform: ServerPlatform = new ServerPlatform(logger, documentManager)
    val builder = LanguageServerBuilder()
      .withTextDocumentSyncConsumer(documentManager)

    val server = addModules(documentManager, serverPlatform, builder).build()

    server
      .initialize(initializeParams)
      .flatMap(_ => {
        server.initialized()
        fn(server)
      })
  }

  def openFile(server: LanguageServer)(uri: String, text: String): Unit =
    server.textDocumentSyncConsumer.didOpen(DidOpenTextDocumentParams(TextDocumentItem(uri, "", 0, text)))

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
