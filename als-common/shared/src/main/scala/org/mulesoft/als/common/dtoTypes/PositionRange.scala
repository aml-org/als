package org.mulesoft.als.common.dtoTypes

import amf.core.parser.{Range => AmfRange}
import org.mulesoft.lexer.InputRange

case class PositionRange(start: Position, end: Position) {
  def contains(position: Position): Boolean = position >= start && position <= end

  def intersection(other: PositionRange): Option[PositionRange] =
    if (start > other.end || other.start > end) None
    else Some(PositionRange(Position.max(start, other.start), Position.min(end, other.end)))

  def +(right: PositionRange) = PositionRange(start, right.end)

  override def toString: String = s"[$start-$end]"

  override def equals(obj: Any): Boolean = obj match {
    case pr: PositionRange => pr.start == this.start && pr.end == this.end
    case _                 => false
  }

  def compareTo(other: PositionRange): Int = {
    if (start < other.start) 0
    else if (start == other.start)
      if (end <= other.end) 0 else 1
    else 1
  }
}

object PositionRange {
  def apply(range: AmfRange): PositionRange = PositionRange(Position(range.start), Position(range.end))

  def apply(range: InputRange): PositionRange =
    PositionRange(Position(range.lineFrom - 1, range.columnFrom), Position(range.lineTo - 1, range.columnTo))
}

object EmptyPositionRange extends PositionRange(Position0, Position1)
