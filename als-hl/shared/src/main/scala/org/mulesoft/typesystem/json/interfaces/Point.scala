package org.mulesoft.typesystem.json.interfaces

import org.mulesoft.als.common.dtoTypes.Position

trait Point {

  def line: Int

  def column: Int

  def position: Int

  def resolved: Boolean = position >= 0

  def toPosition: Position = Position(line, column)
}
