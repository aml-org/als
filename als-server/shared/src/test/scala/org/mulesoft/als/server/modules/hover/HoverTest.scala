package org.mulesoft.als.server.modules.hover

import amf.plugins.domain.webapi.metamodel.{EndPointModel, WebApiModel}
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.modules.reference.MarkerInfo
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.{LanguageServerBuilder, MockDiagnosticClientNotifier, ServerWithMarkerTest}
import org.mulesoft.lsp.feature.common.{Position, Range, TextDocumentIdentifier}
import org.mulesoft.lsp.feature.hover.{Hover, HoverParams, HoverRequestType}

import scala.concurrent.{ExecutionContext, Future}

class HoverTest extends ServerWithMarkerTest[Hover] {
  override implicit val executionContext: ExecutionContext =
    ExecutionContext.Implicits.global

  override def rootPath: String = "actions/hover"

  def buildServer(): LanguageServer = {

    val factory =
      new WorkspaceManagerFactoryBuilder(new MockDiagnosticClientNotifier, logger).buildWorkspaceManagerFactory()
    new LanguageServerBuilder(factory.documentManager,
                              factory.workspaceManager,
                              factory.configurationManager,
                              factory.resolutionTaskManager)
      .addRequestModule(factory.hoverManager)
      .build()
  }

  test("Test hover raml web api description") {
    runTest(buildServer(), "webapi-description.raml").map { h =>
      h.contents.size should be(1)
      h.contents.head should be(WebApiModel.Description.doc.description)
      h.range.get should be(Range(Position(2, 0), Position(3, 0)))
    }
  }

  test("Test hover oas web api title") {
    runTest(buildServer(), "oas-webapi-title.yaml").map { h =>
      h.contents.size should be(1)
      h.contents.head should be(WebApiModel.Name.doc.description)
      h.range.get should be(Range(Position(2, 2), Position(3, 0)))
    }
  }

  test("Test hover endpoint description at value") {
    runTest(buildServer(), "endpoint-description-value.raml").map { h =>
      h.contents.size should be(1)
      h.contents.head should be(EndPointModel.Description.doc.description)
      h.range.get should be(Range(Position(4, 2), Position(5, 0)))
    }
  }

  test("Test hover endpoint object in middle") {
    runTest(buildServer(), "endpoint-in-middle.raml").map { h =>
      h.contents.size should be(1)
      h.contents.head should be(EndPointModel.doc.description)
      h.range.get should be(Range(Position(3, 0), Position(8, 0)))
    }
  }

  test("Test hover endpoint in name") {
    runTest(buildServer(), "endpoint-in-name.raml").map { h =>
      h.contents.size should be(1)
      h.contents.head should be(EndPointModel.Path.doc.description)
      h.range.get should be(Range(Position(3, 0), Position(3, 9)))
    }
  }

  test("Test aml hover name") {
    runTest(buildServer(), "aml/instance-name.yaml", Some("aml/dialect.yaml")).map { h =>
      h.contents.size should be(1)
      h.contents.head should be("Name for the root element")
      h.range.get should be(Range(Position(1, 0), Position(2, 0)))
    }
  }

  test("Test aml hover son level description") {
    runTest(buildServer(), "aml/instance-son-kind.yaml", Some("aml/dialect.yaml")).map { h =>
      h.contents.size should be(1)
      h.contents.head should be("the second level kind")
      h.range.get should be(Range(Position(5, 4), Position(6, 0)))
    }
  }

  test("Test aml hover over a value") {
    runTest(buildServer(), "aml/instance-value.yaml", Some("aml/dialect.yaml")).map { h =>
      h.contents.size should be(1)
      h.contents.head should be("The version of the root element and the hole document")
      h.range.get should be(Range(Position(2, 0), Position(3, 0)))
    }
  }

  test("Test aml hover over standard schema or semantic") {
    runTest(buildServer(), "aml/instance-son-description.yaml", Some("aml/dialect.yaml")).map { h =>
      h.contents.size should be(1)
      h.contents.head should be("Human readable description of an element")
      h.range.get should be(Range(Position(6, 4), Position(7, 0)))
    }
  }

  override def getAction(path: String, server: LanguageServer, markerInfo: MarkerInfo): Future[Hover] = {
    val hoverHandler = server.resolveHandler(HoverRequestType).value
    hoverHandler(HoverParams(TextDocumentIdentifier(path), LspRangeConverter.toLspPosition(markerInfo.position)))
      .map(hover => {
        closeFile(server)(path)
        hover
      })
  }
}
