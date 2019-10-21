package org.mulesoft.als.server.modules.definition.files

import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.server.modules.definition.ServerDefinitionTest
import org.mulesoft.lsp.common.LocationLink
import org.mulesoft.lsp.convert.LspRangeConverter

import scala.concurrent.ExecutionContext

class DefinitionFilesTest extends ServerDefinitionTest {

  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  test("common-ref") {
    runTest(
      "files/common-ref/api.raml",
      Set(
        LocationLink(
          "file://als-server/shared/src/test/resources/actions/definition/files/common-ref/included%20directory/fragment.txt",
          LspRangeConverter.toLspRange(PositionRange(Position(0, 0), Position(0, 0))),
          LspRangeConverter.toLspRange(PositionRange(Position(0, 0), Position(0, 0))),
          Some(LspRangeConverter.toLspRange(PositionRange(Position(1, 7), Position(1, 48))))
        )
      )
    )
  }

  test("oas-anchor") {
    runTest(
      "files/oas-anchor/api.yaml",
      Set(
        LocationLink(
          "file://als-server/shared/src/test/resources/actions/definition/files/oas-anchor/api.yaml",
          LspRangeConverter.toLspRange(PositionRange(Position(6, 2), Position(11, 0))),
          LspRangeConverter.toLspRange(PositionRange(Position(6, 2), Position(11, 0))),
          Some(LspRangeConverter.toLspRange(PositionRange(Position(17, 18), Position(17, 38))))
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

  ignore("oas-ref") {
    runTest(
      "files/oas-ref/api.yaml",
      Set(
        LocationLink(
          "file://als-server/shared/src/test/resources/actions/definition/files/oas-ref/reference/reference.yaml",
          LspRangeConverter.toLspRange(PositionRange(Position(0, 0), Position(0, 0))),
          LspRangeConverter.toLspRange(PositionRange(Position(0, 0), Position(0, 0))),
          Some(LspRangeConverter.toLspRange(PositionRange(Position(2, 8), Position(2, 32))))
        )
      )
    )
  }

  ignore("protocol-ref") {
    runTest(
      "files/protocol-ref/api.raml",
      Set(
        LocationLink(
          "http://localhost:8080/test",
          LspRangeConverter.toLspRange(PositionRange(Position(0, 0), Position(0, 0))),
          LspRangeConverter.toLspRange(PositionRange(Position(0, 0), Position(0, 0))),
          Some(LspRangeConverter.toLspRange(PositionRange(Position(1, 16), Position(1, 42))))
        )
      )
    )
  }
}
