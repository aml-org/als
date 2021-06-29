package org.mulesoft.als.server.modules.serialization

import amf.core.internal.remote.Spec
import amf.core.internal.remote.Spec._
import org.mulesoft.als.server.feature.serialization.{ConversionParams, ConversionRequestType, SerializedDocument}
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.{LanguageServerBaseTest, LanguageServerBuilder, MockDiagnosticClientNotifier}
import org.scalatest.Assertion

import scala.concurrent.{ExecutionContext, Future}

class ConversionRequestTest extends LanguageServerBaseTest {
  override implicit val executionContext: ExecutionContext =
    scala.concurrent.ExecutionContext.Implicits.global
  def buildServer(): LanguageServer = {
    val factory =
      new WorkspaceManagerFactoryBuilder(new MockDiagnosticClientNotifier, logger).buildWorkspaceManagerFactory()

    new LanguageServerBuilder(factory.documentManager,
                              factory.workspaceManager,
                              factory.configurationManager,
                              factory.resolutionTaskManager)
      .addRequestModule(factory.conversionManager)
      .build()
  }

  private val assertions: Map[Spec, SerializedDocument => Assertion] = Map(
    OAS20   -> ((s: SerializedDocument) => s.document should include("\"swagger\": \"2.0\"")),
    OAS30   -> ((s: SerializedDocument) => s.document should startWith("openapi: 3.0.0")),
    RAML10  -> ((s: SerializedDocument) => s.document should startWith("#%RAML 1.0")),
    ASYNC20 -> ((s: SerializedDocument) => s.document should startWith("asyncapi: 2.0.0"))
  )

  test("RAML 0.8 to RAML 1.0 conversion test") {
    run(RAML08, RAML10)
  }

  test("RAML 1.0 to OAS 2.0 conversion test") {
    run(RAML10, OAS20)
  }

  test("RAML 1.0 to OAS 3.0 conversion test") {
    run(RAML10, OAS30)
  }

  test("OAS 2.0 to RAML 1.0 conversion test") {
    run(OAS20, RAML10)
  }

  ignore("OAS 2.0 to RAML 1.0 json syntax conversion test") {
    run(OAS20, RAML10, Some("json")).recoverWith {
      case e: Exception =>
        e.getMessage should include("Cannot serialize domain model")
    }
  }

  test("OAS 3.0 to RAML 1.0 conversion test") {
    run(OAS30, RAML10)
  }

  test("AsyncApi 2.0 yaml to json") {
    run(ASYNC20,
        ASYNC20,
        Some("json"),
        (s: SerializedDocument) => s.document should include("\"asyncapi\": \"2.0.0\""))
  }

  test("OAS 2.0 json to yaml") {
    runTest(OAS20,
            Some("yaml"),
            (s: SerializedDocument) => s.document should startWith("swagger: \"2.0\""),
            Oas20JsonApi)
  }

  test("OAS 2.0 yaml to json") {
    runTest(OAS20, Some("json"), assertions(OAS20), Oas20Api)
  }

  test("OAS 3.0 json to yaml") {
    runTest(OAS30, Some("yaml"), assertions(OAS30), Oas30JsonApi)
  }

  test("OAS 3.0 yaml to json") {
    run(OAS30, OAS30, Some("json"), (s: SerializedDocument) => s.document should include("\"openapi\": \"3.0.0\""))
  }

  test("AsyncApi 2.0 json to yaml") {
    runTest(ASYNC20, Some("yaml"), assertions(ASYNC20), AsyncApi20JsonApi)
  }

  private def run(from: Spec, to: Spec, forcedSyntax: Option[String] = None): Future[Assertion] = {
    run(from, to, forcedSyntax, assertions(to))
  }

  private def run(from: Spec, to: Spec, forcedSyntax: Option[String], assertion: SerializedDocument => Assertion) = {
    runTest(to, forcedSyntax, assertion, apiFromVendor(from))
  }

  private def runTest(to: Spec,
                      forcedSyntax: Option[String] = None,
                      assertion: SerializedDocument => Assertion,
                      conf: ApiConf) = {
    withServer(buildServer()) { server =>
      openFile(server)(conf.name, conf.content)
        .flatMap(_ =>
          requestConversion(server, to.id, conf.name, forcedSyntax.getOrElse(to.mediaType.stripPrefix("application/")))
            .map(assertion) // media types over vendor
        )
    }
  }

  private def apiFromVendor(from: Spec) =
    from match {
      case RAML10  => Raml10Api
      case RAML08  => Raml08Api
      case OAS20   => Oas20Api
      case OAS30   => Oas30Api
      case ASYNC20 => AsyncApi20Api
    }

  trait ApiConf {
    val name: String
    val content: String
  }

  object Oas20Api extends ApiConf {
    override val name            = "file://api.yaml"
    override val content: String = """swagger: '2.0'
                             |info:
                             |  title: test""".stripMargin
  }

  object Oas20JsonApi extends ApiConf {
    override val name            = "file://api.json"
    override val content: String = """{
                                     |  "swagger": "2.0",
                                     |  "info": {
                                     |    "title": "test"
                                     |  }
                                     |}""".stripMargin
  }

  object Oas30Api extends ApiConf {
    override val name            = "file://api.yaml"
    override val content: String = """openapi: 3.0.0
                                     |info:
                                     |  title: test""".stripMargin
  }

  object Oas30JsonApi extends ApiConf {
    override val name            = "file://api.json"
    override val content: String = """{
                                     |  "openapi": "3.0.0",
                                     |  "info": {
                                     |    "title": "test"
                                     |  }
                                     |}""".stripMargin
  }

  object Raml10Api extends ApiConf {
    override val name            = "file://api.raml"
    override val content: String = """#%RAML 1.0
                                     |title: test""".stripMargin
  }

  object Raml08Api extends ApiConf {
    override val name            = "file://api.raml"
    override val content: String = """#%RAML 0.8
                                     |title: test""".stripMargin
  }

  object AsyncApi20Api extends ApiConf {
    override val name            = "file://api.yaml"
    override val content: String = """asyncapi: 2.0.0
                                     |info:
                                     |  title: test""".stripMargin
  }

  object AsyncApi20JsonApi extends ApiConf {
    override val name            = "file://api.json"
    override val content: String = """{
                                     |  "asyncapi": "2.0.0",
                                     |  "info": {
                                     |    "title": "a"
                                     |  }
                                     |}""".stripMargin
  }

  private def requestConversion(server: LanguageServer,
                                to: String,
                                uri: String,
                                syntax: String): Future[SerializedDocument] = {
    server
      .resolveHandler(ConversionRequestType)
      .value
      .apply(ConversionParams(uri, to, Some(syntax)))
  }

  override def rootPath: String = ""
}
