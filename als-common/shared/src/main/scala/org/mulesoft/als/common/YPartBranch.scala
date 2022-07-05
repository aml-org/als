package org.mulesoft.als.common

import amf.core.client.common.position.{Position => AmfPosition}
import amf.core.client.scala.model.document.BaseUnit
import amf.core.internal.parser.YNodeLikeOps
import org.mulesoft.als.common.YamlWrapper._
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.amfintegration.AmfImplicits.{AmfAnnotationsImp, BaseUnitImp}
import org.yaml.lexer.YamlCharRules
import org.yaml.model
import org.yaml.model._

import scala.annotation.tailrec

case class YPartBranch(node: YPart, position: AmfPosition, stack: Seq[YPart], isJson: Boolean, isInFlow: Boolean) {

  /** isPlainText means it is a scalar and has no quotation marks
    */
  lazy val isPlainText: Option[Boolean] = node match {
    case n: YNode   => n.asScalar.map(_.plain)
    case s: YScalar => Some(s.plain)
    case _          => None
  }

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
    node.isInstanceOf[YDocument] || node.isInstanceOf[YMap] || node
      .isInstanceOf[YSequence] || (stack.headOption match {
      case Some(entry: YMapEntry) if entry.value == node =>
        entry.value.asScalar.isDefined &&
        node.range.columnFrom > entry.key.range.columnFrom && ((position.line == entry.key.range.lineFrom && node.range.columnTo != entry.value.range.columnTo) || position.line > entry.key.range.lineFrom)
      case Some(entry: YMapEntry) => entry.key == node
      case _                      => false
    })

  lazy val hasIncludeTag: Boolean = node match {
    case mr: YNode.MutRef => mr.origTag.tagType == YType.Include
    case _                => false
  }

  val isValue: Boolean = stack.headOption.exists(_.isInstanceOf[YMapEntry]) && !isKey && !hasIncludeTag

  lazy val isIncludeTagValue: Boolean = stack.headOption.exists(_.isInstanceOf[YMapEntry]) && !isKey && hasIncludeTag

  val isAtRoot: Boolean = node.isInstanceOf[model.YDocument] || (stack.count(_.isInstanceOf[YMap]) == 0 && node
    .isInstanceOf[YMap]) ||
    (stack.count(_.isInstanceOf[YMap]) <= 1 &&
      (closestEntry.exists(
        _.key.range.contains(position)
      ) || // this is the case that you are in a key on root level (before colon)
        isJson))

  val isArray: Boolean = node.isArray
  lazy val isInArray: Boolean =
    getSequence.isDefined

  lazy val isKeyLike: Boolean = isKey || isInArray

  val parent: Option[YPart] = stack.headOption

  lazy val parentMap: Option[YMap] = stack.headOption match {
    case Some(_: YMapEntry) =>
      stack.tail.headOption.collect({ case m: YMap => m })
    case Some(m: YMap) => Some(m)
    case _             => None
  }

  // if writing a key, the current entry is ignored
  private def skipCurrent(stack: Seq[YPart]): Seq[YPart] =
    stack.headOption match {
      case Some(entry: YMapEntry) if entry.key == node => stack.tail
      case _                                           => stack
    }

  /** ignores current entry
    */
  lazy val parentEntry: Option[YMapEntry] =
    findFirstOf(classOf[YMapEntry], skipCurrent(stack))
      .flatMap {
        case value if value.key == node && value.key.asScalar.forall(_.text.isEmpty) =>
          findFirstOf(classOf[YMapEntry], stack.filterNot(_ == value))
        case v => Some(v)
      }

  /** does not ignore the current entry
    */
  lazy val closestEntry: Option[YMapEntry] =
    findFirstOf(classOf[YMapEntry], stack)
      .flatMap {
        case value if value.key == node && value.key.asScalar.forall(_.text.isEmpty) =>
          findFirstOf(classOf[YMapEntry], stack.filterNot(_ == value))
        case v => Some(v)
      }

  def parentEntryIs(key: String): Boolean =
    parentEntry.exists(e => e.key.asScalar.map(_.text).contains(key))

  def getAncestor(l: Int): Option[YPart] =
    stack.lift(l)

  def isKeyDescendantOf(key: String): Boolean =
    isKey && parentEntryIs(key)

  def isValueDescendantOf(key: String): Boolean =
    isValue && parentEntryIs(key)

  def isInBranchOf(key: String): Boolean =
    stack.exists({
      case e: YMapEntry => e.key.asScalar.exists(_.text == key)
      case _            => false
    })

  @scala.annotation.tailrec
  private def findFirstOf[T <: YPart](clazz: Class[T], l: Seq[YPart]): Option[T] = {
    l match {
      case Nil                                 => None
      case head :: _ if clazz.isInstance(head) => Some(head.asInstanceOf[T])
      case _ :: Nil                            => None
      case _ :: tail                           => findFirstOf(clazz, tail)
    }
  }

  // todo: check if this is still relevant, `k:` is long gone
  // content patch will add a { k: }, I need to get up the k node, the k: entry, and the {k: } map
  private def getSequence: Option[YSequence] =
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

  def arraySiblings: Seq[String] =
    getSequence
      .map(_.nodes.flatMap(node => node.asScalar.map(_.text)))
      .getOrElse(Seq())

  // todo remove
  private def getMap: Option[YPart] =
    node match {
      case m: YMap                                => Some(m)
      case _: YNodePlain if isKey && !isEmptyNode => getAncestor(1)
      case _                                      => None
    }

  def brothers: Seq[YPart] = {
    getMap
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
  def getAstForRange(ast: YPart, startPosition: AmfPosition, endPosition: AmfPosition, isJson: Boolean): YPart = {

    val start = build(ast, startPosition, isJson)
    if (startPosition == endPosition) start.node
    else {
      val end = build(ast, endPosition, isJson)

      findMutualYMapParent(start, end, isJson).getOrElse(ast)
    }

  }

  private def findMutualYMapParent(start: YPartBranch, end: YPartBranch, isJson: Boolean): Option[YPart] = {
    start.stack
      .filter(_.isInstanceOf[YMapEntry])
      .find(end.stack.contains(_))
      .map(m => if (isJson) m else m.asInstanceOf[YMapEntry].inMap)
  }

  def build(ast: YPart, position: AmfPosition, isJson: Boolean): YPartBranch = {
    val (stack, inFlow) = getStack(ast, position, Seq(), isJson)
    stack match {
      case actual :: stack => YPartBranch(actual, position, stack, isJson, inFlow)
      case Nil             => YPartBranch(ast, position, Nil, isJson, inFlow)
    }
  }

  def build(bu: BaseUnit, position: AmfPosition, isJson: Boolean): YPartBranch = {
    val ast: Option[YPart] = astFromBaseUnit(bu)
    build(ast.getOrElse(YDocument(IndexedSeq.empty, bu.location().getOrElse(""))), position, isJson)
  }

  def astFromBaseUnit(bu: BaseUnit): Option[YPart] =
    bu.objWithAST.flatMap(_.annotations.ast())

  def containsFlow(s: YPart): Boolean =
    s.children.exists({
      case e: YNonContent => e.tokens.exists(t => t.text != "" && YamlCharRules.isFlowIndicator(t.text.charAt(0)))
      case _              => false
    })

  @tailrec
  private def getStack(
      s: YPart,
      amfPosition: AmfPosition,
      parents: Seq[YPart],
      isInFlow: Boolean = false
  ): (Seq[YPart], Boolean) = {
    val inFlow = isInFlow || containsFlow(s)
    childWithPosition(s, amfPosition, inFlow) match {
      case Some(n: YNode) =>
        childWithPosition(n, amfPosition, inFlow) match {
          case None
              if !n.isNull && n.value.range.lineFrom == amfPosition.line && n.value.range.columnFrom > amfPosition.column =>
            (n +: s +: parents, inFlow) // case for tag (!include)
          case None    => (parents, inFlow)
          case Some(c) => getStack(c, amfPosition, n +: s +: parents, inFlow)
        }
      case Some(c) =>
        getStack(c, amfPosition, s +: parents, inFlow)
      case None if Some(s).collectFirst({ case e: YMapEntry if !isNullOrEmptyTag(e.value) => e }).isDefined =>
        (parents, inFlow)
      case None if s.isInstanceOf[YMapEntry] =>
        getStack(s.asInstanceOf[YMapEntry].value, amfPosition, s +: parents, inFlow)
      case None if s.isInstanceOf[YScalar] =>
        (parents, inFlow)
      case _ => (s +: parents, inFlow)
    }
  }

  private def isNullOrEmptyTag(node: YNode) =
    node.isNull || (node.tagType.toString == "!include")

  def childWithPosition(ast: YPart, amfPosition: AmfPosition, isInFlow: Boolean): Option[YPart] = {
    val parts = ast.children
      .filterNot(_.isInstanceOf[YNonContent])
      .filter { yp =>
        yp.contains(amfPosition, isInFlow)
      }
    if (parts.length > 1) {
      ast match {
        case e: YMapEntry if inKey(e, amfPosition) =>
          val range = e.value.range.toPositionRange
          if (range.end <= Position(amfPosition) && e.value.value.range.toPositionRange.end.line == range.start.line)
            Some(e.value)
          else Some(e.key)
        case _: YSequence => parts.find(p => p.range.lineFrom == amfPosition.line).orElse(parts.headOption)
        case _ =>
          parts.lastOption
      }
    } else
      parts.lastOption
  }

  private def inKey(e: YMapEntry, amfPosition: AmfPosition) = {
    e.value.isNull || e.key.contains(amfPosition)
  }
}
