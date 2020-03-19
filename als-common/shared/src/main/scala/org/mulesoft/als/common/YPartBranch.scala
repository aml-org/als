package org.mulesoft.als.common

import amf.core.annotations.SourceAST
import amf.core.model.document.{BaseUnit, Document}
import org.mulesoft.als.common.YamlWrapper._
import org.yaml.model._
import org.mulesoft.als.common.dtoTypes.Position
import amf.core.parser.{Position => AmfPosition}
import amf.core.parser._

import scala.annotation.tailrec

case class YPartBranch(node: YPart, position: AmfPosition, stack: Seq[YPart], isJson: Boolean) {

  lazy val isMultiline: Boolean = node match {
    case n: YNode if n.asScalar.isDefined => n.asScalar.exists(_.mark == MultilineMark)
    case _                                => false
  }

  val isEmptyNode: Boolean = node match {
    case n: YNode => n.tagType == YType.Null
    case _        => false
  }

  lazy val stringValue: String = node match {
    case n: YNode =>
      n.toOption[YScalar] match {
        case Some(s) => s.text
        case _       => n.toString
      }
    case _ => node.toString
  }

  lazy val tag: Option[YTag] = node match {
    case n: YNode => Some(n.tag)
    case _        => None
  }

  def getMark: Option[ScalarMark] = node match {
    case n: YNode => n.asScalar.map(_.mark)
    case _        => None
  }

  def keys: Seq[String] = stack.flatMap {
    case e: YMapEntry => e.key.asScalar.map(_.text)
    case _            => None
  }

  val isKey: Boolean = stack.headOption.exists(_.isKey(position)) || (isJson && node.isInstanceOf[YMap])

  lazy val hasIncludeTag: Boolean = node match {
    case mr: YNode.MutRef => mr.origTag.tagType == YType.Include
    case _                => false
  }

  val isValue: Boolean = stack.headOption.exists(_.isInstanceOf[YMapEntry]) && !isKey && !hasIncludeTag

  lazy val isIncludeTagValue: Boolean = stack.headOption.exists(_.isInstanceOf[YMapEntry]) && !isKey && hasIncludeTag

  val isAtRoot: Boolean = stack.count(_.isInstanceOf[YMap]) <= 1
  val isArray: Boolean  = node.isArray
  lazy val isInArray: Boolean =
    getSequence.isDefined

  val parent: Option[YPart] = stack.headOption

  lazy val parentMap: Option[YMap] = stack.headOption match {
    case Some(e: YMapEntry) =>
      stack.tail.headOption.collect({ case m: YMap => m })
    case Some(m: YMap) => Some(m)
    case _             => None
  }

  lazy val parentEntry: Option[YMapEntry] =
    findFirstOf(classOf[YMapEntry], stack)

  def parentEntryIs(key: String): Boolean =
    parentEntry.exists(e => e.key.asScalar.map(_.text).contains(key))

  def getAncestor(l: Int): Option[YPart] =
    if (stack.length < l) None else Some(stack(l))

  def ancestorOf[T <: YPart](clazz: Class[T]): Option[T] =
    if (stack.nonEmpty) findFirstOf(clazz, stack.tail) else None

  def isKeyDescendantOf(key: String): Boolean =
    isKey && ancestorOf(classOf[YMapEntry])
      .flatMap(_.key.asScalar.map(_.text))
      .contains(key)

  def isValueDescendanceOf(key: String): Boolean =
    isValue && ancestorOf(classOf[YMapEntry])
      .flatMap(_.key.asScalar.map(_.text))
      .contains(key)

  @scala.annotation.tailrec
  private def findFirstOf[T <: YPart](clazz: Class[T], l: Seq[YPart]): Option[T] = {
    l match {
      case head :: _ if clazz.isInstance(head) => Some(head.asInstanceOf[T])
      case head :: Nil                         => None
      case _ :: tail                           => findFirstOf(clazz, tail)
    }
  }

  // content patch will add a { k: }, I need to get up the k node, the k: entry, and the {k: } map
  private def getSequence: Option[YSequence] = {
    val offset = if (isKey) 4 else if (isArray) 0 else 1
    stack.drop(offset).headOption match {
      case Some(node: YNode) =>
        node.value match {
          case s: YSequence => Some(s)
          case _            => None
        }
      case _ => None
    }
  }

  def arraySiblings: Seq[String] =
    getSequence
      .map(_.nodes.flatMap(node => node.asScalar.map(_.text)))
      .getOrElse(Seq())

  def brothers: Seq[YPart] = {
    val map = stack.headOption match {
      case Some(entry: YMapEntry) => stack.tail.headOption
      case Some(m: YMap)          => Some(m)
      case _                      => None
    }
    map
      .map(_.children.filterNot(_.isInstanceOf[YNonContent]).filterNot {
        case e: YMapEntry => e.key == node
        case c            => c == node
      })
      .getOrElse(Nil)
  }

  def brothersKeys: Set[String] =
    brothers
      .flatMap({
        case yme: YMapEntry => yme.key.asScalar.map(_.text)
        case _              => None
      })
      .toSet
}

object NodeBranchBuilder {

  def build(ast: YPart, position: AmfPosition, isJson: Boolean): YPartBranch = {
    val actual :: stack = getStack(ast, position, Seq())
    YPartBranch(actual, position, stack, isJson)
  }

  def build(bu: BaseUnit, position: AmfPosition, isJson: Boolean): YPartBranch = {
    val ast: Option[YPart] = astFromBaseUnit(bu)
    build(ast.getOrElse(YDocument(IndexedSeq.empty, bu.location().getOrElse(""))), position, isJson)
  }

  def astFromBaseUnit(bu: BaseUnit): Option[YPart] = {
    val ast = bu match {
      case d: Document =>
        d.encodes.annotations.find(classOf[SourceAST]).map(_.ast)
      case bu => bu.annotations.find(classOf[SourceAST]).map(_.ast)
    }
    ast
  }

  @tailrec
  private def getStack(s: YPart, amfPosition: AmfPosition, parents: Seq[YPart]): Seq[YPart] =
    childWithPosition(s, amfPosition) match {
      case Some(c) =>
        getStack(c, amfPosition, s +: parents)
      case None if s.isInstanceOf[YMapEntry] =>
        getStack(s.asInstanceOf[YMapEntry].value, amfPosition, s +: parents)
      case None if s.isInstanceOf[YScalar] =>
        parents
      case _ => s +: parents
    }

  private def childWithPosition(ast: YPart, amfPosition: AmfPosition): Option[YPart] =
    ast.children
      .filterNot(_.isInstanceOf[YNonContent])
      .filter {
        case entry: YMapEntry =>
          entry.range.toPositionRange.contains(Position(amfPosition))
        case map: YMap      => map.range.toPositionRange.contains(Position(amfPosition))
        case node: YNode    => node.range.toPositionRange.contains(Position(amfPosition))
        case seq: YSequence => seq.range.toPositionRange.contains(Position(amfPosition))
        case _              => false
      }
      .lastOption

}
