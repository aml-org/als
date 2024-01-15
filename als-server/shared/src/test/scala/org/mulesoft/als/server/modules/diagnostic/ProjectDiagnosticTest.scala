package org.mulesoft.als.server.modules.diagnostic

import amf.core.client.common.validation.SeverityLevels.VIOLATION
import amf.core.client.scala.resource.ResourceLoader
import amf.core.client.scala.validation.AMFValidationResult
import org.mulesoft.als.common.AmfConfigurationPatcher
import org.mulesoft.als.server.client.scala.LanguageServerBuilder
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.protocol.configuration.AlsInitializeParams
import org.mulesoft.als.server.{Flaky, LanguageServerBaseTest, MockDiagnosticClientNotifier}
import org.mulesoft.amfintegration.amfconfiguration.EditorConfiguration
import org.mulesoft.lsp.configuration.TraceKind

import scala.concurrent.{ExecutionContext, Future}

class ProjectDiagnosticTest extends LanguageServerBaseTest {

  override implicit val executionContext: ExecutionContext =
    ExecutionContext.Implicits.global

  override def rootPath: String = ""

  private val apiPath   = s"file:///api.raml"
  private val otherPath = s"file:///exchange.json"
  private val content = """#%RAML 1.0
                  |title: api""".stripMargin

  private val rl = AmfConfigurationPatcher.resourceLoaderForFile(apiPath, content)

  def buildServer(
      diagnosticNotifier: MockDiagnosticClientNotifier,
      rls: Seq[ResourceLoader],
      error: Option[AMFValidationResult] = None,
      newCachingLogic: Boolean
  ): (LanguageServer, ProjectErrorConfigurationProvider) = {
    val editorConfig = EditorConfiguration.withoutPlatformLoaders(rls)
    val provider = new ProjectErrorConfigurationProvider(
      editorConfig,
      logger,
      error.getOrElse(AMFValidationResult("Error loading project", VIOLATION, "", None, "2", None, None, None)),
      newCachingLogic
    )
    val builder =
      new WorkspaceManagerFactoryBuilder(
        diagnosticNotifier,
        editorConfig,
        projectConfigurationProvider = Some(provider)
      )
    val dm      = builder.buildDiagnosticManagers()
    val factory = builder.buildWorkspaceManagerFactory()
    val b = new LanguageServerBuilder(
      factory.documentManager,
      factory.workspaceManager,
      factory.configurationManager,
      factory.resolutionTaskManager
    )
    dm.foreach(m => b.addInitializableModule(m))
    (b.build(), provider)
  }

  test("Report project errors") {
    val diagnosticNotifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier(7000)

    withServer(
      buildServer(diagnosticNotifier, Seq(rl), newCachingLogic = true)._1,
      AlsInitializeParams(None, Some(TraceKind.Off), rootPath = Some("file:///"))
    ) { server =>
      for {
        _  <- setMainFile(server)("file:///", "api.raml")
        d1 <- diagnosticNotifier.nextCall
      } yield {
        server.shutdown()
        assert(d1.diagnostics.nonEmpty && d1.uri == apiPath)
        assert(d1.diagnostics.exists(d => d.message == "Error loading project"))
      }
    }
  }

  test("Report project errors outside known files", Flaky) {
    val diagnosticNotifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier(7000)

    val error = AMFValidationResult("Error loading project", VIOLATION, "", None, "", None, Some(otherPath), None)

    withServer(
      buildServer(diagnosticNotifier, Seq(rl), Some(error), newCachingLogic = true)._1,
      AlsInitializeParams(None, Some(TraceKind.Off), rootPath = Some("file:///"))
    ) { server =>
      for {
        _  <- setMainFile(server)("file:///", "api.raml")
        d1 <- diagnosticNotifier.nextCall
        d2 <- diagnosticNotifier.nextCall
      } yield {
        server.shutdown()
        val diagnostic = Seq(d1, d2).find(_.uri == otherPath)
        assert(diagnostic.isDefined)
        assert(diagnostic.get.diagnostics.exists(d => d.message == "Error loading project"))
      }
    }
  }

  test("Clean project errors", Flaky) {
    val diagnosticNotifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier(7000)

    val (server, provider) = buildServer(diagnosticNotifier, Seq(rl), newCachingLogic = true)
    withServer(server, AlsInitializeParams(None, Some(TraceKind.Off), rootPath = Some("file:///"))) { server =>
      for {
        _  <- setMainFile(server)("file:///", "api.raml")
        d1 <- diagnosticNotifier.nextCall
        _  <- Future(provider.setReportError(false))
        _  <- setMainFile(server)("file:///", "api.raml")
        d2 <- diagnosticNotifier.nextCall
      } yield {
        server.shutdown()
        assert(d1.diagnostics.nonEmpty && d1.uri == apiPath)
        assert(d1.diagnostics.exists(d => d.message == "Error loading project"))
        assert(d2.uri == apiPath)
        assert(d2.diagnostics.isEmpty)
      }
    }
  }

  test("Clean project errors on external files", Flaky) {
    val diagnosticNotifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier(7000)

    val error = AMFValidationResult("Error loading project", VIOLATION, "", None, "", None, Some(otherPath), None)
    val (server, provider) = buildServer(diagnosticNotifier, Seq(rl), Some(error), newCachingLogic = true)
    withServer(server, AlsInitializeParams(None, Some(TraceKind.Off), rootPath = Some("file:///"))) { server =>
      for {
        _  <- setMainFile(server)("file:///", "api.raml")
        d1 <- diagnosticNotifier.nextCall
        d2 <- diagnosticNotifier.nextCall
        _  <- Future(provider.setReportError(false))
        _  <- setMainFile(server)("file:///", "api.raml")
        d3 <- diagnosticNotifier.nextCall
        d4 <- diagnosticNotifier.nextCall
      } yield {
        server.shutdown()
        val diagnostic = Seq(d1, d2).find(_.uri == otherPath)
        assert(diagnostic.isDefined)
        assert(diagnostic.get.diagnostics.exists(d => d.message == "Error loading project"))

        val diagnostic2 = Seq(d3, d4).find(_.uri == otherPath)
        assert(diagnostic2.isDefined)
        assert(diagnostic2.get.diagnostics.isEmpty)

      }
    }
  }

}
