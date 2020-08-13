package org.mulesoft.als.server.modules.hover

import amf.plugins.domain.shapes.metamodel.ExampleModel
import amf.plugins.domain.webapi.metamodel.security.SecuritySchemeModel
import amf.plugins.domain.webapi.metamodel.templates.{ResourceTypeModel, TraitModel}
import amf.plugins.domain.webapi.metamodel.{EndPointModel, OperationModel, WebApiModel}
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.modules.reference.MarkerInfo
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.{LanguageServerBuilder, MockDiagnosticClientNotifier, ServerWithMarkerTest}
import org.mulesoft.amfintegration.vocabularies.propertyterms.declarationKeys.{
  DomainPropertyDeclarationKeyTerm,
  MessageAbstractDeclarationKeyTerm,
  MessageDeclarationKeyTerm,
  OperationAbstractDeclarationKeyTerm,
  SecuritySettingsDeclarationKeyTerm,
  ShapeDeclarationKeyTerm
}
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

  test("Test raml type name") {
    runTest(buildServer(), "raml-type-name.raml").map { h =>
      h.contents.size should be(1)
      h.contents.head should be("Human readable name for the object") //ShaclNamePropertyTerm.description
      h.range.get should be(Range(Position(3, 2), Position(3, 8)))
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

  // syaml range should ends when the new line starts
  ignore("Test hover endpoint object in middle") {
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

  test("Test hover in resource type") {
    runTest(buildServer(), "resource-type.raml").map { h =>
      h.contents.size should be(1)
      h.contents.head should be(ResourceTypeModel.doc.description)
      h.range.get should be(Range(Position(3, 2), Position(6, 0)))
    }
  }

  test("Test hover in trait") {
    runTest(buildServer(), "trait.raml").map { h =>
      h.contents.size should be(1)
      h.contents.head should be(TraitModel.doc.description)
      h.range.get should be(Range(Position(3, 2), Position(5, 0)))
    }
  }

  test("Test hover in example value") {
    runTest(buildServer(), "example.raml").map { h =>
      h.contents.size should be(1)
      h.contents.head should be(ExampleModel.doc.description)
      h.range.get should be(Range(Position(7, 4), Position(10, 0)))
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

  test("Test hover on name of include") {
    runTest(buildServer(), "refs/api.raml").map { h =>
      h.contents.size should be(1)
      h.contents.head should be(SecuritySchemeModel.doc.description)
      h.range.get should be(Range(Position(3, 13), Position(3, 52)))
    }
  }

  test("Test hover on include value") {
    runTest(buildServer(), "refs/api2.raml").map { h =>
      h.contents.size should be(1)
      h.contents.head should be(SecuritySchemeModel.doc.description)
      h.range.get should be(Range(Position(3, 13), Position(3, 52)))
    }
  }

  test("Test hover on endpoint operation with include") {
    runTest(buildServer(), "refs/api3.raml").map { h =>
      h.contents.size should be(1)
      h.contents.head should be(OperationModel.Method.doc.description)
      h.range.get should be(Range(Position(3, 4), Position(3, 7)))
    }
  }

  test("Test hover on endpoint operation include") {
    runTest(buildServer(), "refs/api4.raml").map { h =>
      h.contents.size should be(1)
      h.contents.head should be(OperationModel.doc.description)
      h.range.get should be(Range(Position(3, 4), Position(3, 31)))
    }
  }

  test("Test hover on endpoint operation include keyword") {
    runTest(buildServer(), "refs/api5.raml").map { h =>
      h.contents.size should be(1)
      h.contents.head should be(OperationModel.doc.description)
      h.range.get should be(Range(Position(3, 4), Position(3, 31)))
    }
  }

  test("Test hover on declaration key") {
    runTest(buildServer(), "declarationKeys/with-include.raml").map { h =>
      h.contents.size should be(1)
      h.contents.head should be("Contains declarations of reusable SecurityScheme objects")
      h.range.get should be(Range(Position(2, 0), Position(5, 0)))
    }
  }

  test("Test declaration keys on RAML10") {
    runTestMultipleMarkers(buildServer(), "declarationKeys/raml10.raml").map { hovers =>
      hovers.size should be(5)
      hovers.exists(h => {
        h.contents.size == 1 &&
        h.contents.head == "Contains declarations of reusable SecurityScheme objects" &&
        h.range.get == Range(Position(8, 0), Position(12, 0))
      }) should be(true)

      hovers.exists(h => {
        h.contents.size == 1 &&
        h.contents.head == "Contains declarations of reusable Trait objects" &&
        h.range.get == Range(Position(12, 0), Position(16, 0))
      }) should be(true)

      hovers.exists(h => {
        h.contents.size == 1 && h.contents.head == ShapeDeclarationKeyTerm.description &&
        h.range.get == Range(Position(16, 0), Position(20, 0))
      }) should be(true)

      hovers.exists(h => {
        h.contents.size == 1 &&
        h.contents.head == DomainPropertyDeclarationKeyTerm.description &&
        h.range.get == Range(Position(20, 0), Position(23, 0))
      }) should be(true)

      hovers.exists(h => {
        h.contents.size == 1 &&
        h.contents.head == "Contains declarations of reusable ResourceType objects" &&
        h.range.get == Range(Position(23, 0), Position(25, 0))
      }) should be(true)
    }
  }

  test("Test declaration keys on RAML08") {
    runTestMultipleMarkers(buildServer(), "declarationKeys/raml08.raml").map { hovers =>
      hovers.size should be(4)
      hovers.exists(h => {
        h.contents.size == 1 &&
        h.contents.head == "Contains declarations of reusable SecurityScheme objects" &&
        h.range.get == Range(Position(1, 0), Position(2, 0))
      }) should be(true)

      hovers.exists(h => {
        h.contents.size == 1 &&
        h.contents.head == "Contains declarations of reusable Trait objects" &&
        h.range.get == Range(Position(2, 0), Position(3, 0))
      }) should be(true)

      hovers.exists(h => {
        h.contents.size == 1 && h.contents.head == ShapeDeclarationKeyTerm.description &&
        h.range.get == Range(Position(3, 0), Position(4, 0))
      }) should be(true)

      hovers.exists(h => {
        h.contents.size == 1 &&
        h.contents.head == "Contains declarations of reusable ResourceType objects" &&
        h.range.get == Range(Position(4, 0), Position(4, 14))
      }) should be(true)
    }
  }

  test("Test declaration keys on OAS3") {
    runTestMultipleMarkers(buildServer(), "declarationKeys/oas3.yaml").map { hovers =>
      hovers.size should be(9)
      hovers.exists(h => {
        h.contents.size == 1 &&
        h.contents.head == "Contains declarations of reusable Callback objects" &&
        h.range.get == Range(Position(5, 2), Position(10, 0))
      }) should be(true)

      hovers.exists(h => {
        h.contents.size == 1 &&
        h.contents.head == "Contains declarations of reusable Example objects" &&
        h.range.get == Range(Position(10, 2), Position(13, 0))
      }) should be(true)

      hovers.exists(h => {
        h.contents.size == 1 &&
        h.contents.head == "Contains declarations for headers" &&
        h.range.get == Range(Position(13, 2), Position(15, 0))
      }) should be(true)

      hovers.exists(h => {
        h.contents.size == 1 &&
        h.contents.head == "Contains declarations of reusable TemplatedLink objects" &&
        h.range.get == Range(Position(15, 2), Position(18, 0))
      }) should be(true)

      hovers.exists(h => {
        h.contents.size == 1 &&
        h.contents.head == "Contains declarations of reusable Parameter objects" &&
        h.range.get == Range(Position(18, 2), Position(21, 0))
      }) should be(true)

      hovers.exists(h => {
        h.contents.size == 1 &&
        h.contents.head == "Contains declarations of reusable Request objects" &&
        h.range.get == Range(Position(21, 2), Position(22, 0))
      }) should be(true)

      hovers.exists(h => {
        h.contents.size == 1 &&
        h.contents.head == "Contains declarations of reusable Response objects" &&
        h.range.get == Range(Position(22, 2), Position(23, 0))
      }) should be(true)

      hovers.exists(h => {
        h.contents.size == 1 &&
        h.contents.head == ShapeDeclarationKeyTerm.description &&
        h.range.get == Range(Position(23, 2), Position(24, 0))
      }) should be(true)

      hovers.exists(h => {
        h.contents.size == 1 &&
        h.contents.head == SecuritySettingsDeclarationKeyTerm.description &&
        h.range.get == Range(Position(24, 2), Position(25, 0))
      }) should be(true)
    }
  }

  test("Test declaration keys on OAS2") {
    runTestMultipleMarkers(buildServer(), "declarationKeys/oas2.yaml").map { hovers =>
      hovers.size should be(4)
      hovers.exists(h => {
        h.contents.size == 1 &&
        h.contents.head == ShapeDeclarationKeyTerm.description &&
        h.range.get == Range(Position(1, 0), Position(4, 0))
      }) should be(true)

      hovers.exists(h => {
        h.contents.size == 1 &&
        h.contents.head == "Contains declarations of reusable Parameter objects" &&
        h.range.get == Range(Position(4, 0), Position(9, 0))
      }) should be(true)

      hovers.exists(h => {
        h.contents.size == 1 &&
        h.contents.head == "Contains declarations of reusable Response objects" &&
        h.range.get == Range(Position(9, 0), Position(13, 0))
      }) should be(true)

      hovers.exists(h => {
        h.contents.size == 1 &&
        h.contents.head == "Contains declarations for securityDefinitions" &&
        h.range.get == Range(Position(13, 0), Position(14, 0))
      }) should be(true)
    }
  }

  test("Test declaration keys on AsyncApi2") {
    runTestMultipleMarkers(buildServer(), "declarationKeys/asyncapi2.yaml").map { hovers =>
      hovers.size should be(11)
      hovers.exists(h => {
        h.contents.size == 1 &&
        h.contents.head == "Contains declarations of reusable ChannelBindings objects" &&
        h.range.get == Range(Position(2, 2), Position(3, 0))
      }) should be(true)

      hovers.exists(h => {
        h.contents.size == 1 &&
        h.contents.head == "Contains declarations of reusable CorrelationId objects" &&
        h.range.get == Range(Position(3, 2), Position(4, 0))
      }) should be(true)

      hovers.exists(h => {
        h.contents.size == 1 &&
        h.contents.head == "Contains declarations of reusable MessageBindings objects" &&
        h.range.get == Range(Position(4, 2), Position(5, 0))
      }) should be(true)

      hovers.exists(h => {
        h.contents.size == 1 &&
        h.contents.head == MessageDeclarationKeyTerm.description &&
        h.range.get == Range(Position(5, 2), Position(8, 0))
      }) should be(true)

      hovers.exists(h => {
        h.contents.size == 1 &&
        h.contents.head == MessageAbstractDeclarationKeyTerm.description &&
        h.range.get == Range(Position(8, 2), Position(9, 0))
      }) should be(true)

      hovers.exists(h => {
        h.contents.size == 1 &&
        h.contents.head == "Contains declarations of reusable OperationBindings objects" &&
        h.range.get == Range(Position(9, 2), Position(10, 0))
      }) should be(true)

      hovers.exists(h => {
        h.contents.size == 1 &&
        h.contents.head == OperationAbstractDeclarationKeyTerm.description &&
        h.range.get == Range(Position(10, 2), Position(11, 0))
      }) should be(true)

      hovers.exists(h => {
        h.contents.size == 1 &&
        h.contents.head == "Contains declarations of reusable Parameter objects" &&
        h.range.get == Range(Position(11, 2), Position(12, 0))
      }) should be(true)

      hovers.exists(h => {
        h.contents.size == 1 &&
        h.contents.head == ShapeDeclarationKeyTerm.description &&
        h.range.get == Range(Position(12, 2), Position(16, 0))
      }) should be(true)

      hovers.exists(h => {
        h.contents.size == 1 &&
        h.contents.head == SecuritySettingsDeclarationKeyTerm.description &&
        h.range.get == Range(Position(16, 2), Position(17, 0))
      }) should be(true)

      hovers.exists(h => {
        h.contents.size == 1 &&
        h.contents.head == "Contains declarations of reusable ServerBindings objects" &&
        h.range.get == Range(Position(17, 2), Position(19, 0))
      }) should be(true)
    }
  }

  test("Test declaration keys of abstract objects") {
    runTestMultipleMarkers(buildServer(), "declarationKeys/abstractDeclaration.yaml").map { hovers =>
      hovers.size should be(2)
      hovers.exists(h => {
        h.contents.size == 1 &&
        h.contents.head == MessageDeclarationKeyTerm.description &&
        h.range.get == Range(Position(2, 2), Position(5, 0))
      }) should be(true)

      hovers.exists(h => {
        h.contents.size == 1 &&
        h.contents.head == MessageAbstractDeclarationKeyTerm.description &&
        h.range.get == Range(Position(5, 2), Position(5, 16))
      }) should be(true)
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
