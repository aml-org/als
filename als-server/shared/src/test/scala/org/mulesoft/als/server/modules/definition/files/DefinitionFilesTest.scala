package org.mulesoft.als.server.modules.definition.files

import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.server.modules.definition.ServerDefinitionTest
import org.mulesoft.lsp.feature.common.LocationLink
import org.mulesoft.als.convert.LspRangeConverter

import scala.concurrent.ExecutionContext

class DefinitionFilesTest extends ServerDefinitionTest {

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
        LocationLink(
          "file://als-server/shared/src/test/resources/actions/definition/files/oas-anchor/api.yaml",
          LspRangeConverter.toLspRange(PositionRange(Position(6, 7), Position(11, 0))),
          LspRangeConverter.toLspRange(PositionRange(Position(6, 7), Position(11, 0))),
          Some(LspRangeConverter.toLspRange(PositionRange(Position(16, 10), Position(17, 38))))
        )
      )
    )
  }

  test("yaml-alias") {
    runTest(
      "files/yaml-alias/api.raml",
      Set(
        LocationLink(
          "file://als-server/shared/src/test/resources/actions/definition/files/yaml-alias/api.raml",
          LspRangeConverter.toLspRange(PositionRange(Position(2, 7), Position(2, 24))),
          LspRangeConverter.toLspRange(PositionRange(Position(2, 7), Position(2, 24))),
          Some(LspRangeConverter.toLspRange(PositionRange(Position(4, 13), Position(4, 20))))
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

  test("protocol-ref") {
    runTest(
      "files/protocol-ref/api.raml",
      Set(
        // DocumentLink in charge
        //        )
      )
    )
  }

  test("raml-trait 1") {
    runTest(
      "files/raml-trait/api-1.raml",
      Set(
        LocationLink(
          "file://als-server/shared/src/test/resources/actions/definition/files/raml-trait/api-1.raml",
          LspRangeConverter.toLspRange(PositionRange(Position(8, 7), Position(11, 0))),
          LspRangeConverter.toLspRange(PositionRange(Position(8, 7), Position(11, 0))),
          Some(LspRangeConverter.toLspRange(PositionRange(Position(14, 8), Position(14, 10))))
        ))
    )
  }

  test("raml-trait 2") {
    runTest(
      "files/raml-trait/api-2.raml",
      Set(
        LocationLink(
          "file://als-server/shared/src/test/resources/actions/definition/files/raml-trait/library.raml",
          LspRangeConverter.toLspRange(PositionRange(Position(3, 7), Position(4, 13))),
          LspRangeConverter.toLspRange(PositionRange(Position(3, 7), Position(4, 13))),
          Some(LspRangeConverter.toLspRange(PositionRange(Position(15, 8), Position(15, 14))))
        )
      )
    )
  }

  test("raml-library") {
    runTest(
      "files/raml-library/api.raml",
      Set(
        LocationLink(
          "file://als-server/shared/src/test/resources/actions/definition/files/raml-library/api.raml",
          LspRangeConverter.toLspRange(PositionRange(Position(5, 9), Position(5, 21))),
          LspRangeConverter.toLspRange(PositionRange(Position(5, 9), Position(5, 21))),
          Some(LspRangeConverter.toLspRange(PositionRange(Position(15, 8), Position(15, 14))))
        ),
        LocationLink(
          "file://als-server/shared/src/test/resources/actions/definition/files/raml-library/library.raml",
          LspRangeConverter.toLspRange(PositionRange(Position(3, 7), Position(4, 13))),
          LspRangeConverter.toLspRange(PositionRange(Position(3, 7), Position(4, 13))),
          Some(LspRangeConverter.toLspRange(PositionRange(Position(15, 8), Position(15, 14))))
        )
      )
    )
  }
}
