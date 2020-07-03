package org.mulesoft.als.server.workspace.rename

import amf.client.remote.Content
import amf.internal.environment.Environment
import amf.internal.resource.ResourceLoader
import org.mulesoft.als.actions.rename.FindRenameLocations
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.{LanguageServerBaseTest, LanguageServerBuilder, MockDiagnosticClientNotifier}
import org.mulesoft.als.server.protocol.configuration.AlsInitializeParams
import org.mulesoft.als.server.workspace.WorkspaceManager
import org.mulesoft.lsp.configuration.TraceKind
import org.mulesoft.lsp.edit.{TextDocumentEdit, TextEdit, WorkspaceEdit}
import org.mulesoft.lsp.feature.common.{Position, Range, VersionedTextDocumentIdentifier}
import org.mulesoft.als.common.dtoTypes.{Position => DtoPosition}

import scala.concurrent.{ExecutionContext, Future}

class RenameTest extends LanguageServerBaseTest {

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
      Position(2, 3),
      "trait1",
      ws1,
      createWSE(
        Seq(
          ("file:///root/api.raml",
           Seq(
             TextEdit(Range(Position(6, 6), Position(6, 12)), "lib.trait1")
           )),
          ("file:///root/lib.raml",
           Seq(
             TextEdit(Range(Position(2, 2), Position(2, 4)), "trait1")
           ))
        ))
    ),
    TestEntry(
      "file:///root/lib.raml",
      Position(5, 3),
      "type1",
      ws1,
      createWSE(
        Seq(
          ("file:///root/lib.raml",
           Seq(
             TextEdit(Range(Position(6, 5), Position(6, 6)), "type1"),
             TextEdit(Range(Position(7, 5), Position(7, 6)), "type1"),
             TextEdit(Range(Position(5, 2), Position(5, 3)), "type1")
           ))
        ))
    )
  )

  private def createWSE(edits: Seq[(String, Seq[TextEdit])]): WorkspaceEdit =
    WorkspaceEdit(
      edits.groupBy(_._1).mapValues(_.flatMap(_._2)),
      edits.map(e => Left(TextDocumentEdit(VersionedTextDocumentIdentifier(e._1, None), e._2)))
    )

  test("No handler") {
    for {
      results <- Future.sequence {
        testSets.map { test =>
          for {
            (_, wsManager) <- buildServer(test.root, test.ws)
            renames <- FindRenameLocations
              .changeDeclaredName(test.targetUri,
                                  DtoPosition(test.targetPosition),
                                  test.newName,
                                  wsManager.getRelationships(test.targetUri, ""))
          } yield {
            (renames, test.result)
          }
        }
      }
    } yield {
      assert(results.forall(t => equalWSE(t._1, t._2)))
    }
  }

  private def equalWSE(a: WorkspaceEdit, b: WorkspaceEdit): Boolean =
    a.changes.mapValues(_.toSet) == b.changes.mapValues(_.toSet) &&
      a.documentChanges.map(_.left) == b.documentChanges.map(_.left)

  case class TestEntry(targetUri: String,
                       targetPosition: Position,
                       newName: String,
                       ws: Map[String, String],
                       result: WorkspaceEdit,
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
      new LanguageServerBuilder(factory.documentManager,
                                workspaceManager,
                                factory.configurationManager,
                                factory.resolutionTaskManager)
        .addRequestModule(factory.renameManager)
        .build()

    server
      .initialize(AlsInitializeParams(None, Some(TraceKind.Off), rootUri = Some(root)))
      .andThen { case _ => server.initialized() }
      .map(_ => (server, workspaceManager))
  }

  override def rootPath: String = ???
}
