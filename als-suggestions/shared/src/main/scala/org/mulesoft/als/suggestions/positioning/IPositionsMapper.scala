package org.mulesoft.als.suggestions.positioning

import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.suggestions.interfaces.Point
import org.mulesoft.als.suggestions.positioning.json.NodeRange

trait IPositionsMapper {

  def initRange(range: NodeRange): Unit

  def initPoint(point: Point): Unit

  def mapToPosition(line: Int, column: Int): Int

  def offset(position: Int): Int

  def offset(position: Position): Int = mapToPosition(position.line, position.column)

  def lineOffset(str: String): Int

  def point(position: Int): Point

  def lineString(line: Int): Option[String]

  def getText: String

  def textLength: Int

  def line(lineIndex: Int): Option[String]

  def lineContainingPosition(position: Int): Option[String]

  val uri: String
}
