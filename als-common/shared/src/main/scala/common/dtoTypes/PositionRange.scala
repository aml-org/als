package common.dtoTypes

import amf.core.parser.{Range => AmfRange}
import org.mulesoft.lexer.InputRange

case class PositionRange(start: Position, end: Position)

object PositionRange {
  def apply(range: AmfRange): PositionRange = PositionRange(Position(range.start), Position(range.end))

  def apply(range: InputRange): PositionRange =
    PositionRange(Position(range.lineFrom, range.columnFrom), Position(range.lineTo, range.columnTo))
}

object EmptyPositionRange extends PositionRange(Position0, Position1)
