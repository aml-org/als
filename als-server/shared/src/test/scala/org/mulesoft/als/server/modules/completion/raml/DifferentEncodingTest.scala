package org.mulesoft.als.server.modules.completion.raml

import amf.client.remote.Content
import amf.internal.environment.Environment
import amf.internal.resource.ResourceLoader
import org.mulesoft.als.common.DirectoryResolver
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.protocol.configuration.AlsInitializeParams
import org.mulesoft.als.server.workspace.WorkspaceManager
import org.mulesoft.als.server.{LanguageServerBuilder, MockDiagnosticClientNotifier}
import org.mulesoft.lsp.configuration.TraceKind
import org.mulesoft.lsp.feature.common.{Position, TextDocumentIdentifier, TextDocumentItem}
import org.mulesoft.lsp.feature.completion.{CompletionParams, CompletionRequestType}
import org.mulesoft.lsp.textsync.DidOpenTextDocumentParams
import org.mulesoft.als.common.URIImplicits._

import scala.concurrent.{ExecutionContext, Future}

class DifferentEncodingTest extends RAMLSuggestionTestServer {

  def buildServer(root: String, ws: Map[String, String]): Future[(LanguageServer, WorkspaceManager)] = {
    val rs: ResourceLoader = new ResourceLoader {
      override def fetch(resource: String): Future[Content] =
        ws.get(resource)
          .map(c => new Content(c, resource))
          .map(Future.successful)
          .getOrElse(Future.failed(new Exception("File not found on custom ResourceLoader")))
      override def accepts(resource: String): Boolean = ws.keySet.contains(resource)
    }

    val dr: DirectoryResolver = new DirectoryResolver {
      private val allPaths = ws.keys

      override def exists(path: String): Future[Boolean] = Future.successful {
        allPaths.exists(p => p.startsWith(path))
      }

      override def readDir(path: String): Future[Seq[String]] = {
        def getNextPathBlock(p: String): String = {
          var i = p.indexOf('/')
          if (i < 0) i = p.length
          p.substring(0, i)
        }
        Future.successful {
          allPaths
            .filter(_.startsWith(path))
            .map(_.stripPrefix(path))
            .map(getNextPathBlock)
            .map(p => s"file://$p".toPath(platform))
            .toSeq
            .distinct
        }
      }

      override def isDirectory(path: String): Future[Boolean] = Future.successful {
        allPaths.exists(p =>
          p.startsWith(path) && (path.endsWith("/") || p.stripPrefix(path).headOption.contains('/')))
      }
    }

    val env = Environment().withLoaders(Seq(rs))

    val factory =
      new WorkspaceManagerFactoryBuilder(new MockDiagnosticClientNotifier, logger, env)
        .withDirectoryResolver(dr)
        .buildWorkspaceManagerFactory()
    val workspaceManager: WorkspaceManager = factory.workspaceManager
    val server =
      new LanguageServerBuilder(factory.documentManager, workspaceManager, factory.resolutionTaskManager)
        .addRequestModule(factory.completionManager)
        .build()

    server
      .initialize(AlsInitializeParams(None, Some(TraceKind.Off), rootUri = Some(root)))
      .andThen { case _ => server.initialized() }
      .map(_ => (server, workspaceManager))
  }

  override implicit val executionContext: ExecutionContext =
    ExecutionContext.Implicits.global

  val testSets: Set[TestEntry] = Set(
    TestEntry(
      "file:///r/root%20%281%29/api.raml",
      Set(("file:///r/root%20%281%29/api.raml", "file:///r/root%20(1)/api.raml")),
      Position(2, 15),
      Map(
        "file:///r/exchange.json" -> """{"main": "root (1)/api.raml"}""",
        "file:///r/root%20(1)/api.raml" ->
          """#%RAML 1.0
            |types:
            |  t: !include /""".stripMargin,
        "file:///r/root%20(1)/t.raml" ->
          """#%RAML 1.0 DataType
            |type: string""".stripMargin,
        "file:///r/root%20(1)/sub%20(1)/t.raml" ->
          """#%RAML 1.0 DataType
            |type: string""".stripMargin
      ),
      Set("/t.raml", "/sub (1)/")
    ),
    TestEntry(
      "file:///r/root%20%281%29/api.raml",
      Set(("file:///r/root%20%281%29/api.raml", "file:///r/root%20(1)/api.raml")),
      Position(2, 17),
      Map(
        "file:///r/exchange.json" -> """{"main": "root (1)/api.raml"}""",
        "file:///r/root%20(1)/api.raml" ->
          """#%RAML 1.0
            |types:
            |  t: !include ../""".stripMargin,
        "file:///r/root%20(1)/t.raml" ->
          """#%RAML 1.0 DataType
            |type: string""".stripMargin,
        "file:///r/root%20(1)/sub%20(1)/t.raml" ->
          """#%RAML 1.0 DataType
            |type: string""".stripMargin
      ),
      Set("exchange.json", "root (1)/")
    )
  )

  test("Check workspace suggestion with different encoding on URI - Project Root file inclusions and sub folder") {
    for {
      results <- Future.sequence {
        testSets.map { test =>
          for {
            (server, _) <- buildServer(test.root, test.ws)
            _ <- Future {
              test.filesToOpen.foreach { t =>
                server.textDocumentSyncConsumer.didOpen(
                  DidOpenTextDocumentParams(TextDocumentItem(t._1, "RAML", 0, test.ws.getOrElse(t._2, "")))
                )
              }
            }
            links <- {
              server
                .resolveHandler(CompletionRequestType)
                .map { h =>
                  h(CompletionParams(TextDocumentIdentifier(test.fileUri), test.position))
                    .map(completions => {
                      closeFile(server)(test.fileUri)

                      completions.left.value
                    })
                }
            }.getOrElse(Future.failed(new Exception("No completion handler")))
          } yield {
            (links, test.result)
          }
        }
      }
    } yield {
      assert(results.forall(t => t._1.map(_.label).toSet == t._2))
    }
  }

  case class TestEntry(fileUri: String,
                       filesToOpen: Set[(String, String)],
                       position: Position,
                       ws: Map[String, String],
                       result: Set[String],
                       root: String = "file:///r")
}
