package org.mulesoft.als.actions.common

import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.lsp.feature.common.Location
import amf.core.parser.{Position => AmfPosition}
import org.yaml.model.YPart

object YPartImplicits {
  implicit class YPartImproved(yPart: YPart) {

    def yPartToLocation: Location =
      Location(
        yPart.sourceName,
        LspRangeConverter.toLspRange(
          PositionRange(
            Position(AmfPosition(yPart.range.lineFrom, yPart.range.columnFrom)),
            Position(AmfPosition(yPart.range.lineTo, yPart.range.columnTo))
          ))
      )
  }
}
