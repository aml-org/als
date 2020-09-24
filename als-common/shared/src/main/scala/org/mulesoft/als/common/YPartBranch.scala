package org.mulesoft.als.common

import amf.core.annotations.SourceAST
import amf.core.model.document.{BaseUnit, Document}
import org.mulesoft.als.common.YamlWrapper._
import org.yaml.model._
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import amf.core.parser.{Position => AmfPosition}
import amf.core.parser._
import YamlWrapper._
import org.mulesoft.amfintegration.AmfImplicits.{AmfAnnotationsImp, BaseUnitImp}
import org.yaml.model

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

  val isKey: Boolean =
    if (isJson) stack.headOption.exists(_.isKey(position)) || node.isInstanceOf[YMap]
    else {
      node.isInstanceOf[YDocument] || node.isInstanceOf[YMap] || node
        .isInstanceOf[YSequence] || (stack.headOption match {
        case Some(entry: YMapEntry) if entry.value == node =>
          entry.value.asScalar.isDefined &&
            node.range.columnFrom > entry.key.range.columnFrom && position.line > entry.key.range.lineFrom
        case Some(entry: YMapEntry) => entry.key == node
        case _                      => false
      })
    }

  lazy val hasIncludeTag: Boolean = node match {
    case mr: YNode.MutRef => mr.origTag.tagType == YType.Include
    case _                => false
  }

  val isValue: Boolean = stack.headOption.exists(_.isInstanceOf[YMapEntry]) && !isKey && !hasIncludeTag

  lazy val isIncludeTagValue: Boolean = stack.headOption.exists(_.isInstanceOf[YMapEntry]) && !isKey && hasIncludeTag

  val isAtRoot: Boolean = node.isInstanceOf[model.YDocument] || (stack.count(_.isInstanceOf[YMap]) == 0 && node
    .isInstanceOf[YMap]) ||
    (stack.count(_.isInstanceOf[YMap]) <= 1 &&
      (parentEntry.exists(_.key.range.contains(position)) || // this is the case that you are in a key on root level (before colon)
        isJson))

  val isArray: Boolean = node.isArray
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
    if (isJson) ancestorOfJson(clazz) else if (stack.nonEmpty) findFirstOf(clazz, stack) else None

  def ancestorOfJson[T <: YPart](clazz: Class[T]): Option[T] =
    if (stack.nonEmpty) findFirstOf(clazz, stack.tail) else None

  def isKeyDescendantOf(key: String): Boolean =
    isKey && isDescendanceOf(key)

  def isValueDescendanceOf(key: String): Boolean =
    isValue && isDescendanceOf(key)

  def isInBranchOf(key: String): Boolean =
    stack.exists({
      case e: YMapEntry => e.key.asScalar.exists(_.text == key)
      case _            => false
    })

  def isDescendanceOf(key: String): Boolean =
    ancestorOf(classOf[YMapEntry])
      .flatMap(_.key.asScalar.map(_.text))
      .contains(key)
  @scala.annotation.tailrec
  private def findFirstOf[T <: YPart](clazz: Class[T], l: Seq[YPart]): Option[T] = {
    l match {
      case Nil                                 => None
      case head :: _ if clazz.isInstance(head) => Some(head.asInstanceOf[T])
      case head :: Nil                         => None
      case _ :: tail                           => findFirstOf(clazz, tail)
    }
  }

  // content patch will add a { k: }, I need to get up the k node, the k: entry, and the {k: } map
  private def getSequence: Option[YSequence] = {
    if (isArray) Some(node).collectFirst({ case s: YSequence => s })
    else {
      val offset = if (isKey) 4 else 1
      stack.drop(offset).headOption match {
        case Some(node: YNode) =>
          node.value match {
            case s: YSequence => Some(s)
            case _            => None
          }
        case _ => None
      }
    }
  }

  def arraySiblings: Seq[String] =
    getSequence
      .map(_.nodes.flatMap(node => node.asScalar.map(_.text)))
      .getOrElse(Seq())

  // todo remove
  private def getMap(): Option[YPart] = {
    if (isJson) {
      stack.headOption match {
        case Some(entry: YMapEntry) => stack.tail.headOption
        case Some(m: YMap)          => Some(m)
        case _                      => None
      }
    } else {
      node match {
        case m: YMap => Some(m)
        case _       => None
      }
    }
  }

  def brothers: Seq[YPart] = {
    getMap()
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

  def build(ast: YPart, position: AmfPosition, isJson: Boolean): YPartBranch =
    getStack(ast, position, Seq()) match {
      case actual :: stack => YPartBranch(actual, position, stack, isJson)
      case Nil             => YPartBranch(ast, position, Nil, isJson)
    }

  def build(bu: BaseUnit, position: AmfPosition, isJson: Boolean): YPartBranch = {
    val ast: Option[YPart] = astFromBaseUnit(bu)
    build(ast.getOrElse(YDocument(IndexedSeq.empty, bu.location().getOrElse(""))), position, isJson)
  }

  def astFromBaseUnit(bu: BaseUnit): Option[YPart] =
    bu.objWithAST.flatMap(_.annotations.ast())

  @tailrec
  private def getStack(s: YPart, amfPosition: AmfPosition, parents: Seq[YPart]): Seq[YPart] =
    childWithPosition(s, amfPosition) match {
      case Some(n: YNode) =>
        childWithPosition(n, amfPosition) match {
          case None
              if !n.isNull && n.value.range.lineFrom == amfPosition.line && n.value.range.columnFrom > amfPosition.column =>
            n +: s +: parents // case for tag (!include)
          case None    => parents
          case Some(c) => getStack(c, amfPosition, n +: s +: parents)
        }
      case Some(c) =>
        getStack(c, amfPosition, s +: parents)
      case None if Some(s).collectFirst({ case e: YMapEntry if !isNullOrEmptyTag(e.value) => e }).isDefined =>
        parents
      case None if s.isInstanceOf[YMapEntry] =>
        getStack(s.asInstanceOf[YMapEntry].value, amfPosition, s +: parents)
      case None if s.isInstanceOf[YScalar] =>
        parents
      case _ => s +: parents
    }

  private def isNullOrEmptyTag(node: YNode) =
    node.isNull || (node.tagType.toString == "!include" && node.value.toString.isEmpty)

  def childWithPosition(ast: YPart, amfPosition: AmfPosition): Option[YPart] = {
    val parts = ast.children
      .filterNot(_.isInstanceOf[YNonContent])
      .filter { yp =>
        yp.contains(amfPosition)
      }
    if (parts.length > 1) {
      ast match {
        case e: YMapEntry if e.value.isNull => Some(e.key)
        case _                              => parts.lastOption
      }
    } else parts.lastOption
  }

}
