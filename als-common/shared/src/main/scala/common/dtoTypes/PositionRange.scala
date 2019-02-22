package common.dtoTypes

import amf.core.parser.{Range => AmfRange}
import org.mulesoft.lexer.InputRange

case class PositionRange(start: Position, end: Position) {
  def contains(position: Position): Boolean = position >= start && position <= end

  def intersection(other: PositionRange): Option[PositionRange] =
    if(start > other.end || other.start > end) None
    else Some(PositionRange(Position.max(start, other.start), Position.min(end, other.end)))
}

object PositionRange {
  def apply(range: AmfRange): PositionRange = PositionRange(Position(range.start), Position(range.end))

  def apply(range: InputRange): PositionRange =
    PositionRange(Position(range.lineFrom - 1, range.columnFrom), Position(range.lineTo - 1, range.columnTo))
}

object EmptyPositionRange extends PositionRange(Position0, Position1)
