package org.mulesoft.als.server.modules.links.base

import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.server.modules.links.FindLinksTest
import org.mulesoft.lsp.convert.LspRangeConverter
import org.mulesoft.lsp.feature.link.DocumentLink

class FindLinksBaseTest extends FindLinksTest {

  test("simple-include") {
    runTest(
      "files/simple-include/simple.raml",
      Set(
        DocumentLink(
          LspRangeConverter.toLspRange(PositionRange(Position(1, 7), Position(1, 32))),
          "file://als-server/shared/src/test/resources/actions/links/files/simple-include/title%20file.yaml",
          None
        )
      )
    )
  }

  test("simple-ref") {
    runTest(
      "files/simple-ref/simple.yaml",
      Set(
        DocumentLink(
          LspRangeConverter.toLspRange(PositionRange(Position(2, 8), Position(2, 29))),
          "file://als-server/shared/src/test/resources/actions/links/files/simple-ref/simple%20contact.json",
          None
        )
      )
    )
  }

  test("simple-uses") {
    runTest(
      "files/simple-uses/simple.raml",
      Set(
        DocumentLink(
          LspRangeConverter.toLspRange(PositionRange(Position(2, 7), Position(2, 26))),
          "file://als-server/shared/src/test/resources/actions/links/files/simple-uses/simple%20library.raml",
          None
        )
      )
    )
  }

  ignore("simple-uri") {
    runTest(
      "files/simple-uri/simple.raml",
      Set(
        DocumentLink(LspRangeConverter.toLspRange(PositionRange(Position(1, 16), Position(1, 52))),
                     "http://localhost:8080/test/title.txt",
                     None)
      )
    )
  }
}
