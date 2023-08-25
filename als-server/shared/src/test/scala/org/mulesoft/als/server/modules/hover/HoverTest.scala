package org.mulesoft.als.server.modules.hover

import org.mulesoft.als.common.{BaseHoverTest, MarkerInfo, PositionedHover}
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.als.server.client.scala.LanguageServerBuilder
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.{MockTelemetryParsingClientNotifier, ServerWithMarkerTest}
import org.mulesoft.lsp.feature.common.TextDocumentIdentifier
import org.mulesoft.lsp.feature.hover.{HoverParams, HoverRequestType}
import org.scalatest.compatible.Assertion

import scala.concurrent.{ExecutionContext, Future}

class HoverTest extends ServerWithMarkerTest[PositionedHover] with BaseHoverTest {
  override implicit val executionContext: ExecutionContext =
    ExecutionContext.Implicits.global

  override def rootPath: String = "actions/hover"

  override val notifier: MockTelemetryParsingClientNotifier = new MockTelemetryParsingClientNotifier()

  def buildServer(): LanguageServer = {

    val factory =
      new WorkspaceManagerFactoryBuilder(notifier).buildWorkspaceManagerFactory()
    new LanguageServerBuilder(
      factory.documentManager,
      factory.workspaceManager,
      factory.configurationManager,
      factory.resolutionTaskManager
    )
      .addRequestModule(factory.hoverManager)
      .build()
  }

  test("Test hover raml web api description") {
    runFor("webapi-description.raml")
  }

  test("Test raml type name") {
    runFor("raml-type-name.raml")
  }

  test("Test async message binding key") {
    runFor("async-api20-binding.yaml")
  }

  test("Test hover oas web api title") {
    runFor("oas-webapi-title.yaml")
  }

  test("Test hover endpoint description at value") {
    runFor("endpoint-description-value.raml")
  }

  test("Test hover endpoint object in middle") {
    runFor("endpoint-in-middle.raml")
  }

  test("Test hover endpoint in name") {
    runFor("endpoint-in-name.raml")
  }

  test("Test hover in resource type") {
    runFor("resource-type.raml")
  }

  test("Test hover in trait") {
    runFor("trait.raml")
  }

  test("Test hover in example value") {
    runFor("example.raml")
  }

  test("Test aml hover name") {
    runFor("aml/instance-name.yaml", Some("aml/dialect.yaml"), 1)
  }

  test("Test aml hover son level description") {
    runFor("aml/instance-son-kind.yaml", Some("aml/dialect.yaml"), 1)
  }

  test("Test aml hover over a value") {
    runFor("aml/instance-value.yaml", Some("aml/dialect.yaml"), 1)
  }

  test("Test aml hover over standard schema or semantic") {
    runFor("aml/instance-son-description.yaml", Some("aml/dialect.yaml"), 1)
  }

  test("Test hover on name of include") {
    runFor("refs/api.raml", 1)
  }

  test("Test hover on include value") {
    runFor("refs/api2.raml", 1)
  }

  test("Test hover on endpoint operation with include") {
    runFor("refs/api3.raml", 1)
  }

  test("Test hover on endpoint operation include") {
    runFor("refs/api4.raml", 1)
  }

  test("Test hover on endpoint operation include keyword") {
    runFor("refs/api5.raml", 1)
  }

  test("Test hover on declaration key") {
    runFor("declarationKeys/with-include.raml", 1)
  }

  test("Test declaration keys on RAML10") {
    runFor("declarationKeys/raml10.raml", 5)
  }

  test("Test declaration keys on RAML08") {
    runFor("declarationKeys/raml08.raml", 4)
  }

  test("Test declaration keys on OAS3") {
    runFor("declarationKeys/oas3.yaml", 9)
  }

  test("Test declaration keys on OAS2") {
    runFor("declarationKeys/oas2.yaml", 4)
  }

  test("Test declaration keys on AsyncApi2") {
    runFor("declarationKeys/asyncapi2.yaml", 11)
  }

  test("Test declaration keys of abstract objects") {
    runFor("declarationKeys/abstractDeclaration.yaml", 2)
  }

  test("Test AML Vocabularies") {
    runFor("aml/amlVocabulary.yaml", 9)
  }

  // TODO: missing annotations on basePath and host
  test("OAS2 top level keys") {
    runFor("toplevel/oas2.yaml", 5)
  }

  // TODO: fix paths matching as EndPoint rather than WebApi (see Oas3PathsKeyTerm.scala)
  test("OAS3 top level keys") {
    runFor("toplevel/oas3.yaml", 3)
  }

  test("AsyncApi2 top level keys") {
    runFor("toplevel/asyncapi2.yaml", 3)
  }

  test("Raml08 top level keys") {
    runFor("toplevel/raml08.raml", 2)
  }

  test("Raml10 top level keys") {
    runFor("toplevel/raml10.raml")
  }

  test("Raml10 top level uses") {
    runFor("toplevel/raml10-uses.raml")
  }

  test("OAS 3 specific-complex-case in parameters") {
    runFor("oas30-full.yaml")
  }

  override protected val printRange: Boolean = true

  def runFor(file: String, size: Int = 1): Future[Assertion] =
    runFor(file, None, size)

  def runFor(file: String, dialect: Option[String], size: Int): Future[Assertion] = {
    runTestMultipleMarkers(buildServer(), file, dialect).flatMap { hovers =>
      hovers.size should be(size)
      compareResults(filePath(file + ".golden"), hovers)
    }
  }

  override def getAction(path: String, server: LanguageServer, markerInfo: MarkerInfo): Future[PositionedHover] = {
    val hoverHandler = server.resolveHandler(HoverRequestType).value
    hoverHandler(HoverParams(TextDocumentIdentifier(path), LspRangeConverter.toLspPosition(markerInfo.position)))
      .map(hover => {
        PositionedHover(markerInfo.position, hover)
      })
  }
}
