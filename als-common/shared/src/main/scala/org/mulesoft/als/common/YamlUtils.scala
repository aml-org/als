package org.mulesoft.als.common

import amf.core.model.document.BaseUnit
import amf.core.parser.{Position => AmfPosition}
import amf.core.remote.FileMediaType
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.lexer.InputRange
import org.yaml.model.{YMap, YMapEntry, YNode, YNonContent, YPart, YScalar}

import scala.annotation.tailrec

object YamlUtils {

  def isKey(maybePart: Option[YPart], position: AmfPosition): Boolean =
    maybePart.exists(part => {
      val selected = getParent(part, position, None)
      selected match {
        case Some(entry: YMapEntry) =>
          contains(entry.key.range, position)
        case _ => false
      }
    })

  def contains(range: InputRange, position: AmfPosition): Boolean =
    PositionRange(range)
      .contains(Position(position))

  def childWithPosition(s: YPart, position: AmfPosition): Option[YPart] =
    s.children
      .filterNot(_.isInstanceOf[YNonContent])
      .find({
        case entry: YMapEntry => contains(entry.range, position)
        case map: YMap        => contains(map.range, position)
        case node: YNode      => contains(node.range, position)
        case node: YScalar    => contains(node.range, position)
        case _                => false
      })

  @tailrec
  final def getNodeByPosition(s: YPart, position: AmfPosition): YPart =
    childWithPosition(s, position) match {
      case Some(c) => getNodeByPosition(c, position)
      case None    => s
    }

  def getNodeBrothers(s: YPart, position: AmfPosition): Seq[YPart] =
    getParent(s, position, None).map(_.children).getOrElse(Nil)

  @tailrec
  final def getParent(s: YPart, position: AmfPosition, parent: Option[YPart]): Option[YPart] =
    childWithPosition(s, position) match {
      case Some(c) =>
        getParent(c, position, Some(s))
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

  def isJson(baseUnit: BaseUnit) =
    FileMediaType
      .extension(baseUnit.location().getOrElse(baseUnit.id)) match {
      case Some("json") => true
      case _            => false
    }
}
