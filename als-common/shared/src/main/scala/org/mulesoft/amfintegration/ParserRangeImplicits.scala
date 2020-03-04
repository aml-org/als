package org.mulesoft.amfintegration

import org.mulesoft.als.common.dtoTypes.PositionRange

object ParserRangeImplicits {
  implicit class RangeImplicit(range: amf.core.parser.Range) {
    def toPositionRange: PositionRange = {
      PositionRange(range)
    }
  }

}
