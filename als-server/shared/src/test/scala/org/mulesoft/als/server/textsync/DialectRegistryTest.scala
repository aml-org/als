package org.mulesoft.als.server.textsync

import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.workspace.WorkspaceManager
import org.mulesoft.als.server.{LanguageServerBaseTest, LanguageServerBuilder, MockDiagnosticClientNotifier}
import org.mulesoft.amfintegration.amfconfiguration.AmfConfigurationWrapper

import java.util.UUID
import scala.concurrent.ExecutionContext

class DialectRegistryTest extends LanguageServerBaseTest {

  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  override def rootPath: String = ""

  def buildServer(diagnosticNotifier: MockDiagnosticClientNotifier)
    : (LanguageServer, AmfConfigurationWrapper, WorkspaceManager) = {

    val factory =
      new WorkspaceManagerFactoryBuilder(diagnosticNotifier, logger).buildWorkspaceManagerFactory()
    (new LanguageServerBuilder(factory.documentManager,
                               factory.workspaceManager,
                               factory.configurationManager,
                               factory.resolutionTaskManager)
       .addRequestModule(factory.structureManager)
       .build(),
     factory.amfConfiguration,
     factory.workspaceManager)
  }

  // todo: enable test after APIMF-3305 is adopted
  ignore("Remove old dialect after modifying it") {
    val diagnosticNotifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier(3000)

    val (server, amfConfiguration, workspaceManager) = buildServer(diagnosticNotifier)

    withServer(server) { server =>
      val content =
        """#%Dialect 1.0
          |dialect: Test
          |version: 1
          |
          |documents:
          |  root:
          |    encodes: main
          |
          |external:
          |  v2: http://fake.com/#
          |nodeMappings:
          |  main:
          |    classTerm: v2.main
          |    mappin:
          |      p:
          |        range: string""".stripMargin

      val url = "file:///dialect.yaml"

      for {
        _ <- openFileNotification(server)(url, content)
        _ <- workspaceManager.getUnit(url, UUID.randomUUID().toString)
        _ <- changeNotification(server)(url, content.replace("Test", "NewTest"), 1)
        _ <- workspaceManager.getLastUnit(url, UUID.randomUUID().toString)
      } yield {
        server.shutdown()
        amfConfiguration.dialects.map(_.nameAndVersion()) should contain("Test 1")
        amfConfiguration.dialects.map(_.nameAndVersion()) should contain("NewTest 1")
      }

    }
  }

}
