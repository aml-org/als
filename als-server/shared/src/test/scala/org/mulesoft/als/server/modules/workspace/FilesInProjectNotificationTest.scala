package org.mulesoft.als.server.modules.workspace

import org.mulesoft.als.server.modules.{WorkspaceManagerFactory, WorkspaceManagerFactoryBuilder}
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.protocol.configuration.AlsInitializeParams
import org.mulesoft.als.server.{
  LanguageServerBaseTest,
  LanguageServerBuilder,
  MockDiagnosticClientNotifier,
  MockFilesInClientNotifier
}
import org.mulesoft.lsp.configuration.TraceKind

import scala.concurrent.{ExecutionContext, Future}

class FilesInProjectNotificationTest extends LanguageServerBaseTest {
  val alsClient = new MockFilesInClientNotifier

  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  def buildServer(): LanguageServer = {

    val factoryBuilder: WorkspaceManagerFactoryBuilder =
      new WorkspaceManagerFactoryBuilder(new MockDiagnosticClientNotifier, logger)
    val filesInProjectManager = factoryBuilder.filesInProjectManager(alsClient)

    val factory: WorkspaceManagerFactory = factoryBuilder.buildWorkspaceManagerFactory()

    val builder =
      new LanguageServerBuilder(factory.documentManager, factory.workspaceManager, factory.resolutionTaskManager)
    builder.addInitializableModule(filesInProjectManager)
    builder.build()
  }

  override def rootPath: String = "workspace/"

  test("Receive simple dependency tree") {
    withServer(buildServer()) { server =>
      for {
        _              <- server.initialize(AlsInitializeParams(None, Some(TraceKind.Off), rootUri = Some(s"${filePath("ws1")}")))
        filesInProject <- alsClient.nextCall
      } yield {
        filesInProject.uris.size should be(3)
        filesInProject.uris.exists(_.endsWith("api.raml")) should be(true)
        filesInProject.uris.exists(_.endsWith("independent.raml")) should be(false)
      }
    }
  }

  test("Open isolated file") {
    withServer(buildServer()) { server =>
      for {
        _ <- server.initialize(AlsInitializeParams(None, Some(TraceKind.Off), rootUri = Some(s"${filePath("ws1")}")))
        _ <- platform
          .resolve(s"${filePath("ws1/independent.raml")}")
          .map(c => openFile(server)(c.url, c.stream.toString))
        filesInProject <- alsClient.nextCall
      } yield {
        filesInProject.uris.size should be(3)
        filesInProject.uris.exists(_.endsWith("api.raml")) should be(true)
        filesInProject.uris.exists(_.endsWith("independent.raml")) should be(false)
      }
    }
  }
}
