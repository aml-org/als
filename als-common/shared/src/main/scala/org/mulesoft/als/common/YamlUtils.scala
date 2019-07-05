package org.mulesoft.als.common

import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.lexer.InputRange
import org.yaml.model._

import scala.annotation.tailrec

object YamlUtils {
  def isArray(selectedNode: Option[YPart], amfPosition: Position): Boolean =
    selectedNode.exists(_.isInstanceOf[YSequence])

  def isInArray(parents: Seq[YPart], amfPosition: Position): Boolean = {
    @tailrec
    def inner(parents: Seq[YPart]): Boolean =
      parents.headOption match {
        case Some(_: YSequence) => true
        case Some(_: YMapEntry) => false
        case Some(_)            => inner(parents.tail)
        case _                  => false
      }
    parents.headOption match {
      case Some(parent: YMapEntry) =>
        contains(parent.key.range, amfPosition) && inner(parents.tail)
      case _ => inner(parents)
    }
  }

  def isKey(parent: Option[YPart], amfPosition: Position): Boolean =
    parent match {
      case Some(entry: YMapEntry) =>
        contains(entry.key.range, amfPosition)
      case _ => false
    }

  def contains(range: InputRange, amfPosition: Position): Boolean =
    PositionRange(Position(range.lineFrom, range.columnFrom), Position(range.lineTo, range.columnTo))
      .contains(amfPosition)

  private def childWithPosition(s: YPart, amfPosition: Position): Option[YPart] =
    s.children
      .filterNot(_.isInstanceOf[YNonContent])
      .filter {
        case entry: YMapEntry => contains(entry.range, amfPosition)
        case map: YMap        => contains(map.range, amfPosition)
        case node: YNode      => contains(node.range, amfPosition)
        case seq: YSequence   => contains(seq.range, amfPosition)
        case _                => false
      }
      .lastOption
  // it should be the last, because of cases in which ranges go from NodeA[(1,0)(2,0)] and NodeB[(2,0)(2,3)],
  // for position (2,0), the last one is the one containing the pointer

  @tailrec
  final def getNodeByPosition(s: YPart, amfPosition: Position): YPart =
    childWithPosition(s, amfPosition) match {
      case Some(c) => getNodeByPosition(c, amfPosition)
      case None    => s
    }

  def getNodeBrothers(s: YPart, amfPosition: Position): Seq[YPart] =
    getParents(s, amfPosition, Seq()).tail.headOption
      .map(_.children)
      .getOrElse(Nil)

  @tailrec
  final def getParents(s: YPart, amfPosition: Position, parents: Seq[YPart]): Seq[YPart] =
    childWithPosition(s, amfPosition) match {
      case Some(c) =>
        getParents(c, amfPosition, s +: parents)
      case None =>
        parents
    }
}
