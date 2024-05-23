package org.mulesoft.als.server.modules.serialization

import amf.core.client.common.remote.Content
import amf.core.client.scala.resource.ResourceLoader
import org.mulesoft.als.common.Spy
import org.mulesoft.als.common.URIImplicits.StringUriImplicits
import org.mulesoft.als.common.diff.FileAssertionTest
import org.mulesoft.als.server._
import org.mulesoft.als.server.client.platform.ClientNotifier
import org.mulesoft.als.server.client.scala.LanguageServerBuilder
import org.mulesoft.als.server.feature.serialization.SerializationClientCapabilities
import org.mulesoft.als.server.modules.configuration.WorkspaceConfigurationManager
import org.mulesoft.als.server.modules.{WorkspaceManagerFactory, WorkspaceManagerFactoryBuilder}
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.protocol.configuration.{AlsClientCapabilities, AlsInitializeParams}
import org.mulesoft.als.server.workspace.ChangesWorkspaceConfiguration
import org.mulesoft.amfintegration.amfconfiguration.{ALSConfigurationState, EditorConfiguration}
import org.mulesoft.lsp.configuration.TraceKind
import org.mulesoft.lsp.feature.diagnostic.PublishDiagnosticsParams
import org.mulesoft.lsp.feature.telemetry.TelemetryMessage
import org.yaml.builder.{DocBuilder, JsonOutputBuilder}

import java.io.StringWriter
import scala.concurrent.{ExecutionContext, Future}

class CleanSerializationTest extends LanguageServerBaseTest with ChangesWorkspaceConfiguration with FileAssertionTest {

  override implicit val executionContext: ExecutionContext =
    ExecutionContext.Implicits.global

  override val initializeParams: AlsInitializeParams = AlsInitializeParams(
    Some(AlsClientCapabilities(serialization = Some(SerializationClientCapabilities(true)))),
    Some(TraceKind.Off)
  )

  test("Request serialized - check that URIs are fetched correctly to the configuration manager (normal encoded URI)") {
    val alsClient: MockAlsClientNotifier = new MockAlsClientNotifier
    val spyResourceLoader: Spy[String]   = (param: String) => param.isValidUri
    val serializationProps: SerializationProps[StringWriter] =
      new SerializationProps[StringWriter](alsClient) {
        override def newDocBuilder(prettyPrint: Boolean): DocBuilder[StringWriter] =
          JsonOutputBuilder(prettyPrint)
      }

    val apiUrl = "file://api%20.raml"
    val resourceLoader = new ResourceLoader {

      private val fs: Map[String, String] = Map {
        apiUrl -> """#%RAML 1.0
                    |title: test
                    |description: missing title
                    |""".stripMargin
      }

      override def fetch(resource: String): Future[Content] =
        Future.successful(new Content(fs(resource), resource))

      override def accepts(resource: String): Boolean = {
        fs.contains(spyResourceLoader.evaluate(resource))
      }
    }
    val spyConfiguration: Spy[String] = (param: String) => param.isValidUri
    withServer(buildServer(serializationProps, resourceLoader, spyConfiguration)) { server =>
      for {
        s <- serialize(server, apiUrl, serializationProps, clean = true, sourcemaps = true)
      } yield {
        assert(s.contains("sourcemaps"))
        assert(spyConfiguration.hasPassed)
        assert(spyResourceLoader.hasPassed)
      }
    }
  }

  def buildServer(
      serializationProps: SerializationProps[StringWriter],
      resourceLoader: ResourceLoader,
      spy: Spy[String]
  ): LanguageServer = {
    val factoryBuilder: WorkspaceManagerFactoryBuilder =
      new WorkspaceManagerFactoryBuilder(
        new ClientNotifier {
          override def notifyDiagnostic(params: PublishDiagnosticsParams): Unit = {}
          override def notifyTelemetry(params: TelemetryMessage): Unit          = {}
        },
        EditorConfiguration(Seq(resourceLoader), Seq.empty, Seq.empty)
      )
    val serializationManager: SerializationManager[StringWriter] =
      factoryBuilder.serializationManager(serializationProps)
    val factory: WorkspaceManagerFactory = factoryBuilder.buildWorkspaceManagerFactory()

    val builder =
      new LanguageServerBuilder(
        factory.documentManager,
        factory.workspaceManager,
        factory.configurationManager,
        factory.resolutionTaskManager
      )

    builder.addInitializableModule(serializationManager)
    val spiedManager: WorkspaceConfigurationManager =
      new TestWorkspaceConfigurationManager(factory.workspaceConfigurationManager, spy)
    serializationManager.withWorkspaceConfigurationManager(spiedManager)
    builder.addRequestModule(serializationManager)
    builder.build()
  }

  override def rootPath: String = ""

  sealed class TestWorkspaceConfigurationManager(delegate: WorkspaceConfigurationManager, spy: Spy[String])
      extends WorkspaceConfigurationManager(delegate.workspaceManager) {
    override def getConfigurationState(uri: String): Future[ALSConfigurationState] =
      super.getConfigurationState(spy.evaluate(uri))
  }

}
