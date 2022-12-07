package org.mulesoft.als.actions.formatting

import org.mulesoft.common.client.lexical.SourceLocation
import org.mulesoft.lexer.AstToken
import org.yaml.lexer.YamlToken
import org.yaml.lexer.YamlToken._
import org.yaml.model._

import scala.annotation.tailrec

object SyamlImpl {
  implicit class YPartImpl[T <: YPart](part: T) {
    def format(indentSize: Int, currentIndentation: Int, shouldCleanSpaces: Boolean = true): T = {
      (part match {
        case c: YComment if c.metaText.trim.startsWith("%") => c.format(false)
        case c: YComment                                    => c.format(true)
        case d: YDirective                                  => d
        case s: YScalar                                     => s.format(indentSize, currentIndentation)
        case t: YTag         => new YTag(t.text, t.tagType, t.location, t.tokens :+ whiteSpace(t.location))
        case a: YAnchor      => a
        case nc: YNonContent => nc.format(shouldCleanSpaces, indentSize, currentIndentation)
        case d: YDocument =>
          YDocument(cleanChildren(d, indentSize, currentIndentation, shouldCleanSpaces), d.sourceName)
        case s: YSequence => s.format(indentSize, currentIndentation, shouldCleanSpaces)
        case m: YMap      => m.format(indentSize, currentIndentation, shouldCleanSpaces)
        case e: YMapEntry => e.format(indentSize, currentIndentation, shouldCleanSpaces)
        case n: YNode     => n.format(indentSize, currentIndentation, shouldCleanSpaces)
        case _            => part
      }).asInstanceOf[T]
    }
  }

  sealed implicit class YScalarImpl(s: YScalar) {
    def format(indentSize: Int, currentIndentation: Int): YScalar =
      YScalar.withIndentation(s, indentSize * currentIndentation)
  }

  /** We decided to leave YAML Flows as untouched as possible because we are not sure how to best format (maintain
    * single line if user wrote so, expand as with yaml with indentation?) When JSON styler is unified (remove from
    * SYAML), we should revisit this topic
    * @tparam T
    */
  sealed trait FlowableFormat[T <: YPart] {
    val part: T
    val isEmpty: Boolean
    def emptyPart(children: IndexedSeq[YPart]): T
    def nonEmptyPart(children: IndexedSeq[YPart], indentSize: Int, indent: Int): T

    def format(indentSize: Int, indent: Int, shouldCleanSpaces: Boolean): T = {
      val cleanSpaces: Boolean = !(!shouldCleanSpaces || isFlow(part.children))
      val children             = cleanChildren(part, indentSize, indent + 1, cleanSpaces)
      if (isEmpty) emptyPart(children)
      else nonEmptyPart(children, indentSize, indent)
    }

    protected def lineBreakBlocks(children: IndexedSeq[YPart]): IndexedSeq[YPart] =
      children.flatMap {
        case p if shouldBreakLine(p) =>
          IndexedSeq(lineBreak(part.location), p)
        case p if shouldAddSpace(p) =>
          IndexedSeq(YNonContent(IndexedSeq(whiteSpace(part.location))), p)
        case x =>
          IndexedSeq(x)
      }

    protected def indentSeparators(seq: IndexedSeq[YPart], indentSize: Int, isFlow: Boolean): IndexedSeq[YPart] =
      seq.map {
        case nc: YNonContent if !isFlow && nc.tokens.exists(hasSeparator) =>
          val tuple = nc.tokens.splitAt(nc.tokens.indexWhere(hasSeparator))
          YNonContent(tuple._1 ++ IndexedSeq(indentToken(indentSize, part.location)) ++ tuple._2)
        case x => x
      }
  }

  sealed implicit class YSeqImpl(s: YSequence) extends FlowableFormat[YSequence] {
    override val part: YSequence                                   = s
    override val isEmpty: Boolean                                  = s.isEmpty
    override def emptyPart(children: IndexedSeq[YPart]): YSequence = YSequence(s.location, children)
    override def nonEmptyPart(children: IndexedSeq[YPart], indentSize: Int, indent: Int): YSequence = {
      val flow: Boolean = isFlow(children)
      val lineBreaks    = lineBreakBlocks(children)
      val indented      = indentSeparators(lineBreaks, indentSize * indent, flow)
      YSequence(s.location, indented)
    }
  }

  sealed implicit class YMapImpl(m: YMap) extends FlowableFormat[YMap] {
    override val part: YMap                                   = m
    override val isEmpty: Boolean                             = m.entries.isEmpty
    override def emptyPart(children: IndexedSeq[YPart]): YMap = YMap(m.location, children)
    override def nonEmptyPart(children: IndexedSeq[YPart], indentSize: Int, indent: Int): YMap = {
      val flow: Boolean = isFlow(children)
      val lineBreaks    = lineBreakBlocks(children)
      val indented =
        if (flow)
          indentSeparators(lineBreaks, indentSize * indent, flow)
        else
          indentMap(lineBreaks, indentSize * indent)
      YMap(m.location, indented)
    }

    private def indentMap(lineBreaks: IndexedSeq[YPart], indent: Int): IndexedSeq[YPart] =
      lineBreaks.flatMap {
        case nc: YNonContent => IndexedSeq(nc)
        case p: YMapEntry    => IndexedSeq(indentation(indent, p.location), p)
        case p               => IndexedSeq(p)
      }
  }

  sealed implicit class YMapEntryImpl(e: YMapEntry) {
    def format(indentSize: Int, currentIndentation: Int, shouldCleanSpaces: Boolean): YMapEntry = {
      val cChildren = cleanChildren(e, indentSize, currentIndentation, shouldCleanSpaces)
      val iChildren =
        addIndentation(cChildren, currentIndentation * indentSize)
      if (shouldAddSpace(e.value))
        YMapEntry(e.location, addSpace(iChildren))
      else YMapEntry(e.location, iChildren)
    }

    private def addSpace(children: IndexedSeq[YPart]): IndexedSeq[YPart] =
      children.flatMap {
        case nc: YNonContent if nc.tokens.exists(colonToken) =>
          val tuple = nc.tokens.splitAt(nc.tokens.indexWhere(colonToken) + 1)
          IndexedSeq(YNonContent((tuple._1 :+ whiteSpace(nc.location)) ++ tuple._2))
        case x => IndexedSeq(x)
      }

    private def addIndentation(children: IndexedSeq[YPart], indent: Int): IndexedSeq[YPart] =
      children.flatMap {
        case nc: YNonContent if nc.tokens.exists(colonToken) && nc.tokens.exists(_.tokenType == LineBreak) =>
          val tuple = nc.tokens.splitAt(nc.tokens.indexWhere(_.tokenType == LineBreak) + 1)
          IndexedSeq(YNonContent((tuple._1 :+ indentToken(indent, nc.location)) ++ tuple._2))
        case x => IndexedSeq(x)
      }

    private def colonToken(t: AstToken): Boolean =
      t.tokenType == Indicator && t.text == ":"
  }

  sealed implicit class YNodeImpl(n: YNode) {
    def format(indentSize: Int, currentIndentation: Int, shouldCleanSpaces: Boolean): YNode = {
      val formatted = n.value.format(indentSize, currentIndentation, shouldCleanSpaces)
      YNode(
        formatted,
        n.tag,
        n.anchor,
        buildChildren(n, formatted, indentSize, currentIndentation, shouldCleanSpaces),
        n.sourceName
      )
    }

    // ugly code, semi-copy from cleanChildren, would be nice to improve and avoid duplicated logic
    private def buildChildren(
        n: YNode,
        formatted: YValue,
        indentSize: Int,
        currentIndentation: Int,
        shouldCleanSpaces: Boolean
    ): IndexedSeq[YPart] = {
      val seq = n.children
        .sliding(2) // look ahead for comments
        .flatMap {
          case Seq(a: YNonContent, _: YComment) => // don't trim spaces before a comment
            Seq(a)
          case a if a.headOption.contains(n.value) => Seq(formatted)
          case a =>
            a.headOption.map(_.format(indentSize, currentIndentation, shouldCleanSpaces))
        }
        .toIndexedSeq
      if (n.children.size > 1) // last element was not mapped
        seq :+ {
          if (n.children.lastOption.contains(n.value)) formatted
          else n.children.last.format(indentSize, currentIndentation, shouldCleanSpaces)
        }
      else seq
    }
  }

  sealed implicit class YCommImpl(c: YComment) {
    def format(shouldHaveSpace: Boolean): YComment =
      if (shouldHaveSpace)
        YComment(c.metaText, c.location, commentSpace(c.tokens, true))
      else YComment(c.metaText.trim, c.location, commentSpace(c.tokens, false))

    /** mantains tokens until the comment char '#', then trims spaces and adds just 1 space after
      */
    private def commentSpace(tokens: IndexedSeq[AstToken], shouldHaveSpace: Boolean): IndexedSeq[AstToken] =
      tokens.map {
        case t if t.tokenType == MetaText =>
          if (shouldHaveSpace && !t.text.startsWith(" ")) AstToken(MetaText, s" ${t.text}", t.location)
          else if (!shouldHaveSpace) AstToken(MetaText, t.text.trim, t.location)
          else AstToken(MetaText, t.text, t.location)
        case o => o
      }
  }

  sealed implicit class YNonContImpl(nc: YNonContent) {

    def format(shouldCleanSpaces: Boolean, indentSize: Int, currentIndentation: Int): YNonContent = {
      if (nc.containsToken(Error)) nc
      else if (shouldCleanSpaces) YNonContent(nc.range, cleanSpaces(), nc.sourceName)
      else {
        YNonContent(nc.range, addIndentations(cleanSpaces(), indentSize, currentIndentation), nc.sourceName)
      }
    }

    def containsToken(token: YamlToken): Boolean =
      nc.tokens.map(_.tokenType).contains(token)

    /** add whitespace after `:` in entries, clean excess whitespaces
      */
    private def cleanSpaces(): IndexedSeq[AstToken] =
      nc.tokens.filterNot(t => t.tokenType == Indent || t.tokenType == WhiteSpace)

    private def addIndentations(
        tokens: IndexedSeq[AstToken],
        indentSize: Int,
        currentIndentation: Int
    ): IndexedSeq[AstToken] =
      tokens.flatMap {
        case t: AstToken if t.tokenType == LineBreak =>
          Seq(t, indentToken(indentSize * currentIndentation, t.location))
        case t => Seq(t)
      }
  }

  private def cleanChildren(c: YPart, indentSize: Int, indent: Int, shouldCleanSpaces: Boolean): IndexedSeq[YPart] = {
    val seq = c.children
      .sliding(2) // look ahead for comments
      .flatMap {
        case Seq(a: YNonContent, _: YComment) => // don't trim spaces before a comment
          Seq(a)
        case a =>
          a.headOption.map(_.format(indentSize, indent, shouldCleanSpaces))
      }
      .toIndexedSeq
    if (c.children.size > 1) // last element was not mapped
      seq :+ c.children.last.format(indentSize, indent, shouldCleanSpaces)
    else seq
  }

  private def indentation(indentSize: Int, location: SourceLocation): YNonContent =
    YNonContent(IndexedSeq(indentToken(indentSize, location)))

  private def indentToken(indentSize: Int, location: SourceLocation) =
    AstToken(Indent, " " * indentSize, location)

  private def whiteSpace(location: SourceLocation): AstToken = AstToken(WhiteSpace, " ", location)

  private def lineBreak(location: SourceLocation): YNonContent = YNonContent(IndexedSeq(lineBreakToken(location)))

  private def lineBreakToken[T <: YPart](location: SourceLocation) = AstToken(LineBreak, "\n", location)

  @tailrec
  private def shouldBreakLine(p: YPart): Boolean = p match {
    case _ @(_: YMap | _: YSequence) => !isFlow(p.children)
    case n: YNode                    => shouldBreakLine(n.value)
    case _                           => false
  }

  @tailrec
  private def shouldAddSpace(p: YPart): Boolean = p match {
    case _: YScalar   => true
    case m: YMap      => m.isEmpty
    case s: YSequence => s.isEmpty
    case nc: YNonContent =>
      nc.tokens.exists(t => t.tokenType == Indicator && Seq("[", "{", "]", "}").contains(t.text))
    case n: YNode => shouldAddSpace(n.value)
    case _        => false
  }

  private def hasSeparator(t: AstToken) =
    t.tokenType == Indicator && Seq("-", ",").contains(t.text)

  private def isFlow(parts: IndexedSeq[YPart]) =
    parts.exists {
      case nc: YNonContent => nc.tokens.exists(t => t.tokenType == Indicator && Seq("[", "{").contains(t.text))
      case _               => false
    }
}
