package org.mulesoft.als.server.modules.diagnostic

import amf.core.client.common.{NormalPriority, PluginPriority}
import amf.core.client.common.remote.Content
import amf.core.client.common.validation.{ProfileNames, ValidationMode}
import amf.core.client.scala.model.document.PayloadFragment
import amf.core.client.scala.model.domain.Shape
import amf.core.client.scala.resource.ResourceLoader
import amf.core.client.scala.validation.AMFValidationReport
import amf.core.client.scala.validation.payload.{
  AMFShapePayloadValidationPlugin,
  AMFShapePayloadValidator,
  ShapeValidationConfiguration,
  ValidatePayloadRequest
}
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.{LanguageServerBaseTest, LanguageServerBuilder, MockDiagnosticClientNotifier}
import org.mulesoft.amfintegration.amfconfiguration.EditorConfiguration

import scala.concurrent.{ExecutionContext, Future}

class CustomValidationPluginTest extends LanguageServerBaseTest {

  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  trait Counter {
    def total: Int
  }

  val rl: ResourceLoader with Counter = new ResourceLoader with Counter {
    var acc                                               = 0
    override def fetch(resource: String): Future[Content] = Future.failed(new Exception("error"))

    override def accepts(resource: String): Boolean = {
      acc += 1
      false
    }

    override def total: Int = acc
  }

  val plugin: AMFShapePayloadValidationPlugin = new AMFShapePayloadValidationPlugin {
    override def priority: PluginPriority = NormalPriority

    override def applies(element: ValidatePayloadRequest): Boolean = element.mediaType == "application/xml"

    override def validator(shape: Shape,
                           mediaType: String,
                           config: ShapeValidationConfiguration,
                           validationMode: ValidationMode): AMFShapePayloadValidator = {
      new AMFShapePayloadValidator {
        override def validate(payload: String): Future[AMFValidationReport] =
          Future(new AMFValidationReport(payload, ProfileNames.RAML10, Nil))

        override def validate(payloadFragment: PayloadFragment): Future[AMFValidationReport] =
          Future(new AMFValidationReport(payloadFragment.raw.getOrElse(""), ProfileNames.RAML10, Nil))

        override def syncValidate(payload: String): AMFValidationReport =
          new AMFValidationReport(payload, ProfileNames.RAML10, Nil)
      }
    }
    override val id: String = "MyPlugin"
  }

  def buildServer(diagnosticNotifier: MockDiagnosticClientNotifier): LanguageServer = {
    val editorConfiguration = EditorConfiguration(rl +: platform.loaders(), Seq.empty, Seq(plugin), logger)
    val builder =
      new WorkspaceManagerFactoryBuilder(diagnosticNotifier, logger, editorConfiguration)
    val dm      = builder.buildDiagnosticManagers()
    val factory = builder.buildWorkspaceManagerFactory()
    val b = new LanguageServerBuilder(factory.documentManager,
                                      factory.workspaceManager,
                                      factory.configurationManager,
                                      factory.resolutionTaskManager)

    dm.foreach(m => b.addInitializableModule(m))
    b.build()
  }

  override def rootPath: String = ???

  test("Test resource loader invocation from custom plugin") {
    val diagnosticNotifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier(3000)
    val server                                           = buildServer(diagnosticNotifier)
    withServer(server) { server =>
      val apiPath = s"file://api.raml"

      val apiContent =
        """#%RAML 1.0
          |
          |title: Example API
          |
          |types:
          |  A:
          |    properties:
          |      a: string
          |      b: int
          |    example: |
          |       <object>
          |         <a> b</a>
          |         <b> 1</b>
          |       </object>
        """.stripMargin

      for {
        _ <- openFileNotification(server)(apiPath, apiContent)
        _ <- diagnosticNotifier.nextCall
      } yield {
        server.shutdown()
        assert(rl.total > 0)
      }
    }
  }
}
