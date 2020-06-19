package org.mulesoft.als.server.workspace.highlights

import amf.client.remote.Content
import amf.internal.environment.Environment
import amf.internal.resource.ResourceLoader
import org.mulesoft.als.actions.rename.FindRenameLocations
import org.mulesoft.als.common.dtoTypes.{Position => DtoPosition}
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.protocol.configuration.AlsInitializeParams
import org.mulesoft.als.server.workspace.WorkspaceManager
import org.mulesoft.als.server.{LanguageServerBaseTest, LanguageServerBuilder, MockDiagnosticClientNotifier}
import org.mulesoft.lsp.configuration.TraceKind
import org.mulesoft.lsp.edit.{TextDocumentEdit, TextEdit, WorkspaceEdit}
import org.mulesoft.lsp.feature.RequestHandler
import org.mulesoft.lsp.feature.common.{
  Position,
  Range,
  TextDocumentIdentifier,
  TextDocumentItem,
  VersionedTextDocumentIdentifier
}
import org.mulesoft.lsp.feature.highlight.{
  DocumentHighlight,
  DocumentHighlightConfigType,
  DocumentHighlightKind,
  DocumentHighlightParams,
  DocumentHighlightRequestType
}
import org.mulesoft.lsp.textsync.DidOpenTextDocumentParams

import scala.concurrent.{ExecutionContext, Future}

class DocumentHighlightTest extends LanguageServerBaseTest {

  override implicit val executionContext: ExecutionContext =
    ExecutionContext.Implicits.global

  private val ws1 = Map(
    "file:///root/exchange.json" -> """{"main": "api.raml"}""",
    "file:///root/api.raml" ->
      """#%RAML 1.0
        |uses:
        |  lib: lib.raml
        |
        |/links:
        |  is:
        |    - lib.tr""".stripMargin,
    "file:///root/lib.raml" ->
      """#%RAML 1.0 Library
        |traits:
        |  tr:
        |    description: example trait
        |types:
        |  A: string
        |  C: A
        |  D: A""".stripMargin
  )

  val testSets: Set[TestEntry] = Set(
    TestEntry(
      "file:///root/lib.raml",
      Position(5, 3),
      ws1,
      Set(
        DocumentHighlight(Range(Position(6, 5), Position(6, 6)), DocumentHighlightKind.Text),
        DocumentHighlight(Range(Position(7, 5), Position(7, 6)), DocumentHighlightKind.Text)
      )
    )
  )

  private def createWSE(edits: Seq[(String, Seq[TextEdit])]): WorkspaceEdit =
    WorkspaceEdit(
      edits.groupBy(_._1).mapValues(_.flatMap(_._2)),
      edits.map(e => Left(TextDocumentEdit(VersionedTextDocumentIdentifier(e._1, None), e._2)))
    )

  test("Document Highlight tests") {
    for {
      results <- Future.sequence {
        testSets.map { test =>
          for {
            (server, _) <- buildServer(test.root, test.ws)
            highlights <- {
              server.textDocumentSyncConsumer.didOpen(
                DidOpenTextDocumentParams(
                  TextDocumentItem(
                    test.targetUri,
                    "",
                    0,
                    test.ws(test.targetUri)
                  )))
              val dhHandler: RequestHandler[DocumentHighlightParams, Seq[DocumentHighlight]] =
                server.resolveHandler(DocumentHighlightRequestType).get
              dhHandler(DocumentHighlightParams(TextDocumentIdentifier(test.targetUri), test.targetPosition))
            }
          } yield {
            (highlights, test.result)
          }
        }
      }
    } yield {
      assert(results.forall(t => t._1.toSet == t._2))
    }
  }

  case class TestEntry(targetUri: String,
                       targetPosition: Position,
                       ws: Map[String, String],
                       result: Set[DocumentHighlight],
                       root: String = "file:///root")

  def buildServer(root: String, ws: Map[String, String]): Future[(LanguageServer, WorkspaceManager)] = {
    val rs = new ResourceLoader {
      override def fetch(resource: String): Future[Content] =
        ws.get(resource)
          .map(c => new Content(c, resource))
          .map(Future.successful)
          .getOrElse(Future.failed(new Exception("File not found on custom ResourceLoader")))
      override def accepts(resource: String): Boolean =
        ws.keySet.contains(resource)
    }

    val env = Environment().withLoaders(Seq(rs))

    val factory =
      new WorkspaceManagerFactoryBuilder(new MockDiagnosticClientNotifier, logger, env)
        .buildWorkspaceManagerFactory()
    val workspaceManager: WorkspaceManager = factory.workspaceManager
    val server =
      new LanguageServerBuilder(factory.documentManager, workspaceManager, factory.resolutionTaskManager)
        .addRequestModule(factory.documentHighlightManager)
        .build()

    server
      .initialize(AlsInitializeParams(None, Some(TraceKind.Off), rootUri = Some(root)))
      .andThen { case _ => server.initialized() }
      .map(_ => (server, workspaceManager))
  }

  override def rootPath: String = ???
}
