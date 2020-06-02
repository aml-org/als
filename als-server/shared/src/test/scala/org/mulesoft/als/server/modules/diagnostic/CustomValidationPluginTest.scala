package org.mulesoft.als.server.modules.diagnostic

import amf.ProfileNames
import amf.client.plugins.{AMFPlugin, ValidationMode}
import amf.client.remote.Content
import amf.core.model.document.PayloadFragment
import amf.core.model.domain.Shape
import amf.core.validation.{AMFPayloadValidationPlugin, AMFValidationReport, PayloadValidator}
import amf.internal.environment.Environment
import amf.internal.resource.ResourceLoader
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.{LanguageServerBaseTest, LanguageServerBuilder, MockDiagnosticClientNotifier}
import org.mulesoft.amfintegration.AmfInstance

import scala.concurrent.{ExecutionContext, Future}

class CustomValidationPluginTest extends LanguageServerBaseTest {

  override implicit val executionContext = ExecutionContext.Implicits.global

  val rl: ResourceLoader = new ResourceLoader {
    override def fetch(resource: String): Future[Content] = Future.failed(new Exception("error"))

    override def accepts(resource: String): Boolean = false
  }
  val env: Environment = Environment().add(rl)

  val plugin = new AMFPayloadValidationPlugin {
    override val payloadMediaType: Seq[String] = Seq("application/xml")

    override def canValidate(shape: Shape, env: Environment): Boolean = true
    var rls: Seq[ResourceLoader]                                      = Nil
    override def validator(s: Shape, envi: Environment, validationMode: ValidationMode): PayloadValidator = {
      rls = env.loaders
      new PayloadValidator {
        override val shape: Shape                   = s
        override val defaultSeverity: String        = "VIOLATION"
        override val validationMode: ValidationMode = ValidationMode.StrictValidationMode

        override def validate(mediaType: String, payload: String)(
            implicit executionContext: ExecutionContext): Future[AMFValidationReport] =
          Future(new AMFValidationReport(true, "", ProfileNames.RAML10, Nil))

        override def validate(payloadFragment: PayloadFragment)(
            implicit executionContext: ExecutionContext): Future[AMFValidationReport] =
          Future(new AMFValidationReport(true, "", ProfileNames.RAML10, Nil))

        override def syncValidate(mediaType: String, payload: String): AMFValidationReport =
          new AMFValidationReport(true, "", ProfileNames.RAML10, Nil)

        override def isValid(mediaType: String, payload: String)(
            implicit executionContext: ExecutionContext): Future[Boolean] = Future(true)

        override val env: Environment = envi

      }
    }

    override val ID: String = "MyPlugin"

    override def dependencies(): Seq[AMFPlugin] = Nil

    override def init()(implicit executionContext: ExecutionContext): Future[AMFPlugin] = Future(this)
  }

  def buildServer(diagnosticNotifier: MockDiagnosticClientNotifier): LanguageServer = {
    val amfInstance = new AmfInstance(Seq(plugin), platform, env)
    val builder     = new WorkspaceManagerFactoryBuilder(diagnosticNotifier, logger, env).withAmfConfiguration(amfInstance)
    val dm          = builder.diagnosticManager()
    val factory     = builder.buildWorkspaceManagerFactory()
    val b           = new LanguageServerBuilder(factory.documentManager, factory.workspaceManager, factory.resolutionTaskManager)

    dm.foreach(b.addInitializableModule)
    b.build()
  }

  override def rootPath: String = ???

  test("Test resource loader invocation from custom plugin") {
    val diagnosticNotifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier
    withServer(buildServer(diagnosticNotifier)) { server =>
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

      /*
        register dialect -> open invalid instance -> fix -> invalid again
       */
      for {
        _ <- openFileNotification(server)(apiPath, apiContent)
        _ <- diagnosticNotifier.nextCall
      } yield {
        server.shutdown()
        assert(plugin.rls.size == 3)
        assert(plugin.rls.contains(rl))
      }
    }
  }
}
