package org.mulesoft.als.common.dtoTypes

import amf.core.parser.{Position => AmfPosition}

import scala.annotation.tailrec

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
      case current :: Nil =>
        Math.min(currentOffset + column, currentOffset + current.length)
      case current :: _ if currentLine == line =>
        Math.min(currentOffset + column, currentOffset + current.length)
      case current :: rest =>
        innerOffset(rest, currentLine + 1, currentOffset + current.length)
    }

    if (line < 0 || column < 0) -1
    else innerOffset(TextHelper.linesWithSeparators(text), 0, 0)
  }

  def <(other: Position): Boolean =
    (line < other.line) || (line == other.line && column < other.column)

  def <=(other: Position): Boolean =
    (line < other.line) || (line == other.line && column <= other.column)

  def >(other: Position): Boolean =
    (line > other.line) || (line == other.line && column > other.column)

  def >=(other: Position): Boolean =
    (line > other.line) || (line == other.line && column >= other.column)

  def ==(other: Position): Boolean =
    line == other.line && column == other.column

  def moveColumn(value: Int): Position = copy(column = column + value)

  def moveLine(value: Int): Position = copy(line = line + value)

  override def toString: String = s"($line,$column)"

  override def equals(obj: Any): Boolean = obj match {
    case p: Position => p.column == column && p.line == line
    case _           => false
  }
}

object Position {
  def apply(position: AmfPosition): Position =
    Position(position.line - 1, position.column)

  def apply(offset: Int, text: String): Position = {
    def toPosition(count: Int, line: Int, lines: List[String]): Position =
      lines match {
        case Nil => Position(line, 0)
        case currentLine :: Nil =>
          Position(line, Math.min(currentLine.length, offset - count))
        case currentLine :: _ if offset - count < currentLine.length =>
          Position(line, offset - count)
        case current :: rest =>
          toPosition(count + current.length, line + 1, rest)
      }

    offset match {
      case value if value < 0 => Position0
      case _                  => toPosition(0, 0, TextHelper.linesWithSeparators(text))
    }
  }

  def min(first: Position, second: Position): Position =
    if (second < first) second else first

  def max(first: Position, second: Position): Position =
    if (second > first) second else first
}

private object TextHelper {
  def linesWithSeparators(text: String): List[String] = {
    def isBreakLine(head: Char): Boolean = head == '\n'

    def addToResult(result: List[StringBuilder], head: Char): List[StringBuilder] = {
      result.last.append(head)
      if (isBreakLine(head)) result :+ new StringBuilder else result
    }
    @tailrec def innerFn(result: List[StringBuilder], rest: List[Char]): List[StringBuilder] = rest match {
      case Nil          => result
      case head :: Nil  => addToResult(result, head)
      case head :: tail => innerFn(addToResult(result, head), tail)
    }

    innerFn(List(new StringBuilder), text.toList)
      .map(_.mkString)
  }
}

object Position0 extends Position(0, 0)

object Position1 extends Position(0, 1)
