package org.mulesoft.als.actions.selection

import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.convert.LspRangeConverter._
import org.mulesoft.lexer.InputRange
import org.mulesoft.lsp.feature.common
import org.mulesoft.lsp.feature.selectionRange.SelectionRange
import org.yaml.model.YNode.MutRef
import org.yaml.model.{YMapEntry, YNode, YPart, YScalar, YSequence, YTag}

object SelectionRangeFinder {

  def findSelectionRange(yPart: YPart, positions: Seq[Position]): Seq[SelectionRange] = {
    positions.flatMap(p => {
      findSelectionRange(yPart, p, None)
    })
  }

  private def findSelectionRange(
      yPart: YPart,
      position: Position,
      parent: Option[SelectionRange]
  ): Option[SelectionRange] = {
    val range              = PositionRange(yPart.range)
    val rootSelectionRange = SelectionRange(range, parent)
    findSelectionRangeFor(yPart.children, position, Some(rootSelectionRange)).orElse(parent)
  }

  private def findSelectionRangeFor(
      yPart: Iterable[YPart],
      position: Position,
      parent: Option[SelectionRange]
  ): Option[SelectionRange] = {
    yPart
      .find(p => p.range.contains(position))
      .flatMap(yPart => {
        yPart match {
          case ref: MutRef =>
            findSelectionRange(ref, position, parent)
          case _ @(_: YMapEntry | _: YSequence) =>
            findSelectionRange(yPart, position, parent)
          case _: YTag =>
            parent
          case _ @(_: YScalar) =>
            Some(SelectionRange(PositionRange(yPart.range), parent))
          case _ =>
            // We skip this node
            findSelectionRangeFor(yPart.children, position, parent)
        }
      })
  }

  implicit def lspRange(p: PositionRange): common.Range          = toLspRange(p)
  implicit def inputRangeConverter(p: InputRange): PositionRange = PositionRange(p)
}
