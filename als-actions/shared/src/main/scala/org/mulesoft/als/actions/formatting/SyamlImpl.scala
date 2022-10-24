package org.mulesoft.als.actions.formatting

import org.mulesoft.common.client.lexical.SourceLocation
import org.mulesoft.lexer.AstToken
import org.yaml.lexer.YamlToken
import org.yaml.lexer.YamlToken.{
  BeginMapping,
  BeginSequence,
  EndMapping,
  EndSequence,
  Indent,
  Indicator,
  LineBreak,
  MetaText,
  WhiteSpace
}
import org.yaml.model._

object SyamlImpl {

  implicit class YPartImpl[T <: YPart](part: T) {
    def format(indentSize: Int, currentIndentation: Int): T = {
      (part match {
        case c: YComment if c.metaText.trim.startsWith("%") =>
          YComment(c.metaText.trim, c.location, commentSpace(c.tokens, shouldHaveSpace = false))
        case c: YComment =>
          new YComment(c.metaText, c.location, commentSpace(c.tokens, shouldHaveSpace = true))
        case d: YDirective => d
        case s: YScalar =>
          s // maybe it's possible to trim when needed, with the constructor as private I don't know how
        case t: YTag    => new YTag(t.text, t.tagType, t.location, t.tokens :+ whiteSpace(t.location))
        case a: YAnchor => a
        case nc: YNonContent =>
          YNonContent(nc.range, addWhitespaceToEntries(nc), nc.sourceName)
        case d: YDocument => YDocument(cleanChildren(d, indentSize, currentIndentation), d.sourceName)
        case s: YSequence => buildSequence(indentSize, currentIndentation, s)
        case m: YMap =>
          YMap(m.location, indentChildren(indentSize, currentIndentation, m))
        case e: YMapEntry =>
          YMapEntry(e.location, cleanChildren(e, indentSize, currentIndentation))
        case n: YNode =>
          YNode(
            n.value.format(indentSize, currentIndentation),
            n.tag,
            n.anchor,
            cleanChildren(n, indentSize, currentIndentation),
            n.sourceName
          )
        case _ => part
      }).asInstanceOf[T]
    }

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

    private def cleanChildren(c: YPart, indentSize: Int, indent: Int): IndexedSeq[YPart] = {
      val parts = c.children.sliding(2, 2).flatMap {
        case Seq(a: YNonContent, b: YComment) => // don't trim spaces before a comment
          Seq(a, b.format(indentSize, indent))
        case a =>
          a.map(_.format(indentSize, indent))
      }
      parts.toIndexedSeq
    }

    /** no matter the Sequence, build an indented blocked sequence
      */
    private def buildSequence(indentSize: Int, indent: Int, s: YSequence) = {
      val seq = cleanChildren(s, indentSize, indent + 1)
      val insertIndicators = seq.flatMap {
        case nc: YNonContent if nc.tokens.exists(_.tokenType == Indicator) =>
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
        case p: YNonContent if p.tokens.map(_.tokenType).contains(LineBreak) => true
        case _                                                               => false
      }
      val tuple = insertIndicators.splitAt(Math.max(lastLineBreak, 0))
      tuple._1 ++ tuple._2.tail
    }

    /** add whitespace after `:` in entries, clean excess whitespaces
      */
    private def addWhitespaceToEntries(nc: YNonContent): IndexedSeq[AstToken] = {
      val colonIdx = nc.tokens.indexWhere(t => t.tokenType == Indicator && t.text == ":")
      val hasWhitespace =
        if (colonIdx >= 0) nc.tokens.splitAt(colonIdx)._2.headOption.exists(_.tokenType == WhiteSpace) else false

      if (nc.tokens.exists(_.tokenType == Indicator))
        nc.tokens.filterNot(t => isWhiteSpace(t) || t.tokenType == Indent).flatMap {
          case t if t.tokenType == Indicator && t.text == ":" && !hasWhitespace =>
            IndexedSeq(t, whiteSpace(nc.location))
          case t => IndexedSeq(t)
        }
      else
        nc.tokens.filterNot(t => t.tokenType == Indent || t.tokenType == WhiteSpace)
    }

    private def indentChildren[T <: YPart](indentSize: Int, indent: Int, p: T): IndexedSeq[YPart] = {
      var currIndent = indentSize
      cleanChildren(p, indentSize, indent + 1)
        .flatMap {
          case c: YNonContent =>
            isBeginBlock(c) match {
              case Some(value) =>
                currIndent += 1
                Seq(
                  lineBreak(c.location),
                  indentation((currIndent - 1) * indent, p.location),
                  blockDelimitator(value, c.location),
                  lineBreak(c.location)
                )
              case None =>
                isEndBlock(c) match {
                  case Some(value) =>
                    currIndent -= 1
                    Seq(
                      lineBreak(c.location),
                      indentation(currIndent * indent, p.location),
                      blockDelimitator(value, c.location),
                      lineBreak(c.location)
                    )
                  case None => Seq(c)
                }
            }
          case c =>
            Seq(indentation(currIndent * indent, p.location), c)
        }
    }

    private def lineBreak(location: SourceLocation): YNonContent =
      YNonContent(IndexedSeq(lineBreakToken(location)))

    private def blockDelimitator(block: String, location: SourceLocation): YNonContent =
      YNonContent(IndexedSeq(indicatorToken(block, location)))

    private def indentation(indentSize: Int, location: SourceLocation): YNonContent =
      YNonContent(IndexedSeq(AstToken(Indent, " " * indentSize, location)))

    private def whiteSpace(location: SourceLocation): AstToken =
      AstToken(WhiteSpace, " ", location)

    private def isWhiteSpace(a: AstToken) =
      a.tokenType == WhiteSpace && a.text.trim.isEmpty

    private def lineBreakToken[T <: YPart](location: SourceLocation) =
      AstToken(LineBreak, "\n", location)

    private def indicatorToken[T <: YPart](text: String, location: SourceLocation) =
      AstToken(Indicator, text, location)
  }

  private def isBeginBlock(c: YNonContent): Option[String] = {
    val tokens = c.tokens.map(_.tokenType)
    if (tokens.exists(t => t == BeginMapping || t == BeginSequence))
      c.tokens.find(_.tokenType == Indicator).map(_.text)
    else None
  }

  private def isEndBlock(c: YNonContent): Option[String] = {
    val tokens = c.tokens.map(_.tokenType)
    if (tokens.exists(t => t == EndMapping || t == EndSequence))
      c.tokens.find(_.tokenType == Indicator).map(_.text)
    else None
  }
}
