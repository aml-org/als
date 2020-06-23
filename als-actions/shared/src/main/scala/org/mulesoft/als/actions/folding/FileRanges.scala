package org.mulesoft.als.actions.folding

import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.lsp.feature.common.Position
import org.mulesoft.lsp.feature.folding.FoldingRange
import org.yaml.model.YNode.MutRef
import org.yaml.model.{YDocument, YMap, YMapEntry, YNode, YPart, YSequence}

import scala.annotation.tailrec
import scala.collection.mutable

object FileRanges {
  def ranges(yPart: YPart): Seq[FoldingRange] = {
    val list = mutable.ListBuffer[FoldingRange]()
    list += yPart.foldingRange(yPart)
    yPart.children.foreach(c => collectRanges(c, list, yPart))
    list.toList.distinct
  }

  private def collectRanges(yPart: YPart, list: mutable.ListBuffer[FoldingRange], parent: YPart): Unit =
    yPart match {
      case _: MutRef => // ignore
      case d: YDocument =>
        collectRanges(d.node.value, list, yPart)
      case _ @(_: YMapEntry | _: YNode) =>
        yPart.children.foreach(collectRanges(_, list, yPart))
      case _ @(_: YMap | _: YSequence) => // just fold on maps and sequences
        list += yPart.foldingRange(parent)
        yPart.children.foreach(collectRanges(_, list, yPart))
      case _ => // ignore
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
