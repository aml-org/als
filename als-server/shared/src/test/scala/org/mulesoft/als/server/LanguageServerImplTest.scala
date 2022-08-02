package org.mulesoft.als.server

import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.als.logger.Logger
import org.mulesoft.als.server.client.scala.LanguageServerBuilder
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
import org.mulesoft.lsp.feature.common.{TextDocumentIdentifier, TextDocumentItem}
import org.mulesoft.lsp.feature.completion.{CompletionParams, CompletionRequestType}
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
import org.mulesoft.lsp.feature.common.{Position => LspPosition, Range => LspRange}

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
          )
        )
        _ <- server.textDocumentSyncConsumer.didOpen(
          DidOpenTextDocumentParams(
            TextDocumentItem(exchange, "json", 0, "{}")
          )
        )
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
          new CustomTextDocumentSync(container, dependencies, logger)
        )
    ).buildWorkspaceManagerFactory()

    val editorFiles = factory.container
    withServer[Assertion](buildServer(factory)) { server =>
      for {
        _ <- server.textDocumentSyncConsumer.didOpen(
          DidOpenTextDocumentParams(
            TextDocumentItem(apiUri, "raml", 0, "#%RAML 1.0")
          )
        )
        _ <- server.textDocumentSyncConsumer.didOpen(
          DidOpenTextDocumentParams(
            TextDocumentItem(exchange, "json", 0, "{}")
          )
        )
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

//  test("LanguageServer graphql test") {
//
//    val graphql =
//      "\"\"\"\nschema documentation\n\"\"\" \nschema {\n    query: Query\n}\n\n\ntype Query {\n    allPersons(last: Int): [Person!]!\n}\n\n\n\"\"\"\ntype documentation\n\"\"\"\ntype Mutation {\n    createPerson(name: String!, age: Int!): Person!\n}\n\ntype Subscription {\n    newPerson: Person!\n}\n\ntype Person {\n    name: String!\n    age: Int!\n    posts: [Post!]!\n}\n\ntype Post {\n    title: String!\n    author: Person!\n}"
//    val factory = {
//      new WorkspaceManagerFactoryBuilder(new MockDiagnosticClientNotifier, logger).buildWorkspaceManagerFactory()
//    }
//    val filePath    = "file://api.graphql"
//    val editorFiles = factory.container
//    withServer[Assertion](buildServer(factory)) { server =>
//      for {
//        _ <- server.textDocumentSyncConsumer.didOpen(
//          DidOpenTextDocumentParams(
//            TextDocumentItem(filePath, "graphql", 0, graphql)
//          ))
//        completions <- {
//          val completionHandler = server.resolveHandler(CompletionRequestType).value
//
//          completionHandler(CompletionParams(TextDocumentIdentifier(filePath), LspPosition(6, 0)))
//            .flatMap(completions => {
//              closeFile(server)(filePath)
//                .map(_ => completions.left.get)
//            })
//        }
//      } yield {
//        completions.nonEmpty shouldBe (true)
//      }
//    }
//  }

  def buildServer(factory: WorkspaceManagerFactory): LanguageServer =
    new LanguageServerBuilder(
      factory.documentManager,
      factory.workspaceManager,
      factory.configurationManager,
      factory.resolutionTaskManager
    ).build()

  override def rootPath: String = ""
}

class CustomTextDocumentSync(
    override val uriToEditor: TextDocumentContainer,
    val dependencies: List[TextListener],
    protected val logger: Logger
) extends AlsTextDocumentSyncConsumer {

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
      config: Option[SynchronizationClientCapabilities]
  ): Either[TextDocumentSyncKind, TextDocumentSyncOptions] =
    internal.applyConfig(config)
}
