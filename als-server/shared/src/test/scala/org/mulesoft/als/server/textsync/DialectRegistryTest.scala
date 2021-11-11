package org.mulesoft.als.server.textsync

import amf.aml.client.scala.model.document.Dialect
import amf.aml.client.scala.model.domain.NodeMapping
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.protocol.configuration.AlsInitializeParams
import org.mulesoft.als.server.workspace.WorkspaceManager
import org.mulesoft.als.server.{LanguageServerBaseTest, LanguageServerBuilder, MockDiagnosticClientNotifier}
import org.mulesoft.lsp.configuration.TraceKind

import java.util.UUID
import scala.concurrent.ExecutionContext

class DialectRegistryTest extends LanguageServerBaseTest {

  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  override def rootPath: String = ""

  def buildServer(diagnosticNotifier: MockDiagnosticClientNotifier): (LanguageServer, WorkspaceManager) = {

    val factory =
      new WorkspaceManagerFactoryBuilder(diagnosticNotifier, logger).buildWorkspaceManagerFactory()
    (new LanguageServerBuilder(factory.documentManager,
                               factory.workspaceManager,
                               factory.configurationManager,
                               factory.resolutionTaskManager)
       .addRequestModule(factory.structureManager)
       .build(),
     factory.workspaceManager)
  }

  test("Remove old dialect after modifying it") {
    val diagnosticNotifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier(3000)

    val (server, workspaceManager) = buildServer(diagnosticNotifier)

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
        _ <- server.initialize(AlsInitializeParams(None, Some(TraceKind.Off), hotReload = Some(true)))
        _      <- openFileNotification(server)(url, content)
        _      <- workspaceManager.getUnit(url, UUID.randomUUID().toString)
        _      <- changeNotification(server)(url, content.replace("Test", "NewTest"), 1)
        _      <- workspaceManager.getLastUnit(url, UUID.randomUUID().toString)
        config <- workspaceManager.getWorkspace(url).flatMap(_.getConfigurationState)
      } yield {
        server.shutdown()
        config.dialects.map(_.nameAndVersion()) should contain("Test 1")
        config.dialects.map(_.nameAndVersion()) should contain("NewTest 1")
      }

    }
  }

  test("Change version") {
    val diagnosticNotifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier(3000)

    val (server, amfConfiguration, workspaceManager) = buildServer(diagnosticNotifier)

    withServer(server) { server =>
      val content =
        """#%Dialect 1.0
          |dialect: Test
          |version: 2
          |nodeMappings:
          |  main:
          |    classTerm: v2.main
          |    mappin:
          |      p:
          |        range: string""".stripMargin

      val url = "file:///dialect1.yaml"

      for {
        _ <- server.initialize(AlsInitializeParams(None, Some(TraceKind.Off), hotReload = Some(true)))
        _ <- openFileNotification(server)(url, content)
        _ <- changeNotification(server)(url, content.replace("2", "3"), 2)
        _ <- workspaceManager.getLastUnit(url, UUID.randomUUID().toString)
      } yield {
        server.shutdown()
        amfConfiguration.dialects.map(_.nameAndVersion()) should contain("Test 2")
        amfConfiguration.dialects.map(_.nameAndVersion()) should contain("Test 3")
      }

    }
  }

  test("Change property") {
    val diagnosticNotifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier(3000)

    val (server, _, workspaceManager) = buildServer(diagnosticNotifier)

    def checkProp(allDialects: Set[Dialect], dialect: String, propName: String): Boolean =
      allDialects
        .find(_.nameAndVersion().contains(dialect))
        .exists(
          _.declares
            .flatMap {
              case nm: NodeMapping => nm.propertiesMapping()
              case _               => Seq.empty
            }
            .map(_.name().value())
            .contains(propName))

    withServer(server) { server =>
      val content =
        """#%Dialect 1.0
          |dialect: Test
          |version: 2
          |nodeMappings:
          |  main:
          |    classTerm: v2.main
          |    mapping:
          |      prop: string""".stripMargin

      val content2 = content.replace("prop", "prop2")

      val url = "file:///dialect1.yaml"

      for {
        _  <- server.initialize(AlsInitializeParams(None, Some(TraceKind.Off), hotReload = Some(true)))
        d1 <- workspaceManager.getWorkspace(url).map(_.registeredDialects)
        _  <- openFileNotification(server)(url, content)
        _  <- workspaceManager.getLastUnit(url, UUID.randomUUID().toString)
        d2 <- workspaceManager.getWorkspace(url).map(_.registeredDialects)
        _  <- changeNotification(server)(url, content2, 2)
        _  <- workspaceManager.getLastUnit(url, UUID.randomUUID().toString)
        d3 <- workspaceManager.getWorkspace(url).map(_.registeredDialects)
      } yield {
        server.shutdown()
        val nameAndVersion = "Test 2"
        assert(!d1.map(_.nameAndVersion()).exists(_.contains(nameAndVersion)))
        assert(checkProp(d2, nameAndVersion, "prop"))
        assert(checkProp(d3, nameAndVersion, "prop2"))
      }

    }
  }

}
