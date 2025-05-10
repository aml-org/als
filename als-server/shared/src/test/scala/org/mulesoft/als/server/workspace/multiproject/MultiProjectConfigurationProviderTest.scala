package org.mulesoft.als.server.workspace.multiproject

import org.mulesoft.als.common.PlatformDirectoryResolver
import org.mulesoft.als.server.client.scala.LanguageServerBuilder
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.protocol.configuration.AlsInitializeParams
import org.mulesoft.als.server.textsync.TextDocumentContainer
import org.mulesoft.als.server.{LanguageServerBaseTest, MockDiagnosticClientNotifier, MockFilesInClientNotifier}
import org.mulesoft.amfintegration.amfconfiguration.EditorConfiguration
import org.mulesoft.lsp.configuration.TraceKind
import org.scalatest.compatible.Assertion

import scala.concurrent.ExecutionContext

class MultiProjectConfigurationProviderTest extends LanguageServerBaseTest {

  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  override def rootPath: String = "workspace/multi-project"

  def buildServer(alsClient: Option[MockFilesInClientNotifier] = None): LanguageServer = {
    val container           = TextDocumentContainer()
    val editorConfiguration = EditorConfiguration()
    val builder =
      new WorkspaceManagerFactoryBuilder(
        new MockDiagnosticClientNotifier,
        editorConfiguration,
        Option(TestMultiProjectConfigurationProvider(container, editorConfiguration, new PlatformDirectoryResolver(container.platform)))
      )


    val maybeInProjectManager = alsClient.map(builder.filesInProjectManager)
    val factory = builder.buildWorkspaceManagerFactory()

    val b = new LanguageServerBuilder(
      factory.documentManager,
      factory.workspaceManager,
      factory.configurationManager,
      factory.resolutionTaskManager
    )
    maybeInProjectManager.foreach(b.addInitializableModule)
    b.build()
  }

  val defaultInitializationParams: AlsInitializeParams = AlsInitializeParams(None, Some(TraceKind.Off), rootPath = Some(filePath("")))

  test("Configuration provider reads multiple projects in folder") {
    val totalProjects = 4
    val provider = TestMultiProjectConfigurationProvider(TextDocumentContainer(), EditorConfiguration(), new PlatformDirectoryResolver(platform))
    for{
      projectPaths <- provider.getProjectsFromFolder(filePath(""))
    } yield {
      (0 until totalProjects).map(1+).map(r => s"project$r").foreach(name => assert(projectPaths.map(_.split("/").last).contains(name)))
      projectPaths.length shouldBe totalProjects
    }
  }

  test("initialize correct projects for rootPath") {
    withServer[Assertion](buildServer(), defaultInitializationParams) { server =>
        server.workspaceFolders().length shouldBe 4
    }
  }

  test("get correct main file for project") {
    val notifier = new MockFilesInClientNotifier
    withServer[Assertion](buildServer(Some(notifier)), defaultInitializationParams) { server =>
      for {
        _ <- setMainFile(server)(filePath("project1"), "api.raml")
        filesInProject <- notifier.nextCall
      } yield {
        filesInProject.uris.size shouldBe 1
        filesInProject.uris.head.endsWith("project1/api.raml") shouldBe true
      }
    }
  }
}
