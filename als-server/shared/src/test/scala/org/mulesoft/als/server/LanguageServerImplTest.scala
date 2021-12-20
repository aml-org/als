package org.mulesoft.als.server

import org.mulesoft.als.logger.Logger
import org.mulesoft.als.server.modules.ast.TextListener
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.modules.{WorkspaceManagerFactory, WorkspaceManagerFactoryBuilder}
import org.mulesoft.als.server.protocol.textsync.{AlsTextDocumentSyncConsumer, DidFocusParams}
import org.mulesoft.als.server.textsync.{
  TextDocument,
  TextDocumentContainer,
  TextDocumentManager,
  TextDocumentSyncBuilder
}
import org.mulesoft.lsp.feature.common.TextDocumentItem
import org.mulesoft.lsp.textsync.TextDocumentSyncKind.TextDocumentSyncKind
import org.mulesoft.lsp.textsync.{
  DidChangeTextDocumentParams,
  DidCloseTextDocumentParams,
  DidOpenTextDocumentParams,
  SynchronizationClientCapabilities,
  TextDocumentSyncConfigType,
  TextDocumentSyncKind,
  TextDocumentSyncOptions
}
import org.scalatest.Assertion

import scala.concurrent.{ExecutionContext, Future}

class LanguageServerImplTest extends LanguageServerBaseTest {

  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  private val exchange = "file://exchange.json"
  private val apiUri   = "file://api.raml"

  test("LanguageServer will open files") {
    val factory =
      new WorkspaceManagerFactoryBuilder(new MockDiagnosticClientNotifier, logger).buildWorkspaceManagerFactory()
    val editorFiles = factory.container
    withServer[Assertion](buildServer(factory)) { server =>
      for {
        _ <- server.textDocumentSyncConsumer.didOpen(
          DidOpenTextDocumentParams(
            TextDocumentItem(apiUri, "raml", 0, "#%RAML 1.0")
          ))
        _ <- server.textDocumentSyncConsumer.didOpen(
          DidOpenTextDocumentParams(
            TextDocumentItem(exchange, "json", 0, "{}")
          ))
      } yield {
        val documentOption: Option[TextDocument] = editorFiles.get(apiUri)
        val exchangeOption: Option[TextDocument] = editorFiles.get(exchange)
        documentOption.isDefined should be(true)
        exchangeOption.isDefined should be(true)
        val document = documentOption.get
        assert(document.text.equals("#%RAML 1.0"))
      }
    }
  }

  test("LanguageServer with custom document sync open file") {
    val factory = new WorkspaceManagerFactoryBuilder(
      new MockDiagnosticClientNotifier,
      logger,
      textDocumentSyncBuilder =
        Some((container: TextDocumentContainer, dependencies: List[TextListener], logger: Logger) =>
          new CustomTextDocumentSync(container, dependencies, logger))
    ).buildWorkspaceManagerFactory()

    val editorFiles = factory.container
    withServer[Assertion](buildServer(factory)) { server =>
      for {
        _ <- server.textDocumentSyncConsumer.didOpen(
          DidOpenTextDocumentParams(
            TextDocumentItem(apiUri, "raml", 0, "#%RAML 1.0")
          ))
        _ <- server.textDocumentSyncConsumer.didOpen(
          DidOpenTextDocumentParams(
            TextDocumentItem(exchange, "json", 0, "{}")
          ))
      } yield {
        val documentOption: Option[TextDocument] = editorFiles.get(apiUri)
        val exchangeOption: Option[TextDocument] = editorFiles.get(exchange)
        documentOption.isDefined should be(true)
        exchangeOption.isDefined should be(false)
        val document = documentOption.get
        assert(document.text.equals("#%RAML 1.0"))
      }
    }
  }

  def buildServer(factory: WorkspaceManagerFactory): LanguageServer =
    new LanguageServerBuilder(factory.documentManager,
                              factory.workspaceManager,
                              factory.configurationManager,
                              factory.resolutionTaskManager).build()

  override def rootPath: String = ""
}

class CustomTextDocumentSync(override val uriToEditor: TextDocumentContainer,
                             val dependencies: List[TextListener],
                             protected val logger: Logger)
    extends AlsTextDocumentSyncConsumer {

  override val `type`: TextDocumentSyncConfigType.type = TextDocumentSyncConfigType

  val internal = new TextDocumentManager(uriToEditor, dependencies, logger)
  override def didOpen(params: DidOpenTextDocumentParams): Future[Unit] =
    if (params.textDocument.uri.contains("exchange.json")) {
      Future.successful()
    } else {
      internal.didOpen(params)
    }
  override def didFocus(params: DidFocusParams): Future[Unit]               = internal.didFocus(params)
  override def didChange(params: DidChangeTextDocumentParams): Future[Unit] = internal.didChange(params)
  override def didClose(params: DidCloseTextDocumentParams): Future[Unit]   = internal.didClose(params)
  override def initialize(): Future[Unit]                                   = internal.initialize()
  override def applyConfig(
      config: Option[SynchronizationClientCapabilities]): Either[TextDocumentSyncKind, TextDocumentSyncOptions] =
    internal.applyConfig(config)
}
