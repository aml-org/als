package org.mulesoft.als.common

import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.lexer.InputRange
import org.yaml.model.{YMap, YMapEntry, YNode, YNonContent, YPart}

import scala.annotation.tailrec

object YamlUtils {

  def isKey(maybePart: Option[YPart], amfPosition: Position): Boolean =
    maybePart.exists(part => {
      val selected = getParent(part, amfPosition, None)
      selected match {
        case Some(entry: YMapEntry) =>
          contains(entry.key.range, amfPosition)
        case _ => false
      }
    })

  def contains(range: InputRange, amfPosition: Position): Boolean =
    PositionRange(Position(range.lineFrom, range.columnFrom), Position(range.lineTo, range.columnTo))
      .contains(amfPosition)

  def childWithPosition(s: YPart, amfPosition: Position): Option[YPart] =
    s.children
      .filterNot(_.isInstanceOf[YNonContent])
      .find({
        case entry: YMapEntry => contains(entry.range, amfPosition)
        case map: YMap        => contains(map.range, amfPosition)
        case node: YNode      => contains(node.range, amfPosition)
        case _                => false
      })

  @tailrec
  final def getNodeByPosition(s: YPart, amfPosition: Position): YPart =
    childWithPosition(s, amfPosition) match {
      case Some(c) => getNodeByPosition(c, amfPosition)
      case None    => s
    }

  def getNodeBrothers(s: YPart, amfPosition: Position): Seq[YPart] =
    getParent(s, amfPosition, None).map(_.children).getOrElse(Nil)

  @tailrec
  final def getParent(s: YPart, amfPosition: Position, parent: Option[YPart]): Option[YPart] =
    childWithPosition(s, amfPosition) match {
      case Some(c) =>
        getParent(c, amfPosition, Some(s))
      case None =>
        parent
    }

  def getChildWithNode(children: IndexedSeq[YPart], node: YPart): Option[YPart] =
    children.find(_ == node) match {
      case Some(p) => Some(p)
      case _ =>
        children.find(c => {
          getChildWithNode(c.children, node).isDefined
        })
    }

  @tailrec
  final def getParent(s: YPart, child: YPart, parent: Option[YPart] = None): Option[YPart] =
    getChildWithNode(s.children, child) match {
      case Some(c) =>
        getParent(c, child, Some(s))
      case None =>
        parent
    }
}
