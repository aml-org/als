package org.mulesoft.als.common

import amf.core.internal.annotations.LexicalInformation
import org.mulesoft.als.common.ASTElementWrapper.CommonASTOps
import org.mulesoft.als.common.YPartASTWrapper.AlsYPart
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.antlrast.ast.ASTNode
import org.mulesoft.common.client.lexical.{ASTElement, Position => AmfPosition, PositionRange => AmfPositionRange}
import org.mulesoft.lexer.AstToken
import org.mulesoft.lsp.feature.common.Location
import org.yaml.lexer.YamlToken
import org.yaml.model.YNode.MutRef
import org.yaml.model._

object ASTElementWrapper {

  implicit class CommonASTOps(astElement: ASTElement) {

    def contains(amfPosition: AmfPosition, strict: Boolean): Boolean = {
      astElement match {
        case yPart: YPart => new AlsYPart(yPart).contains(amfPosition, strict)
//        case astNode: ASTNode => new ASTNodeOps(astNode).contains(amfPosition)
        case _ =>
          val range = PositionRange(
            Position(AmfPosition(astElement.location.lineFrom, astElement.location.columnTo)),
            Position(AmfPosition(astElement.location.lineTo, astElement.location.columnTo))
          )
          range.contains(Position(amfPosition))
      }
    }

    def sameContentAndLocation(other: ASTElement): Boolean = {
      astElement == other && astElement.location == other.location
    }

    def astToLocation: Location =
      Location(
        astElement.location.sourceName,
        LspRangeConverter.toLspRange(
          PositionRange(
            Position(AmfPosition(astElement.location.range.lineFrom, astElement.location.range.columnFrom)),
            Position(AmfPosition(astElement.location.range.lineTo, astElement.location.range.columnTo))
          )
        )
      )

  }

  def getIndentation(raw: String, position: Position): Int = {
    val pos  = position
    val left = raw.substring(0, pos.offset(raw))
    val line =
      if (left.contains("\n"))
        left.substring(left.lastIndexOf("\n")).stripPrefix("\n")
      else left
    val first = line.headOption match {
      case Some(c) if c == ' ' || c == '\t' => Some(c)
      case _                                => None
    }
    first
      .map(f => {
        line.substring(0, line.takeWhile(_ == f).length)
      })
      .getOrElse("")
      .length
  }

  implicit class AlsPositionRange(range: AmfPositionRange) {
    def toPositionRange: PositionRange =
      PositionRange(
        Position(AmfPosition(range.lineFrom, range.columnFrom)),
        Position(AmfPosition(range.lineTo, range.columnTo))
      )

    def contains(amfPosition: AmfPosition): Boolean =
      toPositionRange.contains(Position(amfPosition))

    def isEqual(li: LexicalInformation): Boolean =
      li.range.start.column == range.columnFrom &&
        li.range.start.line == range.lineFrom &&
        li.range.end.column == range.columnTo &&
        li.range.end.line == range.lineTo
  }
}

object ASTNodeWrapper {
  implicit class ASTNodeOps(astNode: ASTNode) extends CommonASTOps(astNode) {

    override def contains(amfPosition: AmfPosition, strict: Boolean): Boolean =
      PositionRange(astNode.location).contains(Position(amfPosition))
  }
}

object YPartASTWrapper {

  abstract class CommonPartOps(yPart: YPart) {
    protected val selectedPositionRange: PositionRange = PositionRange(yPart.range)

    def contains(amfPosition: AmfPosition, strict: Boolean): Boolean =
      selectedPositionRange.contains(Position(amfPosition))

    /** Contains both start and end positions
      * @param range
      * @return
      */
    def contains(range: AmfPositionRange): Boolean = {
      val positionRange = PositionRange(range)
      selectedPositionRange.contains(positionRange.start) &&
      selectedPositionRange.contains(positionRange.end)
    }

    lazy val isJson: Boolean =
      yPart.location.sourceName.toLowerCase.endsWith(".json")

  }

  abstract class FlowedStructure(beginFlowChar: String, endFlowChar: String, node: YValue) extends CommonPartOps(node) {

    val (flowBegin, flowEnd) = {
      val tokens = node.children.flatMap({
        case nonContent: YNonContent => nonContent.tokens
        case _                       => Nil
      })
      (
        tokens.exists(t => (t.tokenType == YamlToken.Indicator || jsonIndicator(t)) && t.text == beginFlowChar),
        tokens.exists(t => (t.tokenType == YamlToken.Indicator || jsonIndicator(t)) && t.text == endFlowChar)
      )
    }

    def jsonIndicator(t: AstToken): Boolean =
      isJson && (t.tokenType == YamlToken.BeginMapping || t.tokenType == YamlToken.EndMapping)

    private def flowedPosition = {
      PositionRange(
        node.range.copy(
          start = node.range.start.copy(column = (if (flowBegin) node.range.columnFrom + 1 else node.range.columnFrom)),
          end = node.range.end.copy(column = (if (flowEnd) node.range.columnTo - 1 else node.range.columnTo))
        )
      )
    }

    override val selectedPositionRange: PositionRange = flowedPosition
  }

  implicit class YSequenceOps(seq: YSequence) extends FlowedStructure("[", "]", seq) {
    override def contains(amfPosition: AmfPosition, strict: Boolean): Boolean =
      if (strict) super.contains(amfPosition, strict)
      else super.contains(amfPosition, strict) && respectsIndentation(seq, amfPosition)
  }

  private def respectsIndentation(seq: YSequence, amfPosition: AmfPosition) =
    seq.nodes.headOption.forall(_.range.columnFrom <= amfPosition.column)

  implicit class YMapEntryOps(entry: YMapEntry) extends CommonPartOps(entry) {
    def inMap: YNode = YNode(YMap(IndexedSeq(entry), entry.sourceName))

    def isArray: Boolean = false

    override def contains(position: AmfPosition, strict: Boolean): Boolean = {
      if (strict) super.contains(position, strict)
      else
        super.contains(position, strict) &&
        !isFirstChar(position) &&
        respectIndentation(position)
    }

    def respectIndentation(position: AmfPosition): Boolean =
      !(outScalarValue(position) || outIndentation(position)) &&
        mapValueRespectsEntryKey(position)

    def inJsonValue(position: AmfPosition, strict: Boolean): Boolean = {
      entry.key.contains(position, strict) || (entry.value.value match {
        case map: YMap if isJson => AlsYMapOps(map).contains(position, strict)
        case _                   => isJson
      })
    }

    def mapValueRespectsEntryKey(position: AmfPosition): Boolean =
      entry.value.tagType != YType.Map || (entry.value.tagType == YType.Map && entry.key.range.columnFrom < position.column)

    def isFirstChar(position: AmfPosition): Boolean =
      !isQuotedKey(
        entry.key
      ) && entry.key.range.lineFrom == position.line && entry.key.range.columnFrom == position.column

    def isQuotedKey(key: YNode): Boolean =
      key.asScalar match {
        case Some(s) => s.mark.isInstanceOf[QuotedMark]
        case _       => false
      }

    private def outScalarValue(position: AmfPosition) =
      entry.range.lineFrom < position.line && (scalarValue(position) || nullValueOutIndentation(position))

    private def scalarValue(position: AmfPosition) =
      !entry.value.isNull && entry.value.asScalar.isDefined && entry.value.value.range.lineTo < position.line

    private def nullValueOutIndentation(position: AmfPosition) =
      entry.value.isNull && outIndentation(position)

    private def outIndentation(position: AmfPosition) =
      entry.key.range.columnFrom >= position.column && entry.key.range.lineTo < position.line
  }

  implicit class YNodeImplicits(yNode: YNode) extends CommonPartOps(yNode) {
    def withKey(k: String): YNode = yNode.asEntry(k).inMap

    def asEntry(k: String): YMapEntry = YMapEntry(YNode(k), yNode)
  }

  implicit class AlsYMapOps(map: YMap) extends FlowedStructure("{", "}", map) {
    def isArray: Boolean = false

    override def contains(amfPosition: AmfPosition, strict: Boolean): Boolean =
      if (strict) super.contains(amfPosition, strict)
      else respectIndentation(amfPosition)

    def respectIndentation(amfPosition: AmfPosition): Boolean =
      beforeFirstEntry(amfPosition: AmfPosition) && map.entries.headOption
        .forall(e => {
          e.range.columnFrom <= amfPosition.column
        })

    def beforeFirstEntry(amfPosition: AmfPosition): Boolean = {
      map.range.lineTo >= amfPosition.line
    }
  }

  implicit class AlsYScalarOps(scalar: YScalar) extends CommonPartOps(scalar) {
    override def contains(amfPosition: AmfPosition, strict: Boolean): Boolean =
      super.contains(amfPosition, strict) ||
        isInsideNull(amfPosition) ||
        oneCharAfterEnd(scalar.range, amfPosition)

    private def isInsideNull(amfPosition: AmfPosition) =
      scalar.range.lineFrom <= amfPosition.line && scalar.value == null

    /** Hack for abstract declaration variables. By some reason, last empty char is trimmed, so: <<params | * will not
      * work
      *
      * @param inputRange
      * @param amfPosition
      * @return
      */
    private def oneCharAfterEnd(inputRange: AmfPositionRange, amfPosition: AmfPosition) = {
      inputRange.lineTo == amfPosition.line && inputRange.columnTo == amfPosition.column - 1
    }

    def unmarkedRange(): AmfPositionRange =
      if (scalar.mark.isInstanceOf[QuotedMark])
        scalar.range.copy(
          start = scalar.range.start.copy(column = scalar.range.columnFrom + 1),
          end = scalar.range.end.copy(column = scalar.range.columnTo - 1)
        )
      else scalar.range
  }

  implicit class AlsYPart(selectedNode: YPart) extends CommonPartOps(selectedNode) {

    def isArray: Boolean = selectedNode.isInstanceOf[YSequence]

    def isKey(amfPosition: AmfPosition): Boolean =
      selectedNode match {
        case entry: YMapEntry => PositionRange(entry.key.range).contains(Position(amfPosition))
        case _                => false
      }

    override def contains(amfPosition: AmfPosition, strict: Boolean): Boolean =
      if (strict) PositionRange(selectedNode.range).contains(Position(amfPosition))
      else
        selectedNode match {
          case ast: MutRef =>
            ast.origValue.contains(amfPosition, strict) || ast.origTag.contains(amfPosition, strict)
          case ast: YMapEntry =>
            YMapEntryOps(ast).contains(amfPosition, strict)
          case ast: YMap =>
            ast.contains(amfPosition, strict)
          case ast: YNode =>
            ast.value.contains(amfPosition, strict)
          case ast: YScalar =>
            AlsYScalarOps(ast).contains(amfPosition, strict)
          case seq: YSequence =>
            seq.contains(amfPosition, strict)
          case _ => super.contains(amfPosition, strict)
        }
  }
}
