package org.mulesoft.als.common

import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import amf.core.parser.{Position => AmfPosition}
import org.mulesoft.lexer.InputRange
import org.yaml.model._

object YamlWrapper {

  implicit class AlsInputRange(range: InputRange) {
    def toPositionRange: PositionRange =
      PositionRange(Position(AmfPosition(range.lineFrom, range.columnFrom)),
                    Position(AmfPosition(range.lineTo, range.columnTo)))

    def contains(amfPosition: AmfPosition): Boolean =
      toPositionRange.contains(Position(amfPosition))
  }

  implicit class AlsYPart(selectedNode: YPart) {

    def isArray: Boolean = selectedNode.isInstanceOf[YSequence]

    def isKey(amfPosition: AmfPosition): Boolean =
      selectedNode match {
        case entry: YMapEntry => PositionRange(entry.key.range).contains(Position(amfPosition))
        case _                => false
      }

    def isValue(amfPosition: AmfPosition): Boolean =
      contains(amfPosition) && !isKey(amfPosition)

    private val selectedPositionRange: PositionRange = PositionRange(selectedNode.range)

    def contains(amfPosition: AmfPosition): Boolean =
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
  }

  implicit class YMapEntryOps(entry: YMapEntry) extends AlsYPart(entry) {
    override def isArray: Boolean = false

    override def contains(position: AmfPosition): Boolean =
      super.contains(position) &&
        !(outScalarValue(position) || outIndentation(position)) &&
        !isFirstChar(position) &&
        mapValueRespectsEntryKey(position)

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

  implicit class YNodeImplicits(yNode: YNode) {
    def withKey(k: String): YNode =
      YNode(YMap(IndexedSeq(YMapEntry(YNode(k), yNode)), yNode.sourceName))
  }

  implicit class YScalarImplicit(scalar: YScalar) {
    def unmarkedRange(): InputRange =
      if (scalar.mark.isInstanceOf[QuotedMark])
        scalar.range.copy(columnFrom = scalar.range.columnFrom + 1, columnTo = scalar.range.columnTo - 1)
      else scalar.range
  }

  implicit class AlsYMapOps(map: YMap) extends AlsYPart(map) {
    override def isArray: Boolean = false

    override def contains(amfPosition: AmfPosition): Boolean =
      (super.contains(amfPosition) || sameLevelBefore(amfPosition)) && respectIndentation(amfPosition)

    private def respectIndentation(amfPosition: AmfPosition) =
      map.entries.headOption.forall(_.range.columnFrom <= amfPosition.column)

    private def sameLevelBefore(amfPosition: AmfPosition) =
      map.range.lineFrom > amfPosition.line && map.range.lineTo >= amfPosition.line && map.entries.nonEmpty
  }

  implicit class AlsYScalarOps(scalar: YScalar) extends AlsYPart(scalar) {
    override def contains(amfPosition: AmfPosition): Boolean = super.contains(amfPosition) || lineContains(amfPosition)
    def lineContains(amfPosition: AmfPosition): Boolean =
      scalar.range.lineFrom <= amfPosition.line && ((scalar.range.lineTo >= amfPosition.line && scalar.range.columnFrom <= amfPosition.column) || scalar.value == null)
  }
}
