package org.mulesoft.als.server.workspace.fileusage

import amf.client.remote.Content
import amf.internal.environment.Environment
import amf.internal.resource.ResourceLoader
import org.mulesoft.als.server.feature.fileusage.FileUsageRequestType
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.protocol.configuration.AlsInitializeParams
import org.mulesoft.als.server.workspace.WorkspaceManager
import org.mulesoft.als.server.{LanguageServerBaseTest, LanguageServerBuilder, MockDiagnosticClientNotifier}
import org.mulesoft.lsp.configuration.TraceKind
import org.mulesoft.lsp.feature.common.{Location, TextDocumentIdentifier}
import org.scalatest.Assertion

import scala.concurrent.{ExecutionContext, Future}

trait ServerFileUsageTest extends LanguageServerBaseTest {

  override implicit val executionContext: ExecutionContext =
    ExecutionContext.Implicits.global

  override def rootPath: String = "actions/fileusage"

  def buildServer(root: String, ws: Map[String, String]): Future[(LanguageServer, WorkspaceManager)] = {
    val rs = new ResourceLoader {
      override def fetch(resource: String): Future[Content] =
        ws.get(resource)
          .map(c => new Content(c, resource))
          .map(Future.successful)
          .getOrElse(Future.failed(new Exception("File not found on custom ResourceLoader")))
      override def accepts(resource: String): Boolean = ws.keySet.contains(resource)
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
        .addRequestModule(factory.fileUsageManager)
        .build()

    server
      .initialize(AlsInitializeParams(None, Some(TraceKind.Off), rootUri = Some(root)))
      .andThen { case _ => server.initialized() }
      .map(_ => (server, workspaceManager))
  }

  def runTest(root: String,
              ws: Map[String, String],
              searchedUri: String,
              expectedResult: Set[Location]): Future[Assertion] =
    for {
      (server, _) <- buildServer(root, ws)
      result      <- getServerFileUsage(server, searchedUri)
    } yield {
      assert(result.toSet == expectedResult)
    }

  def getServerFileUsage(server: LanguageServer, searchedUri: String): Future[Seq[Location]] =
    server
      .resolveHandler(FileUsageRequestType)
      .map { _(TextDocumentIdentifier(searchedUri)) }
      .getOrElse(Future.failed(new Exception("No handler found for FileUsage")))
}
