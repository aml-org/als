package org.mulesoft.als.actions.selection

import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.convert.LspRangeConverter._
import org.mulesoft.lsp.feature.common
import org.mulesoft.lsp.feature.selectionRange.SelectionRange
import org.yaml.model.{YMapEntry, YPart, YScalar, YSequence}
import org.mulesoft.lsp.feature.common.{Position => LspPosition, Range => LspRange}

object SelectionRangeFinder {
  def findSelectionRange(yPart: YPart, positions: Seq[Position]): Option[Seq[SelectionRange]] = {
    Some(
      positions.map(p => {
        findSelectionRangeFor(yPart, p)
      })
    )
  }

  private def findSelectionRangeFor(yPart: YPart, position: Position): SelectionRange = {
    val range              = PositionRange(yPart.range)
    val rootSelectionRange = SelectionRange(range, None)
    findSelectionRangeFor(yPart.children, position, rootSelectionRange).getOrElse(rootSelectionRange)
  }

  private def findSelectionRangeFor(yPart: Iterable[YPart],
                                    position: Position,
                                    parent: SelectionRange): Option[SelectionRange] = {
    yPart
      .find(p => PositionRange(p.range).contains(position))
      .flatMap(yPart => {
        yPart match {
          case _ @(_: YMapEntry | _: YSequence | _: YScalar) =>
            findSelectionRangeFor(yPart.children, position, SelectionRange(PositionRange(yPart.range), Some(parent)))
          case _ =>
            // We skip this node
            findSelectionRangeFor(yPart.children, position, parent)
        }
      })
      .orElse(Some(parent))
  }

  implicit def lspRange(p: PositionRange): common.Range = toLspRange(p)

}
