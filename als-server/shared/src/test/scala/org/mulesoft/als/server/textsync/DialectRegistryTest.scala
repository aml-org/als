package org.mulesoft.als.server.textsync

import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.{LanguageServerBaseTest, LanguageServerBuilder, MockDiagnosticClientNotifier}
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.workspace.WorkspaceManager
import org.mulesoft.amfintegration.AmfInstance
import org.mulesoft.lsp.feature.common.TextDocumentIdentifier
import org.mulesoft.lsp.feature.documentsymbol.{DocumentSymbolParams, DocumentSymbolRequestType}

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

class DialectRegistryTest extends LanguageServerBaseTest {

  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  override def rootPath: String = ""

  def buildServer(diagnosticNotifier: MockDiagnosticClientNotifier): (LanguageServer, AmfInstance, WorkspaceManager) = {

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

  test("Remove old dialect after modifying it") {
    val diagnosticNotifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier(3000)

    val (server, amfInstance, workspaceManager) = buildServer(diagnosticNotifier)

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
        a <- workspaceManager.getUnit(url, UUID.randomUUID().toString)
        _ <- changeNotification(server)(url, content.replace("Test", "NewTest"), 1)
        b <- workspaceManager.getLastUnit(url, UUID.randomUUID().toString)
      } yield {
        server.shutdown()
        val registry = amfInstance.alsAmlPlugin.registry

        registry.knowsHeader("%Test1") should be(false)
        registry.knowsHeader("%NewTest1") should be(true)
      }

    }
  }

}
