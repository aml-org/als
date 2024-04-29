package org.mulesoft.als.actions.folding

import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.common.client.lexical.ASTElement
import org.mulesoft.lsp.feature.common.Position
import org.mulesoft.lsp.feature.folding.FoldingRange
import org.yaml.model
import org.yaml.model.YNode.MutRef
import org.yaml.model._

import scala.annotation.tailrec

object FileRanges {
  def ranges(astElement: ASTElement): Seq[FoldingRange] = collectRanges(astElement, astElement)

  private def collectRanges(astElement: ASTElement, parent: ASTElement): Seq[FoldingRange] =
    astElement match {
      case _: MutRef => Seq.empty
      case d: YDocument =>
        collectRanges(d.node.value, d.node.value)
      case yPart @ (_: YMapEntry | _: YNode) =>
        yPart.asInstanceOf[YPart].children.flatMap(collectRanges(_, yPart))
      case _ @(_: YMap | _: YSequence) => // just fold on maps and sequences
        val part = astElement.asInstanceOf[YPart]
        part.foldingRange(parent.asInstanceOf[YPart]) +: part.children.flatMap(collectRanges(_, part))
      case _ => Seq.empty
    }

  implicit class YPartRange(yPart: YPart) {
    def foldingRange(parent: YPart): FoldingRange = {
      val start = LspRangeConverter.toLspRange(PositionRange(parent.range)).start
      val end   = lastChildEnd()
      FoldingRange(start.line, Some(start.character), end.line, Some(end.character), None)
    }

    private def lastChildEnd(): Position =
      LspRangeConverter.toLspRange(PositionRange(yPart.getLastChild.range)).end

    @tailrec
    final def getLastChild: YPart = {
      val children = yPart.children.filter(p => !p.isInstanceOf[YNonContent])
      if (children.nonEmpty)
        children.last.getLastChild
      else
        yPart
    }
  }
}
