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
import org.mulesoft.amfintegration.amfconfiguration.AmfConfigurationWrapper
import org.mulesoft.lsp.configuration.TraceKind

import scala.concurrent.ExecutionContext

class FilesInProjectNotificationTest extends LanguageServerBaseTest {

  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  def buildServer(alsClient: MockFilesInClientNotifier): LanguageServer = {

    val factoryBuilder: WorkspaceManagerFactoryBuilder =
      new WorkspaceManagerFactoryBuilder(new MockDiagnosticClientNotifier, logger)
    val filesInProjectManager = factoryBuilder.filesInProjectManager(alsClient)

    val factory: WorkspaceManagerFactory = factoryBuilder.buildWorkspaceManagerFactory()

    val builder =
      new LanguageServerBuilder(factory.documentManager,
                                factory.workspaceManager,
                                factory.configurationManager,
                                factory.resolutionTaskManager)
    builder.addInitializableModule(filesInProjectManager)
    builder.build()
  }

  override def rootPath: String = "workspace/"

  test("Receive simple dependency tree") {
    val alsClient: MockFilesInClientNotifier = new MockFilesInClientNotifier
    withServer(buildServer(alsClient)) { server =>
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

  test("Test empty schema in trait for visitors (NullPointerException)") {
    val alsClient: MockFilesInClientNotifier = new MockFilesInClientNotifier
    withServer(buildServer(alsClient)) { server =>
      for {
        _ <- server.initialize(
          AlsInitializeParams(None, Some(TraceKind.Off), rootUri = Some(s"${filePath("empty-trait-schema")}")))
        filesInProject <- alsClient.nextCall
      } yield {
        filesInProject.uris.size should be(1)
      }
    }
  }

  test("Open isolated file") {
    val alsClient: MockFilesInClientNotifier = new MockFilesInClientNotifier
    withServer(buildServer(alsClient)) { server =>
      val helperAmfConfiguration = AmfConfigurationWrapper()
      for {
        _ <- server.initialize(AlsInitializeParams(None, Some(TraceKind.Off), rootUri = Some(s"${filePath("ws1")}")))
        _ <- helperAmfConfiguration
          .fetchContent(s"${filePath("ws1/independent.raml")}")
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
