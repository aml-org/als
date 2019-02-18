package org.mulesoft.language.server.lsp4j

import common.dtoTypes.{Position, PositionRange}
import org.eclipse.lsp4j.{Position => Lsp4JPosition, Range => Lsp4JRange}

import scala.language.implicitConversions

object Lsp4JConversions {

  implicit def lsp4JPosition(position: Position): Lsp4JPosition = new Lsp4JPosition(position.line, position.column)

  implicit def position(position: Lsp4JPosition): Position = new Position(position.getLine, position.getCharacter)

  implicit def lsp4JRange(range: PositionRange)(implicit lsp4JPosition: Position => Lsp4JPosition): Lsp4JRange =
    new Lsp4JRange(lsp4JPosition(range.start), lsp4JPosition(range.end))
}
