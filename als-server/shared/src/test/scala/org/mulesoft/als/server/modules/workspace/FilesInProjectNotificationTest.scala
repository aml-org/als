package org.mulesoft.als.server.modules.workspace

import org.mulesoft.als.server.modules.{WorkspaceManagerFactory, WorkspaceManagerFactoryBuilder}
import org.mulesoft.als.server.{
  LanguageServerBaseTest,
  LanguageServerBuilder,
  MockDiagnosticClientNotifier,
  MockFilesInClientNotifier
}
import org.mulesoft.lsp.configuration.{AlsInitializeParams, TraceKind}
import org.mulesoft.lsp.server.LanguageServer

import scala.concurrent.ExecutionContext

class FilesInProjectNotificationTest extends LanguageServerBaseTest {
  val alsClient = new MockFilesInClientNotifier

  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  override def buildServer(): LanguageServer = {

    val factoryBuilder: WorkspaceManagerFactoryBuilder =
      new WorkspaceManagerFactoryBuilder(new MockDiagnosticClientNotifier, logger)
    val filesInProjectManager = factoryBuilder.filesInProjectManager(alsClient)

    val factory: WorkspaceManagerFactory = factoryBuilder.buildWorkspaceManagerFactory()

    val builder = new LanguageServerBuilder(factory.documentManager, factory.workspaceManager)
    builder.addInitializableModule(filesInProjectManager)
    builder.build()
  }

  override def rootPath: String = "workspace/"

  test("Recieve simple dependency tree") {
    withServer { server =>
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
}
