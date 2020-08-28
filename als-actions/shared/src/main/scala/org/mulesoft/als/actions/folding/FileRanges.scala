package org.mulesoft.als.actions.folding

import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.lsp.feature.common.Position
import org.mulesoft.lsp.feature.folding.FoldingRange
import org.yaml.model.YNode.MutRef
import org.yaml.model._

import scala.annotation.tailrec

object FileRanges {
  def ranges(yPart: YPart): Seq[FoldingRange] = collectRanges(yPart, yPart)

  private def collectRanges(yPart: YPart, parent: YPart): Seq[FoldingRange] =
    yPart match {
      case _: MutRef => Seq.empty
      case d: YDocument =>
        collectRanges(d.node.value, d.node.value)
      case _ @(_: YMapEntry | _: YNode) =>
        yPart.children.flatMap(collectRanges(_, yPart))
      case _ @(_: YMap | _: YSequence) => // just fold on maps and sequences
        yPart.foldingRange(parent) +: yPart.children.flatMap(collectRanges(_, yPart))
      case _ => Seq.empty
    }

  implicit class YPartRange(yPart: YPart) {
    def foldingRange(parent: YPart): FoldingRange = {
      val start = LspRangeConverter.toLspRange(PositionRange(parent.range)).start
      val end   = lastChildEnd()
      FoldingRange(start.line, Some(start.character), end.line, Some(end.character), None)
    }

    private def lastChildEnd(): Position =
      LspRangeConverter.toLspRange(PositionRange(yPart.getLastChild().range)).end

    @tailrec
    final def getLastChild(): YPart =
      if (yPart.children.nonEmpty)
        yPart.children.last.getLastChild()
      else
        yPart
  }
}
