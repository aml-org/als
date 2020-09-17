package org.mulesoft.als.common.dtoTypes

import amf.core.parser.{Range => AmfRange}
import org.mulesoft.lexer.InputRange
import org.mulesoft.lsp.feature.common.{Range => LspRange}

case class PositionRange(start: Position, end: Position) {
  def contains(position: Position): Boolean = position >= start && position <= end

  def containsNotEndField(position: Position): Boolean =
    containsNotEndObj(position) && !(end.column == 0 && position.column > 0)

  def containsNotEndObj(position: Position): Boolean =
    position >= start && position <= end && !(start.line < position.line && (end.line == position.line && position.column == end.column ||
      end.line == position.line + 1 && end.column == 0 && position.column == 0))

  def intersection(other: PositionRange): Option[PositionRange] =
    if (start > other.end || other.start > end) None
    else Some(PositionRange(Position.max(start, other.start), Position.min(end, other.end)))

  def +(right: PositionRange) = PositionRange(start min right.start, end max right.end)

  override def toString: String = s"[$start-$end]"

  override def equals(obj: Any): Boolean = obj match {
    case pr: PositionRange => pr.start == this.start && pr.end == this.end
    case _                 => false
  }

  override def hashCode(): Int = super.hashCode()

  def compareTo(other: PositionRange): Int = {
    if (start < other.start) 0
    else if (start == other.start)
      if (end <= other.end) 0 else 1
    else 1
  }
}

object PositionRange {
  def apply(range: AmfRange): PositionRange = PositionRange(Position(range.start), Position(range.end))
  def apply(range: LspRange): PositionRange = PositionRange(Position(range.start), Position(range.end))

  def apply(range: InputRange): PositionRange =
    PositionRange(Position(range.lineFrom - 1, range.columnFrom), Position(range.lineTo - 1, range.columnTo))
}

object EmptyPositionRange extends PositionRange(Position0, Position1)
