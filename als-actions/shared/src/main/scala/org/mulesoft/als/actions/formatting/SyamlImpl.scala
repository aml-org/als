package org.mulesoft.als.actions.formatting

import org.mulesoft.common.client.lexical.SourceLocation
import org.mulesoft.lexer.AstToken
import org.yaml.lexer.YamlToken
import org.yaml.lexer.YamlToken._
import org.yaml.model._

// todo: avoid recursive overhead by creating a case class with (part, children) and then rebuilding each part when it is
// already calculated?
// memoization map with part -> formatted? (flattening every YPart to a single level?)
object SyamlImpl {
  implicit class YPartImpl[T <: YPart](part: T) {
    def format(indentSize: Int, currentIndentation: Int): T = {
      (part match {
        case c: YComment if c.metaText.trim.startsWith("%") => c.format(false)
        case c: YComment                                    => c.format(true)
        case d: YDirective                                  => d
        case s: YScalar =>
          s // maybe it's possible to trim when needed, with the constructor as private I don't know how
        case t: YTag         => new YTag(t.text, t.tagType, t.location, t.tokens :+ whiteSpace(t.location))
        case a: YAnchor      => a
        case nc: YNonContent => nc.format()
        case d: YDocument    => YDocument(cleanChildren(d, indentSize, currentIndentation), d.sourceName)
        case s: YSequence =>
          s.format(indentSize, currentIndentation)
        case m: YMap => m.format(indentSize, currentIndentation)
        case e: YMapEntry =>
          YMapEntry(e.location, cleanChildren(e, indentSize, currentIndentation))
        case n: YNode => n.format(indentSize, currentIndentation)
        case _        => part
      }).asInstanceOf[T]
    }
  }

  sealed implicit class YMapImpl(m: YMap) {
    def format(indentSize: Int, currentIndentation: Int): YMap =
      if (m.entries.isEmpty)
        YMap(
          m.location,
          openMapFlow(m.location) +:
            indentChildren(indentSize, currentIndentation, m) :+
            closeMapFlow(m.location)
        )
      else {
        val parts = indentChildren(indentSize, currentIndentation, m)
//        if(parts.exists{
//          case nc: YNonContent =>
//            nc.tokens.exists(_.tokenType == LineBreak)
//          case _ => false
//        })
        YMap(m.location, parts)
//        else
//          YMap(m.location, lineBreak(m.location) +: parts) // if no endOfLine, add one
      }

    private def openMapFlow(location: SourceLocation)  = YNonContent(IndexedSeq(AstToken(Indicator, "{", location)))
    private def closeMapFlow(location: SourceLocation) = YNonContent(IndexedSeq(AstToken(Indicator, "}", location)))
  }

  sealed implicit class YNodeImpl(n: YNode) {
    def format(indentSize: Int, currentIndentation: Int): YNode = {
      val formatted = n.value.format(indentSize, currentIndentation)
      YNode(
        formatted,
        n.tag,
        n.anchor,
        buildChildren(n, formatted, indentSize, currentIndentation),
        n.sourceName
      )
    }
  }

  private def buildChildren(n: YNode, formatted: YValue, indentSize: Int, currentIndentation: Int) = {
    n.children.map {
      case a if a == n.value => formatted
      case a =>
        a.format(indentSize, currentIndentation)
    }.toIndexedSeq
  }

  sealed implicit class YSeqImpl(s: YSequence) {

    /** no matter the Sequence, build an indented blocked sequence
      */
    def format(indentSize: Int, indent: Int): YSequence = {
      val seq = cleanChildren(s, indentSize, indent + 1)
      if (s.isEmpty) emptySequence(seq)
      else sequenceBlock(seq, indentSize, indent)
    }

    private def sequenceBlock(seq: IndexedSeq[YPart], indentSize: Int, indent: Int) = {
      val insertIndicators = seq.flatMap {
        case nc: YNonContent if nc.containsToken(Indicator) =>
          nc.tokens.collect {
            case t if t.tokenType == Indicator && t.text == "[" => lineBreak(t.location)
          }
        case nc: YNonContent => Seq(nc)
        case p: YMap =>
          Seq(startSeqVal(p.location, indentSize * indent, containsMap = true), p, lineBreak(p.location))
        case p: YNode if p.tag.tagType == YType.Map =>
          Seq(startSeqVal(p.location, indentSize * indent, containsMap = true), p, lineBreak(p.location))
        case p =>
          Seq(startSeqVal(p.location, indentSize * indent, containsMap = false), p, lineBreak(p.location))
      }
      YSequence(s.location, removeLastEOL(insertIndicators))
    }

    private def emptySequence(seq: IndexedSeq[YPart]) = YSequence(s.location, seq)

    /** indent, mark sequence entry and add space after
      */
    private def startSeqVal(location: SourceLocation, indentSize: Int, containsMap: Boolean): YNonContent = {
      val valueTokens = // if it contains a map, start at the next line indented to avoid confusions
        if (containsMap)
          lineBreakToken(location)
        else
          whiteSpace(location)
      YNonContent(
        IndexedSeq(
          AstToken(Indent, " " * indentSize, location),
          AstToken(Indicator, "-", location)
        ) :+ valueTokens
      )
    }

    private def removeLastEOL[T <: YPart](insertIndicators: IndexedSeq[YPart]): IndexedSeq[YPart] = {
      val lastLineBreak: Int = insertIndicators.lastIndexWhere {
        case p: YNonContent if p.containsToken(LineBreak) => !p.containsToken(Error)
        case _                                            => false
      }

      if (lastLineBreak > 0) {
        val tuple = insertIndicators.splitAt(lastLineBreak)
        tuple._1 ++ tuple._2.tail
      } else insertIndicators
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
    def format(): YNonContent =
      if (nc.containsToken(Error)) nc
      else YNonContent(nc.range, addWhitespaceToEntries(), nc.sourceName)

    def containsToken(token: YamlToken): Boolean =
      nc.tokens.map(_.tokenType).contains(token)

    /** add whitespace after `:` in entries, clean excess whitespaces
      */
    private def addWhitespaceToEntries(): IndexedSeq[AstToken] = {
      val colonIdx = nc.tokens.indexWhere(t => t.tokenType == Indicator && t.text == ":")
      val hasWhitespace =
        if (colonIdx >= 0) nc.tokens.splitAt(colonIdx)._2.headOption.exists(_.tokenType == WhiteSpace) else false
      if (nc.tokens.exists(_.tokenType == Indicator))
        nc.tokens
          .filterNot(t =>
            isWhiteSpace(t) || t.tokenType == Indent ||
              (t.tokenType == Indicator && Seq("{", "}").contains(t.text))
          )
          .flatMap {
            case t if t.tokenType == Indicator && t.text == ":" && !hasWhitespace =>
              IndexedSeq(t, whiteSpace(nc.location))
            case t => IndexedSeq(t)
          }
      else
        nc.tokens.filterNot(t => t.tokenType == Indent || t.tokenType == WhiteSpace)
    }
  }

  private def cleanChildren(c: YPart, indentSize: Int, indent: Int): IndexedSeq[YPart] =
    c.children
      .sliding(2, 2)
      .flatMap {
        case Seq(a: YNonContent, b: YComment) => // don't trim spaces before a comment
          Seq(a, b.format(true))
        case Seq(a: YNonContent, b: YNodePlain) if b.tag.tagType != YType.Map =>
          if (a.tokens.exists(t => t.tokenType == Indicator && t.text == ":") && a.tokens.exists(_.tokenType == Indent))
            Seq(a.format(), indentation(indentSize * indent, a.location), b.format(indentSize, indent))
          else
            Seq(a.format(), b.format(indentSize, indent))
        case a =>
          a.map(_.format(indentSize, indent))
      }
      .toIndexedSeq

  private def indentChildren[T <: YPart](indentSize: Int, indent: Int, p: T): IndexedSeq[YPart] =
    cleanChildren(p, indentSize, indent + 1)
      .flatMap {
        case c: YNonContent => Seq(c)
        case c              => Seq(indentation(indentSize * indent, p.location), c)
      }

  private def indentation(indentSize: Int, location: SourceLocation): YNonContent =
    YNonContent(IndexedSeq(AstToken(Indent, " " * indentSize, location)))

  private def whiteSpace(location: SourceLocation): AstToken =
    AstToken(WhiteSpace, " ", location)

  private def isWhiteSpace(a: AstToken) =
    a.tokenType == WhiteSpace && a.text.trim.isEmpty

  private def lineBreak(location: SourceLocation): YNonContent =
    YNonContent(IndexedSeq(lineBreakToken(location)))

  private def lineBreakToken[T <: YPart](location: SourceLocation) =
    AstToken(LineBreak, "\n", location)
}
