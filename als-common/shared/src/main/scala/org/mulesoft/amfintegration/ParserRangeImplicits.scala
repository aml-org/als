package org.mulesoft.amfintegration

import org.mulesoft.als.common.dtoTypes.PositionRange
import amf.core.client.common.position.{Range => AmfRange}

object ParserRangeImplicits {
  implicit class RangeImplicit(range: AmfRange) {
    def toPositionRange: PositionRange = {
      PositionRange(range)
    }
  }

}
