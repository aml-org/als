package org.mulesoft.als.actions.selection

import amf.core.model.document.BaseUnit
import amf.core.model.domain.AmfObject
import org.mulesoft.als.common.{ObjectInTree, ObjectInTreeBuilder}
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.convert.LspRangeConverter._
import org.mulesoft.lexer.InputRange
import org.mulesoft.lsp.feature.common
import org.mulesoft.lsp.feature.selectionRange.SelectionRange
import org.yaml.model.YNode.MutRef
import org.yaml.model.{YDocument, YMap, YMapEntry, YNode, YNodePlain, YPart, YSequence}

object SelectionRangeFinder {
  def findSelectionRange(yPart: YPart, positions: Seq[Position]): Option[Seq[SelectionRange]] = {
    Some(
      positions.map(p => {
        findSelectionRangeFor(yPart, p)
      })
    )
  }

  def findSelectionRangeFor(yPart: YPart, position: Position): SelectionRange = {
    val range              = PositionRange(yPart.range)
    val rootSelectionRange = SelectionRange(range, None)
    findSelectionRangeFor(yPart.children, position, rootSelectionRange).getOrElse(rootSelectionRange)
  }

  def findSelectionRangeFor(yPart: Iterable[YPart],
                            position: Position,
                            parent: SelectionRange): Option[SelectionRange] = {
    yPart
      .find(p => PositionRange(p.range).contains(position))
      .flatMap(y => {
        y match {
          case _ @(_: YNodePlain) =>
            findSelectionRangeFor(y.children, position, SelectionRange(PositionRange(y.range), Some(parent)))
          case _ =>
            // We skip this node
            findSelectionRangeFor(y.children, position, parent)
        }
      })
      .orElse(Some(parent))
  }

  implicit def lspRange(p: PositionRange): common.Range = toLspRange(p)
}
