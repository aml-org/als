package org.mulesoft.als.server.textsync

import amf.aml.client.scala.model.document.Dialect
import amf.aml.client.scala.model.domain.NodeMapping
import org.mulesoft.als.common.AmfConfigurationPatcher
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.protocol.configuration.AlsInitializeParams
import org.mulesoft.als.server.workspace.WorkspaceManager
import org.mulesoft.als.server.{LanguageServerBaseTest, LanguageServerBuilder, MockDiagnosticClientNotifier}
import org.mulesoft.amfintegration.amfconfiguration.EditorConfiguration
import org.mulesoft.lsp.configuration.TraceKind

import java.util.UUID
import scala.concurrent.ExecutionContext

class DialectRegistryTest extends LanguageServerBaseTest {

  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  override def rootPath: String = ""

  private val extraDialectPath = "file:///extra-dialect.yaml"
  private val extraDialectContent =
    """#%Dialect 1.0
      |dialect: Extra
      |version: 99
      |documents:
      |  root:
      |    encodes: main
      |nodeMappings:
      |  main:
      |    mapping:
      |      extraMapping:
      |        range: string""".stripMargin

  def buildServer(diagnosticNotifier: MockDiagnosticClientNotifier): (LanguageServer, WorkspaceManager) = {

    val factory =
      new WorkspaceManagerFactoryBuilder(
        diagnosticNotifier,
        logger,
        EditorConfiguration.withPlatformLoaders(
          Seq(AmfConfigurationPatcher.resourceLoaderForFile(extraDialectPath, extraDialectContent)))
      ).buildWorkspaceManagerFactory()
    (new LanguageServerBuilder(factory.documentManager,
                               factory.workspaceManager,
                               factory.configurationManager,
                               factory.resolutionTaskManager)
       .addRequestModule(factory.structureManager)
       .build(),
     factory.workspaceManager)
  }

  def checkProp(allDialects: Seq[Dialect], dialect: String, propName: String): Boolean =
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
        _  <- server.initialize(AlsInitializeParams(None, Some(TraceKind.Off), hotReload = Some(true)))
        _  <- openFileNotification(server)(url, content)
        _  <- workspaceManager.getUnit(url, UUID.randomUUID().toString)
        c1 <- workspaceManager.getWorkspace(url).flatMap(_.getConfigurationState)
        _  <- changeNotification(server)(url, content.replace("Test", "NewTest"), 1)
        _  <- workspaceManager.getLastUnit(url, UUID.randomUUID().toString)
        c2 <- workspaceManager.getWorkspace(url).flatMap(_.getConfigurationState)
      } yield {
        server.shutdown()
        c1.dialects.map(_.nameAndVersion()) should contain("Test 1")
        c2.dialects.map(_.nameAndVersion()) should not contain ("Test 1")
        c2.dialects.map(_.nameAndVersion()) should contain("NewTest 1")
      }

    }
  }

  test("Update dialect on version change") {
    val diagnosticNotifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier(3000)

    val (server, workspaceManager) = buildServer(diagnosticNotifier)

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
        _        <- server.initialize(AlsInitializeParams(None, Some(TraceKind.Off), hotReload = Some(true)))
        _        <- openFileNotification(server)(url, content)
        _        <- changeNotification(server)(url, content.replace("2", "3"), 2)
        _        <- workspaceManager.getLastUnit(url, UUID.randomUUID().toString)
        dialects <- workspaceManager.getWorkspace(url).flatMap(_.getConfigurationState).map(_.dialects)
      } yield {
        server.shutdown()
        dialects.map(_.nameAndVersion()) should not contain ("Test 2")
        dialects.map(_.nameAndVersion()) should contain("Test 3")
      }

    }
  }

  test("Update property on dialect change") {
    val diagnosticNotifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier(3000)

    val (server, workspaceManager) = buildServer(diagnosticNotifier)

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
        d1 <- workspaceManager.getWorkspace(url).flatMap(_.getConfigurationState).map(_.dialects)
        _  <- openFileNotification(server)(url, content)
        _  <- workspaceManager.getLastUnit(url, UUID.randomUUID().toString)
        d2 <- workspaceManager.getWorkspace(url).flatMap(_.getConfigurationState).map(_.dialects)
        _  <- changeNotification(server)(url, content2, 2)
        _  <- workspaceManager.getLastUnit(url, UUID.randomUUID().toString)
        d3 <- workspaceManager.getWorkspace(url).flatMap(_.getConfigurationState).map(_.dialects)
      } yield {
        server.shutdown()
        val nameAndVersion = "Test 2"
        assert(!d1.map(_.nameAndVersion()).exists(_.contains(nameAndVersion)))
        assert(checkProp(d2, nameAndVersion, "prop"))
        assert(checkProp(d3, nameAndVersion, "prop2"))
      }

    }
  }

  test("Keep manually registered dialects if not modified") {
    val diagnosticNotifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier(3000)

    val (server, workspaceManager) = buildServer(diagnosticNotifier)

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
        _  <- server.initialize(AlsInitializeParams(None, Some(TraceKind.Off), hotReload = Some(true)))
        d1 <- workspaceManager.getWorkspace(url).flatMap(_.getConfigurationState).map(_.dialects)
        _  <- changeWorkspaceConfiguration(server)(changeConfigArgs(None, Some(""), dialects = Set(extraDialectPath)))
        _  <- workspaceManager.getLastUnit(extraDialectPath, UUID.randomUUID().toString)
        d2 <- workspaceManager.getWorkspace(url).flatMap(_.getConfigurationState).map(_.dialects)
        _  <- openFileNotification(server)(url, content)
        _  <- workspaceManager.getLastUnit(url, UUID.randomUUID().toString)
        d3 <- workspaceManager.getWorkspace(url).flatMap(_.getConfigurationState).map(_.dialects)
        _  <- changeNotification(server)(url, content.replace("2", "3"), 2)
        _  <- workspaceManager.getLastUnit(url, UUID.randomUUID().toString)
        d4 <- workspaceManager.getWorkspace(url).flatMap(_.getConfigurationState).map(_.dialects)
      } yield {
        server.shutdown()
        d1.map(_.nameAndVersion()) should not contain ("Extra 99")
        d1.map(_.nameAndVersion()) should not contain ("Test 2")
        d1.map(_.nameAndVersion()) should not contain ("Test 3")
        d2.map(_.nameAndVersion()) should contain("Extra 99")
        d3.map(_.nameAndVersion()) should contain("Test 2")
        d3.map(_.nameAndVersion()) should contain("Extra 99")
        d4.map(_.nameAndVersion()) should not contain ("Test 2")
        d4.map(_.nameAndVersion()) should contain("Extra 99")
        d4.map(_.nameAndVersion()) should contain("Test 3")
      }

    }
  }

  test("Override manually registered dialects if modified") {
    val diagnosticNotifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier(3000)

    val (server, workspaceManager) = buildServer(diagnosticNotifier)

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

      val extraContent2 = extraDialectContent.replace("extraMapping", "extraMapping2")
      val url           = "file:///dialect1.yaml"

      for {
        _  <- server.initialize(AlsInitializeParams(None, Some(TraceKind.Off), hotReload = Some(true)))
        _  <- changeWorkspaceConfiguration(server)(changeConfigArgs(None, Some(""), dialects = Set(extraDialectPath)))
        _  <- workspaceManager.getLastUnit(extraDialectPath, UUID.randomUUID().toString)
        d1 <- workspaceManager.getWorkspace(url).flatMap(_.getConfigurationState).map(_.dialects)
        _  <- openFileNotification(server)(url, content)
        _  <- workspaceManager.getLastUnit(url, UUID.randomUUID().toString)
        d2 <- workspaceManager.getWorkspace(url).flatMap(_.getConfigurationState).map(_.dialects)
        _  <- changeNotification(server)(extraDialectPath, extraContent2, 2)
        _  <- workspaceManager.getLastUnit(url, UUID.randomUUID().toString)
        d3 <- workspaceManager.getWorkspace(url).flatMap(_.getConfigurationState).map(_.dialects)
      } yield {
        server.shutdown()
        val nameAndVersion = "Test 2"
        val extraName      = "Extra 99"
        d1.map(_.nameAndVersion()) should contain(extraName)
        assert(checkProp(d1, extraName, "extraMapping"))
        d2.map(_.nameAndVersion()) should contain(extraName)
        d2.map(_.nameAndVersion()) should contain(nameAndVersion)
        assert(checkProp(d2, extraName, "extraMapping"))

        d3.map(_.nameAndVersion()) should contain(extraName)
        d3.map(_.nameAndVersion()) should contain(nameAndVersion)
        assert(checkProp(d3, extraName, "extraMapping2"))
      }

    }
  }

}
