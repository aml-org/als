package org.mulesoft.als.common

import org.mulesoft.als.common.YamlWrapper._
import org.mulesoft.als.common.dtoTypes.Position
import org.yaml.model._

import scala.annotation.tailrec

case class YPartBranch(node: YPart, position: Position, private val stack: Seq[YPart]) {
  val isKey: Boolean = stack.headOption.exists(_.isKey(position))

  lazy val hasIncludeTag: Boolean = node match {
    case mr: YNode.MutRef => mr.origTag.tagType == YType.Include
    case _                => false
  }

  val isValue: Boolean = stack.headOption.exists(_.isInstanceOf[YMapEntry]) && !isKey

  val isAtRoot: Boolean = stack.length <= 2
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

  lazy val parentEntry: Option[YMapEntry] = findFirstOf(classOf[YMapEntry], stack)

  def parentEntryIs(key: String): Boolean = parentEntry.exists(e => e.key.asScalar.map(_.text).contains(key))

  def getAncestor(l: Int): Option[YPart] = if (stack.length < l) None else Some(stack(l))

  def ancestorOf[T <: YPart](clazz: Class[T]): Option[T] = findFirstOf(clazz, stack.tail)

  private def findFirstOf[T <: YPart](clazz: Class[T], l: Seq[YPart]): Option[T] = {
    l match {
      case head :: _ if clazz.isInstance(head) => Some(head.asInstanceOf[T])
      case head :: Nil                         => None
      case _ :: tail                           => findFirstOf(clazz, tail)
    }
  }

  // content patch will add a { k: }, I need to get up the k node, the k: entry, and the {k: } map
  private def getSequence: Option[YSequence] = {
    val offset = if (isKey) 4 else 0
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

  def build(ast: YPart, position: Position): YPartBranch = {
    val actual :: stack = getStack(ast, position, Seq())
    YPartBranch(actual, position, stack)
  }

  @tailrec
  private def getStack(s: YPart, amfPosition: Position, parents: Seq[YPart]): Seq[YPart] =
    childWithPosition(s, amfPosition) match {
      case Some(c) =>
        getStack(c, amfPosition, s +: parents)
      case None if s.isInstanceOf[YMapEntry] =>
        getStack(s.asInstanceOf[YMapEntry].value, amfPosition, s +: parents)
      case None if s.isInstanceOf[YScalar] =>
        parents
      case _ => s +: parents
    }

  private def childWithPosition(ast: YPart, amfPosition: Position): Option[YPart] =
    ast.children
      .filterNot(_.isInstanceOf[YNonContent])
      .filter {
        case entry: YMapEntry =>
          entry.range.toPositionRange.contains(amfPosition)
        case map: YMap      => map.range.toPositionRange.contains(amfPosition)
        case node: YNode    => node.range.toPositionRange.contains(amfPosition)
        case seq: YSequence => seq.range.toPositionRange.contains(amfPosition)
        case _              => false
      }
      .lastOption

}
