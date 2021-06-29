package org.mulesoft.als.server.modules.foldings

import amf.core.client.common.remote.Content
import amf.core.client.scala.resource.ResourceLoader
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.protocol.configuration.AlsInitializeParams
import org.mulesoft.als.server.workspace.WorkspaceManager
import org.mulesoft.als.server.{LanguageServerBaseTest, LanguageServerBuilder, MockDiagnosticClientNotifier}
import org.mulesoft.lsp.configuration.TraceKind
import org.mulesoft.lsp.feature.RequestHandler
import org.mulesoft.lsp.feature.common.{TextDocumentIdentifier, TextDocumentItem}
import org.mulesoft.lsp.feature.folding.{FoldingRange, FoldingRangeParams, FoldingRangeRequestType}
import org.mulesoft.lsp.textsync.DidOpenTextDocumentParams

import scala.concurrent.{ExecutionContext, Future}

class FoldingRangeTest extends LanguageServerBaseTest {

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
      ws1,
      Seq(
        FoldingRange(1, Some(0), 7, Some(6), None),
        FoldingRange(1, Some(7), 3, Some(30), None),
        FoldingRange(2, Some(5), 3, Some(30), None),
        FoldingRange(4, Some(6), 7, Some(6), None)
      )
    ),
    TestEntry(
      "file:///root/api.raml",
      ws1,
      Seq(
        FoldingRange(1, Some(0), 6, Some(12), None),
        FoldingRange(1, Some(5), 2, Some(15), None),
        FoldingRange(4, Some(7), 6, Some(12), None),
        FoldingRange(5, Some(5), 6, Some(12), None)
      )
    )
  )

  test("Folding Range tests") {
    for {
      results <- Future.sequence {
        testSets.map { test =>
          for {
            (server, _) <- buildServer(test.root, test.ws)
            folds <- {
              server.textDocumentSyncConsumer.didOpen(
                DidOpenTextDocumentParams(
                  TextDocumentItem(
                    test.targetUri,
                    "",
                    0,
                    test.ws(test.targetUri)
                  )))
              val dhHandler: RequestHandler[FoldingRangeParams, Seq[FoldingRange]] =
                server.resolveHandler(FoldingRangeRequestType).get
              dhHandler(FoldingRangeParams(TextDocumentIdentifier(test.targetUri)))
            }
          } yield {
            (folds, test.result, test.targetUri)
          }
        }
      }
    } yield {
      results.foreach { t =>
        val (result, expected, targetUri) = t
        val notExpected                   = result.toSet -- expected.toSet
        val notFound                      = expected.toSet -- result.toSet
        if (notExpected.nonEmpty) {
          notExpected.foreach(println)
          fail(s"Not expected for $targetUri:\n${notExpected.mkString("\n\t")}")
        }
        if (notFound.nonEmpty) {
          notFound.foreach(println)
          fail(s"Not found for $targetUri:\n${notFound.mkString("\n\t")}")
        }

      }

      results.foreach(t => assert(t._1.size == t._2.size))
      results.foreach(t => assert(t._1 == t._2))
      succeed
    }
  }

  case class TestEntry(targetUri: String,
                       ws: Map[String, String],
                       result: Seq[FoldingRange],
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

    val factory =
      new WorkspaceManagerFactoryBuilder(new MockDiagnosticClientNotifier, logger, Seq(rs))
        .buildWorkspaceManagerFactory()
    val workspaceManager: WorkspaceManager = factory.workspaceManager
    val server =
      new LanguageServerBuilder(factory.documentManager,
                                workspaceManager,
                                factory.configurationManager,
                                factory.resolutionTaskManager)
        .addRequestModule(factory.foldingRangeManager)
        .build()

    server
      .initialize(AlsInitializeParams(None, Some(TraceKind.Off), rootUri = Some(root)))
      .andThen { case _ => server.initialized() }
      .map(_ => (server, workspaceManager))
  }

  override def rootPath: String = ???
}
