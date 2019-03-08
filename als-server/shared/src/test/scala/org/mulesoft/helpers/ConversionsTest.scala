package org.mulesoft.helpers

import common.dtoTypes.Position
import org.scalatest._

class ConversionsTest extends FlatSpec {

  "A position" should "maintain line and column equality through transformations" in {
    val originalText = """#%RAML 1.0
                         |
                         |title: test API
                         |
                         |securitySchemes:
                         |  ss:
                         |    type:
                         |
                         |/endpoint: """

    val pos = Position(6, 10)

    val offset = pos.offset(originalText)

    assert(pos == Position(offset, originalText))
  }

  it should "maintain offset equality through transformations" in {
    val originalText = """#%RAML 1.0
                         |
                         |title: test API
                         |
                         |securitySchemes:
                         |  ss:
                         |    type:
                         |
                         |/endpoint: """

    val offset = 62

    val pos = Position(offset, originalText)

    assert(offset == pos.offset(originalText))
  }
}
