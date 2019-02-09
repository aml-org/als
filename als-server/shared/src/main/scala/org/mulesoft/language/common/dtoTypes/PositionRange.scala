package org.mulesoft.language.common.dtoTypes

import amf.core.parser.{Range => AmfRange}

case class PositionRange(start: Position, end: Position)

object PositionRange {
  def apply(range: AmfRange): PositionRange = PositionRange(Position(range.start), Position(range.end))
}

object EmptyPositionRange extends PositionRange(Position0, Position1)
