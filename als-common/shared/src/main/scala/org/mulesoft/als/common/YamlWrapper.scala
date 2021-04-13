package org.mulesoft.als.common

import amf.core.annotations.LexicalInformation
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import amf.core.parser.{Position => AmfPosition}
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.lsp.feature.common.Location
import org.mulesoft.lexer.{AstToken, InputRange}
import org.yaml.lexer.YamlToken
import org.yaml.model.YNode.MutRef
import org.yaml.model._

object YamlWrapper {

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

  implicit class AlsInputRange(range: InputRange) {
    def toPositionRange: PositionRange =
      PositionRange(Position(AmfPosition(range.lineFrom, range.columnFrom)),
                    Position(AmfPosition(range.lineTo, range.columnTo)))

    def contains(amfPosition: AmfPosition): Boolean =
      toPositionRange.contains(Position(amfPosition))

    def isEqual(li: LexicalInformation): Boolean =
      li.range.start.column == range.columnFrom &&
        li.range.start.line == range.lineFrom &&
        li.range.end.column == range.columnTo &&
        li.range.end.line == range.lineTo
  }

  abstract class CommonPartOps(yPart: YPart) {
    protected val selectedPositionRange: PositionRange = PositionRange(yPart.range)

    def contains(amfPosition: AmfPosition, editionMode: Boolean = true): Boolean =
      selectedPositionRange.contains(Position(amfPosition))

    /**
      * Contains both start and end positions
      * @param range
      * @return
      */
    def contains(range: InputRange): Boolean = {
      val positionRange = PositionRange(range)
      selectedPositionRange.contains(positionRange.start) &&
      selectedPositionRange.contains(positionRange.end)
    }

    def yPartToLocation: Location =
      Location(
        yPart.sourceName,
        LspRangeConverter.toLspRange(
          PositionRange(
            Position(AmfPosition(yPart.range.lineFrom, yPart.range.columnFrom)),
            Position(AmfPosition(yPart.range.lineTo, yPart.range.columnTo))
          ))
      )

    lazy val isJson: Boolean =
      yPart.location.sourceName.toLowerCase.endsWith(".json")

  }

  abstract class FlowedStructure(beginFlowChar: String, endFlowChar: String, node: YValue)
      extends CommonPartOps(node) {

    val (flowBegin, flowEnd) = {
      val tokens = node.children.flatMap({
        case nonContent: YNonContent => nonContent.tokens
        case _                       => Nil
      })
      (tokens.exists(t => (t.tokenType == YamlToken.Indicator || jsonIndicator(t)) && t.text == beginFlowChar),
       tokens.exists(t => (t.tokenType == YamlToken.Indicator || jsonIndicator(t)) && t.text == endFlowChar))
    }

    def jsonIndicator(t: AstToken): Boolean =
      isJson && (t.tokenType == YamlToken.BeginMapping || t.tokenType == YamlToken.EndMapping)

    private def flowedPosition = {
      PositionRange(
        node.range.copy(
          columnFrom = (if (flowBegin) node.range.columnFrom + 1 else node.range.columnFrom),
          columnTo = (if (flowEnd) node.range.columnTo - 1 else node.range.columnTo)
        ))
    }

    override val selectedPositionRange: PositionRange = flowedPosition
  }

  implicit class YSequenceOps(seq: YSequence) extends FlowedStructure("[", "]", seq) {
    override def contains(amfPosition: AmfPosition, editionMode: Boolean = false): Boolean =
      super.contains(amfPosition, editionMode) &&
        seq.nodes.headOption.forall(_.range.columnFrom <= amfPosition.column)
  }

  implicit class YMapEntryOps(entry: YMapEntry) extends CommonPartOps(entry) {
    def inMap: YNode = YNode(YMap(IndexedSeq(entry), entry.sourceName))

    def isArray: Boolean = false

    override def contains(position: AmfPosition, editionMode: Boolean = false): Boolean =
      super.contains(position, editionMode) &&
        !isFirstChar(position) &&
        (inJsonValue(position) || (!isJson && respectIndentation(position)))

    def respectIndentation(position: AmfPosition): Boolean =
      !(outScalarValue(position) || outIndentation(position)) &&
        mapValueRespectsEntryKey(position)

    def inJsonValue(position: AmfPosition): Boolean = {
      entry.key.contains(position) || (entry.value.value match {
        case map: YMap if isJson => AlsYMapOps(map).contains(position)
        case _                   => isJson
      })
    }

    def mapValueRespectsEntryKey(position: AmfPosition): Boolean =
      entry.value.tagType != YType.Map || (entry.value.tagType == YType.Map && entry.key.range.columnFrom < position.column)

    def isFirstChar(position: AmfPosition): Boolean =
      !isQuotedKey(entry.key) && entry.key.range.lineFrom == position.line && entry.key.range.columnFrom == position.column

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

    override def contains(amfPosition: AmfPosition, editionMode: Boolean = false): Boolean =
      (super
        .contains(amfPosition, editionMode) || sameLevelBefore(amfPosition, editionMode)) && (isJson || respectIndentation(
        amfPosition))

    private def respectIndentation(amfPosition: AmfPosition): Boolean =
      map.entries.headOption.forall(_.range.columnFrom <= amfPosition.column)

    private def sameLevelBefore(amfPosition: AmfPosition, editionMode: Boolean): Boolean =
      editionMode && map.range.lineFrom > amfPosition.line && map.range.lineTo >= amfPosition.line && map.entries.nonEmpty
  }

  implicit class AlsYScalarOps(scalar: YScalar) extends CommonPartOps(scalar) {
    override def contains(amfPosition: AmfPosition, editionMode: Boolean = false): Boolean =
      super.contains(amfPosition, editionMode) || // (lineContains(amfPosition) && scalar.mark == NoMark)
        (scalar.range.lineFrom <= amfPosition.line && scalar.value == null) // blind guessing, we should find a better solution

//    // todo: check why such hack is necessary, sensible case:  [val1, val2, *]
//    private def lineContains(amfPosition: AmfPosition): Boolean =
//      scalar.range.lineFrom <= amfPosition.line && ((scalar.range.lineTo >= amfPosition.line && scalar.range.columnFrom <= amfPosition.column) || scalar.value == null)

    def unmarkedRange(): InputRange =
      if (scalar.mark.isInstanceOf[QuotedMark])
        scalar.range.copy(columnFrom = scalar.range.columnFrom + 1, columnTo = scalar.range.columnTo - 1)
      else scalar.range
  }

  implicit class AlsYPart(selectedNode: YPart) extends CommonPartOps(selectedNode) {

    def isArray: Boolean = selectedNode.isInstanceOf[YSequence]

    def isKey(amfPosition: AmfPosition): Boolean =
      selectedNode match {
        case entry: YMapEntry => PositionRange(entry.key.range).contains(Position(amfPosition))
        case _                => false
      }

    override def contains(amfPosition: AmfPosition, editionMode: Boolean = false): Boolean = selectedNode match {
      case ast: MutRef =>
        ast.origValue.contains(amfPosition, editionMode)
      case ast: YMapEntry =>
        YMapEntryOps(ast).contains(amfPosition, editionMode)
      case ast: YMap =>
        ast.contains(amfPosition, editionMode)
      case ast: YNode if ast.isNull =>
        true
      case ast: YNode =>
        ast.value.contains(amfPosition)
      case ast: YScalar =>
        AlsYScalarOps(ast).contains(amfPosition, editionMode)
      case seq: YSequence =>
        seq.contains(amfPosition, editionMode)
      case _ => super.contains(amfPosition, editionMode)
    }
  }
}
