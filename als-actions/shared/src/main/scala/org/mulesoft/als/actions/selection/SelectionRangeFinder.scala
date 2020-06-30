package org.mulesoft.als.actions.selection

import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.convert.LspRangeConverter._
import org.mulesoft.lexer.InputRange
import org.mulesoft.lsp.feature.common
import org.mulesoft.lsp.feature.selectionRange.SelectionRange
import org.yaml.model.{YMapEntry, YPart, YScalar, YSequence}

object SelectionRangeFinder {

  def findSelectionRange(yPart: YPart, positions: Seq[Position]): Option[Seq[SelectionRange]] = {
    Some(findSelectionRange(yPart, positions, None))
  }

  private def findSelectionRange(yPart: YPart,
                                 positions: Seq[Position],
                                 parent: Option[SelectionRange]): Seq[SelectionRange] = {
    val range = PositionRange(yPart.range)
    findSelectionRangeFor(yPart.children, positions, SelectionRange(range, parent))
  }

  private def findSelectionRangeFor(yParts: Seq[YPart],
                                    positions: Seq[Position],
                                    parent: SelectionRange): Seq[SelectionRange] = {
    // The idea is to solve all the selections for the positions in only 1 pass through the AST
    yParts.flatMap(yPart => {
      val containedPositions: Seq[Position] = positions.filter(yPart.range.contains)
      if (containedPositions.nonEmpty) {
        yPart match {
          case _ @(_: YMapEntry | _: YSequence) =>
            findSelectionRange(yPart, containedPositions, Some(parent))
          case _: YScalar =>
            Seq(SelectionRange(PositionRange(yPart.range), Some(parent))) // This will get flatted
          case _ =>
            // We skip this node
            findSelectionRangeFor(yPart.children, containedPositions, parent)
        }
      } else None
    })
  }

  implicit def lspRange(p: PositionRange): common.Range          = toLspRange(p)
  implicit def inputRangeConverter(p: InputRange): PositionRange = PositionRange(p)
}
