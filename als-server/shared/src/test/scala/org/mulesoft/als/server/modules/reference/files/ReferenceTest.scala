package org.mulesoft.als.server.modules.reference.files

import org.mulesoft.als.common.MarkerInfo
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.als.server.modules.reference.ServerReferencesTest
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.lsp.feature.common.{Location, TextDocumentIdentifier}
import org.mulesoft.lsp.feature.reference.{ReferenceContext, ReferenceParams, ReferenceRequestType}

import scala.concurrent.{ExecutionContext, Future}

class ReferenceTest extends ServerReferencesTest {

  override implicit val executionContext: ExecutionContext =
    ExecutionContext.Implicits.global

  test("common-ref") {
    runTest(
      "files/common-ref/api.raml",
      Set.empty
      // DocumentLink in charge
    )
  }

  test("oas-anchor") {
    runTest(
      "files/oas-anchor/api.yaml",
      Set(
        Location(
          "file://als-server/shared/src/test/resources/actions/reference/files/oas-anchor/api.yaml",
          LspRangeConverter.toLspRange(PositionRange(Position(17, 18), Position(17, 38)))
        )
      )
    )
  }

  test("yaml-alias") {
    runTest(
      "files/yaml-alias/api.raml",
      Set(
        Location(
          "file://als-server/shared/src/test/resources/actions/reference/files/yaml-alias/api.raml",
          LspRangeConverter.toLspRange(PositionRange(Position(4, 13), Position(4, 20)))
        )
      )
    )
  }

  test("oas-ref") {
    runTest(
      "files/oas-ref/api.yaml",
      Set.empty
      // DocumentLink in charge
    )
  }

  test("raml-trait") {
    runTest(
      "files/raml-trait/api.raml",
      Set(
        Location(
          "file://als-server/shared/src/test/resources/actions/reference/files/raml-trait/api.raml",
          LspRangeConverter.toLspRange(PositionRange(Position(14, 8), Position(14, 10)))
        )
      )
    )
  }

  test("raml-trait implementations") {
    runTestImplementations(
      "files/raml-trait/api.raml",
      Set(
        Location(
          "file://als-server/shared/src/test/resources/actions/reference/files/raml-trait/api.raml",
          LspRangeConverter.toLspRange(PositionRange(Position(14, 8), Position(14, 10)))
        )
      )
    )
  }

  test("raml-trait library") {
    runTestImplementations(
      "files/raml-trait/api-name.raml",
      Set()
    )
  }

  test("raml-resourceType") {
    runTestImplementations(
      "files/raml-resourceType/api.raml",
      Set(
        Location(
          "file://als-server/shared/src/test/resources/actions/reference/files/raml-resourceType/api.raml",
          LspRangeConverter.toLspRange(PositionRange(Position(8, 4), Position(8, 16)))
        )
      )
    )
  }

  test("raml-resourceType field") {
    runTestImplementations(
      "files/raml-resourceType/api-value.raml",
      Set()
    )
  }

  override def getAction(path: String, server: LanguageServer, markerInfo: MarkerInfo): Future[Seq[Location]] = {

    val referenceHandler = server.resolveHandler(ReferenceRequestType).value

    openFile(server)(path, markerInfo.content).flatMap(_ =>
      referenceHandler(
        ReferenceParams(
          TextDocumentIdentifier(path),
          LspRangeConverter.toLspPosition(markerInfo.position),
          ReferenceContext(false)
        )
      )
        .map(references => {
          references
        })
    )
  }
}
