package org.mulesoft.als.common

import amf.core.client.scala.model.document.BaseUnit
import amf.core.internal.parser.YNodeLikeOps
import org.mulesoft.als.common.ASTElementWrapper._
import org.mulesoft.als.common.YPartASTWrapper._
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.amfintegration.AmfImplicits.{AmfAnnotationsImp, BaseUnitImp}
import org.mulesoft.antlrast.ast.{ASTNode, Node, Terminal}
import org.mulesoft.common.client.lexical.{ASTElement, Position => AmfPosition}
import org.yaml.lexer.{YamlCharRules, YamlToken}
import org.yaml.lexer.YamlToken.Indicator
import org.yaml.model
import org.yaml.model._

import scala.annotation.tailrec

case class YPartBranch(
    override val node: YPart,
    override val position: AmfPosition,
    stack: Seq[YPart],
    strict: Boolean
) extends ASTPartBranch {

  override type T = YPart

  /** isPlainText means it is a scalar and has no quotation marks
    */
  lazy val isPlainText: Option[Boolean] = node match {
    case n: YNode   => n.asScalar.map(_.plain)
    case s: YScalar => Some(s.plain)
    case _          => None
  }

  override lazy val isMultiline: Boolean = node match {
    case n: YNode if n.asScalar.isDefined => n.asScalar.exists(_.mark == MultilineMark)
    case _                                => false
  }

  override val isEmptyNode: Boolean = node match {
    case n: YNode => n.tagType == YType.Null
    case _        => false
  }

  override lazy val stringValue: String = node match {
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

  override def keys: Seq[String] = stack.flatMap {
    case e: YMapEntry => e.key.asScalar.map(_.text)
    case _            => None
  }

  override val isKey: Boolean =
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

  override val isValue: Boolean = stack.headOption.exists(_.isInstanceOf[YMapEntry]) && !isKey && !hasIncludeTag

  lazy val isIncludeTagValue: Boolean = stack.headOption.exists(_.isInstanceOf[YMapEntry]) && !isKey && hasIncludeTag

  override val isAtRoot: Boolean = node
    .isInstanceOf[model.YDocument] || (stack.count(_.isInstanceOf[YMap]) == 0 && node
    .isInstanceOf[YMap]) ||
    (stack.count(_.isInstanceOf[YMap]) <= 1 &&
      isYMapEntryKey)

  private def isYMapEntryKey =
    stack.headOption.exists {
      case entry: YMapEntry => entry.key == node
      case _                => false
    }

  override val isArray: Boolean = node.isArray
  override lazy val isInArray: Boolean =
    getSequence.isDefined

  override val isPositionOutsideLastEndNode: Boolean = {
    isPlainText match {
      case Some(true) => false
      case _ =>
        val lastAstTokenOpt = node.children.collectFirst {
          case yNodeContent: YNonContent
              if yNodeContent.tokens.nonEmpty && yNodeContent.tokens.exists(_.tokenType == YamlToken.EndNode) =>
            yNodeContent.tokens.collect {
              case astToken if astToken.tokenType == YamlToken.EndNode => astToken
            }.lastOption
        }.flatten

        lastAstTokenOpt match {
          case Some(lastToken) if lastToken.location.columnTo <= position.column => true
          case _                                                                 => false
        }
    }
  }

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

  def isValueDescendantOf(key: String): Boolean =
    isValue && parentEntryIs(key)

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

  override def parentKey: Option[String] = parentEntry.flatMap(_.key.asScalar.map(_.text))
}

case class ElementWithIndentation(ast: ASTElement, indentation: Option[Int])

object NodeBranchBuilder {

  def getAstForRange(
      ast: ASTElement,
      startPosition: AmfPosition,
      endPosition: AmfPosition,
      strict: Boolean
  ): ElementWithIndentation = {

    val start = buildElement(ast, startPosition, strict)
    if (startPosition == endPosition)
      ElementWithIndentation(start.node, findIndentation(start.stack, start.node))
    else {
      val end = buildElement(ast, endPosition, strict)
      findMutualYMapParent(start, end)
        .map(e => ElementWithIndentation(e, findIndentation(start.stack, e)))
        .getOrElse(ElementWithIndentation(ast, None))
    }

  }

  def buildElement(ast: ASTElement, position: AmfPosition, strict: Boolean): ASTPartBranch =
    ast match {
      case node: ASTNode => buildAST(node, position, strict)
      case ypart: YPart  => build(ypart, position, strict)
      case _             => YPartBranch(YDocument(IndexedSeq.empty, ""), position, Nil, strict)
    }

  private def findMutualYMapParent(start: ASTPartBranch, end: ASTPartBranch): Option[ASTElement] =
    start.stack.find(end.stack.contains(_))

  private def findIndentation(stack: Seq[ASTElement], element: ASTElement): Option[Int] =
    stack.splitAt(stack.indexOf(element))._2.collectFirst {
      case entry: YMapEntry => entry.range.columnFrom
      case seq: YSequence =>
        seq.children
          .collectFirst { case nc: YNonContent =>
            nc.tokens.find(t => t.tokenType == Indicator && (t.text == "-" || t.text == "[")).map(_.location.columnFrom)
          }
          .flatten
          .getOrElse(seq.range.columnFrom)
    }

  def build(ast: YPart, position: AmfPosition, strict: Boolean): YPartBranch = {
    val (stack, innerStrict) = getStack(ast, position, Seq(), strict)
    stack match {
      case actual :: stack => YPartBranch(actual, position, stack, innerStrict)
      case Nil             => YPartBranch(ast, position, Nil, innerStrict)
    }
  }

  def buildAST(astElement: ASTNode, position: AmfPosition, strict: Boolean): ASTElementPartBranch = {
    val stack = getStack(astElement, position, strict)
    stack match {
      case actual :: stack => ASTElementPartBranch(actual, position, stack)
      case Nil             => ASTElementPartBranch(astElement, position, Nil)
    }
  }

  def getStack(ast: ASTNode, position: AmfPosition, strict: Boolean): Seq[ASTNode] = {
    ast match {
      case n: Node if n.contains(position, strict)     => getChildren(n, position, Seq.empty, strict)
      case t: Terminal if t.contains(position, strict) => Seq(t)
      case _                                           => Seq.empty
    }
  }

  @tailrec
  def getChildren(node: Node, position: AmfPosition, stack: Seq[ASTNode], strict: Boolean): Seq[ASTNode] = {
    val current = node +: stack
    node.children.find(_.contains(position, strict)) match {
      case Some(n: Node)     => getChildren(n, position, current, strict)
      case Some(t: Terminal) => t +: current
      case _                 => current
    }
  }

  def build(bu: BaseUnit, position: AmfPosition, strict: Boolean): ASTPartBranch = {
    astFromBaseUnit(bu) match {
      case astElement: ASTNode => buildAST(astElement, position, strict)
      case ypart: YPart        => build(ypart, position, strict)
      case _                   => build(YDocument(IndexedSeq.empty, bu.location().getOrElse("")), position, strict)
    }
  }

  def astFromBaseUnit(bu: BaseUnit): ASTElement = {
    bu.objWithAST.flatMap(_.annotations.yPart()) match {
      case Some(d: YDocument) => d
      case Some(n: ASTNode)   => n
      case Some(p)            => YDocument(IndexedSeq(p), p.sourceName)
      case None               => YDocument(IndexedSeq.empty, "")
    }
  }

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
      strict: Boolean
  ): (Seq[YPart], Boolean) = {
    val innerStrict = strict || containsFlow(s)
    childWithPosition(s, amfPosition, innerStrict) match {
      case Some(n: YNode) =>
        childWithPosition(n, amfPosition, innerStrict) match {
          case None
              if innerStrict || (!n.isNull && n.value.range.lineFrom == amfPosition.line && n.value.range.columnFrom > amfPosition.column) =>
            (n +: s +: parents, innerStrict) // case for tag (!include)
          case None    => (parents, innerStrict)
          case Some(c) => getStack(c, amfPosition, n +: s +: parents, innerStrict)
        }
      case Some(c) =>
        getStack(c, amfPosition, s +: parents, innerStrict)
      case None if Some(s).collectFirst({ case e: YMapEntry if !isNullOrEmptyTag(e.value) => e }).isDefined =>
        (parents, innerStrict)
      case None if s.isInstanceOf[YMapEntry] =>
        getStack(s.asInstanceOf[YMapEntry].value, amfPosition, s +: parents, innerStrict)
      case None if s.isInstanceOf[YScalar] =>
        (parents, innerStrict)
      case _ => (s +: parents, innerStrict)
    }
  }

  private def isNullOrEmptyTag(node: YNode) =
    node.isNull || (node.tagType.toString == "!include")

  def childWithPosition(ast: YPart, amfPosition: AmfPosition, strict: Boolean): Option[YPart] = {
    val parts = ast.children
      .filterNot(_.isInstanceOf[YNonContent])
      .filter { yp =>
        yp.contains(amfPosition, strict)
      }
    if (parts.length > 1) {
      ast match {
        case e: YMapEntry if inKey(e, amfPosition, strict) =>
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

  private def inKey(e: YMapEntry, amfPosition: AmfPosition, strict: Boolean) =
    e.value.isNull || e.key.contains(amfPosition, strict)
}
