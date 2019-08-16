package org.mulesoft.als.common

import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.lexer.InputRange
import org.yaml.model._

object YamlWrapper {

  implicit class AlsInputRange(range: InputRange) {
    def toPositionRange =
      PositionRange(Position(range.lineFrom, range.columnFrom), Position(range.lineTo, range.columnTo))
  }

  implicit class AlsYPart(selectedNode: YPart) {

    def isArray: Boolean = selectedNode.isInstanceOf[YSequence]

    def isKey(amfPosition: Position): Boolean =
      selectedNode match {
        case entry: YMapEntry => entry.key.range.toPositionRange.contains(amfPosition)
        case _                => false
      }

    def isValue(amfPosition: Position): Boolean =
      selectedNode match {
        case entry: YMapEntry => entry.value.range.toPositionRange.contains(amfPosition)
        case _                => false
      }
  }
}
