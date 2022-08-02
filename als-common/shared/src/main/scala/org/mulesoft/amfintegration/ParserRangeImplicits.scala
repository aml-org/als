package org.mulesoft.amfintegration

import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.common.client.lexical.{PositionRange => AmfPositionRange}

object ParserRangeImplicits {
  implicit class RangeImplicit(range: AmfPositionRange) {
    def toPositionRange: PositionRange = {
      PositionRange(range)
    }
  }

}
