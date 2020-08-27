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
          LspRangeConverter.toLspRange(PositionRange(Position(6, 2), Position(6, 6))),
          LspRangeConverter.toLspRange(PositionRange(Position(6, 2), Position(6, 6))),
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

  test("oas-ref") {
    runTest(
      "files/oas-ref/api.yaml",
      Set.empty
      // DocumentLink in charge
    )
  }

  test("oas-ref-to-node") {
    runTest(
      "files/oas-ref-to-node/api.yaml",
      Set(LocationLink(
        "file://als-server/shared/src/test/resources/actions/definition/files/oas-ref-to-node/reference/reference.yaml",
        LspRangeConverter.toLspRange(PositionRange(Position(6, 2), Position(6, 8))),
        LspRangeConverter.toLspRange(PositionRange(Position(6, 2), Position(6, 8))),
        Some(LspRangeConverter.toLspRange(PositionRange(Position(14, 22), Position(14, 68))))
      ))
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
          LspRangeConverter.toLspRange(PositionRange(Position(8, 4), Position(11, 0))),
          LspRangeConverter.toLspRange(PositionRange(Position(8, 4), Position(11, 0))),
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
          LspRangeConverter.toLspRange(PositionRange(Position(3, 4), Position(4, 13))),
          LspRangeConverter.toLspRange(PositionRange(Position(3, 4), Position(4, 13))),
          Some(LspRangeConverter.toLspRange(PositionRange(Position(15, 12), Position(15, 14))))
        )
      )
    )
  }

  test("raml-resource 1") {
    runTest(
      "files/raml-resource/api-1.raml",
      Set(
        LocationLink(
          "file://als-server/shared/src/test/resources/actions/definition/files/raml-resource/api-1.raml",
          LspRangeConverter.toLspRange(PositionRange(Position(8, 4), Position(11, 0))),
          LspRangeConverter.toLspRange(PositionRange(Position(8, 4), Position(11, 0))),
          Some(LspRangeConverter.toLspRange(PositionRange(Position(13, 6), Position(13, 14))))
        ))
    )
  }

  test("raml-resource 2") {
    runTest(
      "files/raml-resource/api-2.raml",
      Set(
        LocationLink(
          "file://als-server/shared/src/test/resources/actions/definition/files/raml-resource/library.raml",
          LspRangeConverter.toLspRange(PositionRange(Position(3, 4), Position(4, 12))),
          LspRangeConverter.toLspRange(PositionRange(Position(3, 4), Position(4, 12))),
          Some(LspRangeConverter.toLspRange(PositionRange(Position(13, 10), Position(13, 18))))
        )
      )
    )
  }

  test("raml-trait 1 type definition") {
    runTestTypeDefinition(
      "files/raml-trait/api-1.raml",
      Set(
        LocationLink(
          "file://als-server/shared/src/test/resources/actions/definition/files/raml-trait/api-1.raml",
          LspRangeConverter.toLspRange(PositionRange(Position(8, 4), Position(11, 0))),
          LspRangeConverter.toLspRange(PositionRange(Position(8, 4), Position(11, 0))),
          Some(LspRangeConverter.toLspRange(PositionRange(Position(14, 8), Position(14, 10))))
        ))
    )
  }

  test("raml-trait 2 type definition") {
    runTestTypeDefinition(
      "files/raml-trait/api-2.raml",
      Set(
        LocationLink(
          "file://als-server/shared/src/test/resources/actions/definition/files/raml-trait/library.raml",
          LspRangeConverter.toLspRange(PositionRange(Position(3, 4), Position(4, 13))),
          LspRangeConverter.toLspRange(PositionRange(Position(3, 4), Position(4, 13))),
          Some(LspRangeConverter.toLspRange(PositionRange(Position(15, 12), Position(15, 14))))
        )
      )
    )
  }

  test("raml-resource 1 type definition") {
    runTestTypeDefinition(
      "files/raml-resource/api-1.raml",
      Set(
        LocationLink(
          "file://als-server/shared/src/test/resources/actions/definition/files/raml-resource/api-1.raml",
          LspRangeConverter.toLspRange(PositionRange(Position(8, 4), Position(11, 0))),
          LspRangeConverter.toLspRange(PositionRange(Position(8, 4), Position(11, 0))),
          Some(LspRangeConverter.toLspRange(PositionRange(Position(13, 6), Position(13, 14))))
        ))
    )
  }

  test("raml-resource 2 type definition") {
    runTestTypeDefinition(
      "files/raml-resource/api-2.raml",
      Set(
        LocationLink(
          "file://als-server/shared/src/test/resources/actions/definition/files/raml-resource/library.raml",
          LspRangeConverter.toLspRange(PositionRange(Position(3, 4), Position(4, 12))),
          LspRangeConverter.toLspRange(PositionRange(Position(3, 4), Position(4, 12))),
          Some(LspRangeConverter.toLspRange(PositionRange(Position(13, 10), Position(13, 18))))
        )
      )
    )
  }

  test("raml-library type definition") {
    runTestTypeDefinition(
      "files/raml-library/api.raml",
      Set(
        LocationLink(
          "file://als-server/shared/src/test/resources/actions/definition/files/raml-library/library.raml",
          LspRangeConverter.toLspRange(PositionRange(Position(3, 4), Position(4, 13))),
          LspRangeConverter.toLspRange(PositionRange(Position(3, 4), Position(4, 13))),
          Some(LspRangeConverter.toLspRange(PositionRange(Position(15, 12), Position(15, 14))))
        )
      )
    )
  }

  test("raml-library") {
    runTest(
      "files/raml-library/api.raml",
      Set(
        LocationLink(
          "file://als-server/shared/src/test/resources/actions/definition/files/raml-library/library.raml",
          LspRangeConverter.toLspRange(PositionRange(Position(3, 4), Position(4, 13))),
          LspRangeConverter.toLspRange(PositionRange(Position(3, 4), Position(4, 13))),
          Some(LspRangeConverter.toLspRange(PositionRange(Position(15, 12), Position(15, 14))))
        )
      )
    )
  }

  ignore("ref-anchor") {
    runTest(
      "files/ref-anchor/api.yaml",
      Set(
        LocationLink(
          "file://als-server/shared/src/test/resources/actions/definition/files/ref-anchor/fragment.yaml",
          LspRangeConverter.toLspRange(PositionRange(Position(0, 0), Position(3, 14))),
          LspRangeConverter.toLspRange(PositionRange(Position(0, 0), Position(3, 14))),
          Some(LspRangeConverter.toLspRange(PositionRange(Position(4, 13), Position(4, 38))))
        ))
    )
  }

  test("yaml path navigation to type") {
    runTest(
      "files/path-nav/api.yaml",
      Set(
        LocationLink(
          "file://als-server/shared/src/test/resources/actions/definition/files/path-nav/ref.yaml",
          LspRangeConverter.toLspRange(PositionRange(Position(6, 2), Position(6, 8))),
          LspRangeConverter.toLspRange(PositionRange(Position(6, 2), Position(6, 8))),
          Some(LspRangeConverter.toLspRange(PositionRange(Position(14, 22), Position(14, 52))))
        )
      )
    )
  }

  test("yaml path navigation to recursive type") {
    runTest(
      "files/yaml-recursive/api.yaml",
      Set(
        LocationLink(
          "file://als-server/shared/src/test/resources/actions/definition/files/yaml-recursive/api.yaml",
          LspRangeConverter.toLspRange(PositionRange(Position(5, 2), Position(5, 8))),
          LspRangeConverter.toLspRange(PositionRange(Position(5, 2), Position(5, 8))),
          Some(LspRangeConverter.toLspRange(PositionRange(Position(13, 14), Position(13, 36))))
        )
      )
    )
  }
}
