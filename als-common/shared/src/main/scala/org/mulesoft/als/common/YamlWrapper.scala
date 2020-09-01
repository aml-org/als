package org.mulesoft.als.common

import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import amf.core.parser.{Position => AmfPosition}
import org.mulesoft.lexer.InputRange
import org.yaml.model._

object YamlWrapper {

  implicit class AlsInputRange(range: InputRange) {
    def toPositionRange =
      PositionRange(Position(AmfPosition(range.lineFrom, range.columnFrom)),
                    Position(AmfPosition(range.lineTo, range.columnTo)))

    def contains(amfPosition: AmfPosition): Boolean =
      toPositionRange.contains(Position(amfPosition))
  }

  implicit class AlsYPart(selectedNode: YPart) {

    def isArray: Boolean = selectedNode.isInstanceOf[YSequence]

    def isKey(amfPosition: AmfPosition): Boolean =
      selectedNode match {
        case entry: YMapEntry => entry.key.range.toPositionRange.contains(Position(amfPosition))
        case _                => false
      }

    def isValue(amfPosition: AmfPosition): Boolean =
      selectedNode match {
        case entry: YMapEntry => entry.value.range.toPositionRange.contains(Position(amfPosition))
        case _                => false
      }

    def contains(amfPosition: AmfPosition): Boolean =
      selectedNode.range.toPositionRange.contains(Position(amfPosition))
  }

  implicit class YMapEntryOps(entry: YMapEntry) extends AlsYPart(entry) {
    override def isArray: Boolean = false

    override def contains(position: AmfPosition): Boolean = {
      super.contains(position) && !outScalarValue(position)
    }

    private def outScalarValue(position: AmfPosition) =
      entry.range.lineFrom < position.line && (scalarValue(position) || nullValueOutIndentation(position))

    private def scalarValue(position: AmfPosition) =
      !entry.value.isNull && entry.value.asScalar.isDefined && !entry.value
        .contains(position)
    private def nullValueOutIndentation(position: AmfPosition) =
      entry.value.isNull && entry.key.range.columnFrom >= position.column

    private def containsInValue(position: AmfPosition) = {
      entry.value.tagType match {
        case YType.Map | YType.Seq                                                                 => false
        case YType.Null                                                                            => position.column > entry.range.columnFrom
        case _ if entry.value.tagType.toString == "!include" && entry.value.value.toString.isEmpty => true
      }
    }

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

    private def sameLevelBefore(amfPosition: AmfPosition) = {
      map.range.lineFrom > amfPosition.line && map.range.lineTo >= amfPosition.line && map.entries.nonEmpty
    }
  }

}
