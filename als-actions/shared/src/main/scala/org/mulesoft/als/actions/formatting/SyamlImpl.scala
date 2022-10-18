package org.mulesoft.als.actions.formatting

import org.mulesoft.common.client.lexical.SourceLocation
import org.mulesoft.lexer.AstToken
import org.yaml.lexer.YamlToken.{Indent, Indicator, LineBreak, WhiteSpace}
import org.yaml.model._

object SyamlImpl {

  implicit class YPartImpl[T <: YPart](part: T) {
    def format(indentSize: Int, indent: Int = 0): T =
      (part match {
        case c: YComment if c.metaText.trim.startsWith("%") => YComment(s"${c.metaText.trim}", c.location, c.tokens)
        case c: YComment   => new YComment(s" ${c.metaText.trim}", c.location, insertSpaceComment(c.tokens))
        case d: YDirective => d
        case s: YScalar    => s
        case t: YTag       => new YTag(t.text, t.tagType, t.location, t.tokens :+ whiteSpace(t.location))
        case a: YAnchor    => a
        case nc: YNonContent =>
          YNonContent(nc.range, addWhitespaceToEntries(nc), nc.sourceName)
        case d: YDocument => YDocument(cleanChildren(d, indentSize, indent), d.sourceName)
        case s: YSequence => buildSequence(indentSize, indent, s)
        case m: YMap      => YMap(m.location, indentChildren(indentSize, indent, m))
        case e: YMapEntry =>
          YMapEntry(e.location, cleanChildren(e, indentSize, indent))
        case n: YNode =>
          YNode(n.value.format(indentSize, indent), n.tag, n.anchor, cleanChildren(n, indentSize, indent), n.sourceName)
        case _ => part
      }).asInstanceOf[T]

    /** mantains tokens until the comment char '#', then trims spaces and adds just 1 space after
      */
    private def insertSpaceComment(tokens: IndexedSeq[AstToken]): IndexedSeq[AstToken] = {
      val preTokens = tokens.takeWhile(t => !isCommentToken(t))
      val postTokens = tokens
        .splitAt(preTokens.size)
        ._2
        .flatMap {
          case t if t.tokenType == WhiteSpace => Seq.empty
          case t if isCommentToken(t) =>
            Seq(t, whiteSpace(t.location))
          case t => Seq(t)
        }
      preTokens ++ postTokens
    }

    private def cleanChildren(c: YPart, indentSize: Int, indent: Int): IndexedSeq[YPart] =
      c.children.map(_.format(indentSize, indent))

    /** no matter the Sequence, build an indented blocked sequence
      */
    private def buildSequence(indentSize: Int, indent: Int, s: YSequence) = {

      // if entry is map, set EOL

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
      YSequence(s.location, insertIndicators)
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

    /** add whitespace after `:` in entries, clean excess whitespaces
      */
    private def addWhitespaceToEntries(nc: YNonContent): IndexedSeq[AstToken] =
      if (nc.tokens.exists(_.tokenType == Indicator))
        nc.tokens.filterNot(t => isWhiteSpace(t) || t.tokenType == Indent).flatMap {
          case t if t.tokenType == Indicator && t.text == ":" =>
            IndexedSeq(t, whiteSpace(nc.location))
          case t => IndexedSeq(t)
        }
      else
        nc.tokens.filterNot(t => t.tokenType == Indent || t.tokenType == WhiteSpace && t.text == " ")

    private def indentChildren[T <: YPart](indentSize: Int, indent: Int, p: T): IndexedSeq[YPart] =
      cleanChildren(p, indentSize, indent + 1)
        .flatMap {
          case c: YNonContent => Seq(c)
          case c              => Seq(indentation(indentSize * indent, p.location), c)
        }

    private def lineBreak(location: SourceLocation): YNonContent =
      YNonContent(IndexedSeq(lineBreakToken(location)))

    private def indentation(indentSize: Int, location: SourceLocation): YNonContent =
      YNonContent(IndexedSeq(AstToken(Indent, " " * indentSize, location)))

    private def whiteSpace(location: SourceLocation): AstToken =
      AstToken(WhiteSpace, " ", location)

    private def isWhiteSpace(a: AstToken) =
      a.tokenType == WhiteSpace && a.text.trim.isBlank

    private def isCommentToken[T <: YPart](t: AstToken) =
      t.tokenType == Indicator && t.text == "#"
  }

  private def lineBreakToken[T <: YPart](location: SourceLocation) = {
    AstToken(LineBreak, "\n", location)
  }
}
