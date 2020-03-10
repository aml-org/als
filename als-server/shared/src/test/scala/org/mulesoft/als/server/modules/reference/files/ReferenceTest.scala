package org.mulesoft.als.server.modules.reference.files

import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.als.server.modules.reference.ServerReferencesTest
import org.mulesoft.lsp.feature.common.Location

import scala.concurrent.ExecutionContext

class ReferenceTest extends ServerReferencesTest {

  override implicit val executionContext: ExecutionContext =
    ExecutionContext.Implicits.global

  test("common-ref") {
    runTest(
      "files/common-ref/api.raml",
      Set(
        // DocumentLink in charge
      )
    )
  }

  test("oas-anchor") {
    runTest(
      "files/oas-anchor/api.yaml",
      Set(
        Location(
          "file://als-server/shared/src/test/resources/actions/reference/files/oas-anchor/api.yaml",
          LspRangeConverter.toLspRange(PositionRange(Position(16, 10), Position(17, 38)))
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
      Set(
        // DocumentLink in charge
        //        )
      )
    )
  }

  test("raml-trait") {
    runTest(
      "files/raml-trait/api.raml",
      Set(
        Location(
          "file://als-server/shared/src/test/resources/actions/reference/files/raml-trait/api.raml",
          LspRangeConverter.toLspRange(PositionRange(Position(14, 8), Position(14, 10)))
        ))
    )
  }
}
