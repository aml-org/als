package org.mulesoft.als.server.modules.definition.files

import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.server.modules.definition.ServerDefinitionTest
import org.mulesoft.lsp.common.Location
import org.mulesoft.lsp.convert.LspRangeConverter

import scala.concurrent.ExecutionContext

class DefinitionFilesTest extends ServerDefinitionTest {

  override implicit val executionContext = ExecutionContext.Implicits.global

  test("common-ref") {
    runTest(
      "files/common-ref/api.raml",
      Set(
        Location(
          "file://als-server/shared/src/test/resources/actions/definition/files/common-ref/included%20directory/fragment.txt",
          LspRangeConverter.toLspRange(PositionRange(Position(0, 0), Position(0, 0)))
        )
      )
    )
  }

  test("oas-anchor") {
    runTest(
      "files/oas-anchor/api.yaml",
      Set(
        Location(
          "file://als-server/shared/src/test/resources/actions/definition/files/oas-anchor/api.yaml",
          LspRangeConverter.toLspRange(PositionRange(Position(6, 2), Position(11, 0)))
        )
      )
    )
  }

  test("yaml-alias") {
    runTest(
      "files/yaml-alias/api.raml",
      Set(
        Location(
          "file://als-server/shared/src/test/resources/actions/definition/files/yaml-alias/api.raml",
          LspRangeConverter.toLspRange(PositionRange(Position(2, 7), Position(2, 24)))
        )
      )
    )
  }

  test("oas-ref") {
    runTest(
      "files/oas-ref/api.yaml",
      Set(
        Location(
          "file://als-server/shared/src/test/resources/actions/definition/files/oas-ref/reference/reference.yaml",
          LspRangeConverter.toLspRange(PositionRange(Position(0, 0), Position(0, 0)))
        )
      )
    )
  }

  test("protocol-ref") {
    runTest(
      "files/protocol-ref/api.raml",
      Set(
        Location(
          "http://localhost:8080/test",
          LspRangeConverter.toLspRange(PositionRange(Position(0, 0), Position(0, 0)))
        )
      )
    )
  }
}
