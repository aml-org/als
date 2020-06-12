package org.mulesoft.als.convert

import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.lsp.feature.common.{Position => LspPosition, Range => LspRange}

object LspRangeConverter {

  def toPosition(position: LspPosition): Position = Position(position.line, position.character)

  def toLspPosition(position: Position): LspPosition = LspPosition(position.line, position.column)

  def toLspRange(position: PositionRange): LspRange =
    LspRange(toLspPosition(position.start), toLspPosition(position.end))
}
