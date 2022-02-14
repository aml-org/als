package org.mulesoft.als.server.modules.completion.raml

import amf.core.client.common.remote.Content
import amf.core.client.scala.resource.ResourceLoader
import org.mulesoft.als.common.DirectoryResolver
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.protocol.configuration.AlsInitializeParams
import org.mulesoft.als.server.workspace.WorkspaceManager
import org.mulesoft.als.server.MockDiagnosticClientNotifier
import org.mulesoft.lsp.configuration.TraceKind
import org.mulesoft.lsp.feature.common.{Position, TextDocumentIdentifier, TextDocumentItem}
import org.mulesoft.lsp.feature.completion.{CompletionParams, CompletionRequestType}
import org.mulesoft.lsp.textsync.DidOpenTextDocumentParams
import org.mulesoft.als.common.URIImplicits._
import org.mulesoft.als.server.client.scala.LanguageServerBuilder
import org.mulesoft.amfintegration.amfconfiguration.EditorConfiguration

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
    val global = EditorConfiguration.withPlatformLoaders(Seq(rs))
    val factory =
      new WorkspaceManagerFactoryBuilder(new MockDiagnosticClientNotifier, logger, global)
        .withDirectoryResolver(dr)
        .buildWorkspaceManagerFactory()
    val workspaceManager: WorkspaceManager = factory.workspaceManager
    val server =
      new LanguageServerBuilder(factory.documentManager,
                                workspaceManager,
                                factory.configurationManager,
                                factory.resolutionTaskManager)
        .addRequestModule(factory.completionManager)
        .build()

    server
      .testInitialize(AlsInitializeParams(None, Some(TraceKind.Off), rootUri = Some(root)))
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
      Set("root (1)/")
    ),
    TestEntry( // Isolated path in a workspace which contains a Project
      "file:///r/t.raml",
      Set(("file:///r/t.raml", "file:///r/t.raml")),
      Position(2, 15),
      Map(
        "file:///r/root%20(1)/api.raml" ->
          """#%RAML 1.0
            |types:
            |  t: !include ../""".stripMargin,
        "file:///r/root%20(1)/t.raml" ->
          """#%RAML 1.0 DataType
            |type: string""".stripMargin,
        "file:///r/root%20(1)/sub%20(1)/t.raml" ->
          """#%RAML 1.0 DataType
            |type: string""".stripMargin,
        "file:///r/t.raml" ->
          """#%RAML 1.0
            |types:
            |  t: !include /""".stripMargin
      ),
      Set("/root (1)/")
    )
  )

  test("Check workspace suggestion with different encoding on URI - Project Root file inclusions and sub folder") {
    for {
      results <- Future.sequence {
        testSets.map { test =>
          for {
            (server, _) <- buildServer(test.root, test.ws)
            _ <- Future.sequence {
              test.filesToOpen.map { t =>
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
                    .flatMap(completions => {
                      closeFile(server)(test.fileUri)
                        .map(_ => completions.left.value)
                    })
                }
            }.getOrElse(Future.failed(new Exception("No completion handler")))
          } yield {
            (links, test.result)
          }
        }
      }
    } yield {
      results.foreach(t => assert(t._1.map(_.label).toSet == t._2))
      succeed
    }
  }

  case class TestEntry(fileUri: String,
                       filesToOpen: Set[(String, String)],
                       position: Position,
                       ws: Map[String, String],
                       result: Set[String],
                       root: String = "file:///r")
}
