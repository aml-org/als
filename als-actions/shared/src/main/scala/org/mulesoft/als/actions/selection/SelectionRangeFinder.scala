package org.mulesoft.als.actions.selection

import org.mulesoft.als.common.ASTElementWrapper._
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.convert.LspRangeConverter._
import org.mulesoft.antlrast.ast.Node
import org.mulesoft.common.client.lexical.ASTElement
import org.mulesoft.lsp.feature.selectionRange.SelectionRange
import org.yaml.model.YNode.MutRef
import org.yaml.model._

object SelectionRangeFinder {

  def findSelectionRange(astElement: ASTElement, positions: Seq[Position]): Seq[SelectionRange] = {
    positions.flatMap(p => {
      findSelectionRange(astElement, p, None)
    })
  }

  private def findSelectionRange(
      astElement: ASTElement,
      position: Position,
      parent: Option[SelectionRange]
  ): Option[SelectionRange] = {
    val range              = PositionRange(astElement.location.range)
    val rootSelectionRange = SelectionRange(toLspRange(range), parent)

    findSelectionRangeFor(getChildren(astElement), position, Some(rootSelectionRange)).orElse(parent)
  }

  private def getChildren(astElement: ASTElement) = {
    astElement match {
      case yPart: YPart => yPart.children
      case n: Node      => n.children
      case _            => Nil
    }
  }

  private def findSelectionRangeFor(
      astElements: Iterable[ASTElement],
      position: Position,
      parent: Option[SelectionRange]
  ): Option[SelectionRange] = {
    astElements
      .find(p => p.location.range.toPositionRange.contains(position))
      .flatMap(yPart => {
        yPart match {
          case ref: MutRef =>
            findSelectionRange(ref, position, parent)
          case _ @(_: YMapEntry | _: YSequence) =>
            findSelectionRange(yPart, position, parent)
          case _: YTag =>
            parent
          case _ @(_: YScalar) =>
            Some(SelectionRange(toLspRange(PositionRange(yPart.location.range)), parent))
          case _ =>
            // We skip this node
            findSelectionRangeFor(getChildren(yPart), position, parent)
        }
      })
  }

}
