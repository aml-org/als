package org.mulesoft.language.common.dtoTypes

import amf.core.parser.{Position => AmfPosition}

/**
  * @param line   Line position in a document (zero-based).
  * @param column Character offset on a line in a document (zero-based). Assuming that the line is
  *               represented as a string, the `character` value represents the gap between the
  *               `character` and `character + 1`.
  */

case class Position(line: Int, column: Int) {
  def offset(text: String): Int = {
    def innerOffset(lines: List[String], currentLine: Int, currentOffset: Int): Int = lines match {
      case Nil => currentOffset
      case current :: Nil => Math.min(currentOffset + column, currentOffset + current.length - 1)
      case current :: _ if currentLine == line => Math.min(currentOffset + column, currentOffset + current.length - 1)
      case current :: rest => innerOffset(rest, currentLine + 1, currentOffset + current.length)
    }

    if (line < 0 || column < 0) -1 else innerOffset(text.linesWithSeparators.toList, 0, 0)
  }
}

object Position {
  def apply(position: AmfPosition): Position = Position(position.line, position.column)

  def apply(offset: Int, text: String): Position = {
    def toPosition(count: Int, line: Int, lines: List[String]): Position = lines match {
      case Nil => Position(line, 0)
      case currentLine :: Nil => Position(line, Math.min(currentLine.length, offset - count))
      case currentLine :: _ if offset - count < currentLine.length => Position(line, offset - count)
      case current :: rest => toPosition(count + current.length, line + 1, rest)
    }

    offset match {
      case value if value < 0 => Position0
      case _ => toPosition(0, 0, text.linesWithSeparators.toList)
    }
  }
}

object Position0 extends Position(0, 0)

object Position1 extends Position(0, 1)
