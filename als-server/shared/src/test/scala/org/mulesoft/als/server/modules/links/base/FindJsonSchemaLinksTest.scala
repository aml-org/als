package org.mulesoft.als.server.modules.links.base

import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.als.server.modules.links.FindLinksTest
import org.mulesoft.lsp.feature.link.DocumentLink

class FindJsonSchemaLinksTest extends FindLinksTest {

  // hacer un test por cada draft

  ignore("Jason-Schema-Draft-03") {
    runTest(
      "files/simple-ref/json-schema/draft-03/simple.yaml",
      Set(
        DocumentLink(
          LspRangeConverter.toLspRange(PositionRange(Position(2, 9), Position(2, 30))),
          "file://als-server/shared/src/test/resources/actions/links/files/simple-ref/json-schema/draft-03/simple%20ref.json",
          None
        )
      )
    )
  }

  ignore("Jason-Schema-Draft-04") {
    runTest(
      "files/simple-ref/json-schema/draft-04/simple.yaml",
      Set(
        DocumentLink(
          LspRangeConverter.toLspRange(PositionRange(Position(2, 9), Position(2, 30))),
          "file://als-server/shared/src/test/resources/actions/links/files/simple-ref/json-schema/draft-04/simple%20ref.json",
          None
        )
      )
    )
  }

  ignore("Jason-Schema-Draft-07") {
    runTest(
      "files/simple-ref/json-schema/draft-07/simple.yaml",
      Set(
        DocumentLink(
          LspRangeConverter.toLspRange(PositionRange(Position(2, 9), Position(2, 30))),
          "file://als-server/shared/src/test/resources/actions/links/files/simple-ref/json-schema/draft-07/simple%20ref.json",
          None
        )
      )
    )
  }

  ignore("Jason-Schema-Draft-2019") {
    runTest(
      "files/simple-ref/json-schema/draft-2019-09/simple.yaml",
      Set(
        DocumentLink(
          LspRangeConverter.toLspRange(PositionRange(Position(2, 9), Position(2, 30))),
          "file://als-server/shared/src/test/resources/actions/links/files/simple-ref/json-schema/draft-2019-09/simple%20ref.json",
          None
        )
      )
    )
  }
}
