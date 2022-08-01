package org.mulesoft.als.server.modules.links.base

import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.als.server.modules.links.FindLinksTest
import org.mulesoft.lsp.feature.link.DocumentLink

class FindJsonSchemaLinksTest extends FindLinksTest {

  // ignored until amf adds links instead of inlined references (W-11461036)
  ignore("Json Schema draft-03") {
    runTest(
      "files/simple-ref/json-schema/draft-03/basic-schema.json",
      Set(
        DocumentLink(
          LspRangeConverter.toLspRange(PositionRange(Position(7, 24), Position(7, 61))),
          "file://als-server/shared/src/test/resources/actions/links/files/simple-ref/json-schema/draft-03/basic-schema2.json",
          None
        )
      )
    )
  }

  ignore("Json Schema draft-04") {
    runTest(
      "files/simple-ref/json-schema/draft-04/basic-schema.json",
      Set(
        DocumentLink(
          LspRangeConverter.toLspRange(PositionRange(Position(7, 24), Position(7, 61))),
          "file://als-server/shared/src/test/resources/actions/links/files/simple-ref/json-schema/draft-04/basic-schema2.json",
          None
        )
      )
    )
  }

  ignore("Json Schema draft-07") {
    runTest(
      "files/simple-ref/json-schema/draft-07/basic-schema.json",
      Set(
        DocumentLink(
          LspRangeConverter.toLspRange(PositionRange(Position(7, 24), Position(7, 61))),
          "file://als-server/shared/src/test/resources/actions/links/files/simple-ref/json-schema/draft-07/basic-schema2.json",
          None
        )
      )
    )
  }

  ignore("Json Schema draft-2019-09") {
    runTest(
      "files/simple-ref/json-schema/draft-2019-09/basic-schema.json",
      Set(
        DocumentLink(
          LspRangeConverter.toLspRange(PositionRange(Position(7, 24), Position(7, 61))),
          "file://als-server/shared/src/test/resources/actions/links/files/simple-ref/json-schema/draft-2019-09/basic-schema2.json",
          None
        )
      )
    )
  }
}