package org.mulesoft.als.suggestions.positioning.json

import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.suggestions.interfaces.Point

trait NodeRange {

  def start: Point

  def end: Point

  def toPositionRange: PositionRange = PositionRange(start.toPosition, end.toPosition)

  def containsPosition(pos: Int): Boolean = start.position <= pos && end.position > pos

  def containsPosition(position: Position): Boolean = {
    toPositionRange.contains(position)
  }
}
