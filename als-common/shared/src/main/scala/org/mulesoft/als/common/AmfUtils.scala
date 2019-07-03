package org.mulesoft.als.common

import amf.core.annotations.LexicalInformation
import amf.core.model.document.BaseUnit
import amf.core.model.domain.{AmfArray, AmfElement, AmfObject, DomainElement}
import amf.core.parser.FieldEntry
import amf.plugins.document.vocabularies.model.document.Dialect
import amf.plugins.document.vocabularies.model.domain
import amf.plugins.document.vocabularies.model.domain.{NodeMapping, PropertyMapping}
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}

object AmfUtils {
  def getPropertyMappings(bu: BaseUnit, position: Position, dialect: Option[Dialect]): Seq[PropertyMapping] = {
    dialect
      .map(d => {
        val maybeMapping: Option[DomainElement] =
          getDialectNode(d, getNodeByPosition(bu, Position(position.line + 1, position.column)))
        maybeMapping match {
          case Some(nm: domain.NodeMapping) => nm.propertiesMapping()
          case _                            => Nil
        }
      })
      .getOrElse(Nil)
  }

  def getFieldEntryByPosition(bu: BaseUnit, amfPosition: Position): Option[FieldEntry] =
    getNodeByPosition(bu, amfPosition).fields
      .fields()
      .find(f => f.value.value.position().exists(containsLine(_, amfPosition.line)))

  def getNodeByPosition(bu: BaseUnit, amfPosition: Position): AmfObject =
    findInnerSon(bu, amfPosition)

  def findInnerSon(amfObject: AmfObject, amfPosition: Position): AmfObject = {
    def innerNode(amfObject: AmfObject): Option[FieldEntry] =
      amfObject.fields
        .fields()
        .find(f => {
          f.value.value match {
            case a: AmfArray =>
              arrayContainsPosition(a, amfPosition)
            case v =>
              v.position() match {
                case Some(p) =>
                  containsPosition(p, amfPosition)
                case _ => false
              }
          }
        })

    var a: Option[AmfObject] = Some(amfObject)
    var result               = amfObject
    do {
      a = innerNode(result).flatMap(entry =>
        entry.value.value match {
          case e: AmfArray =>
            findInArray(e, amfPosition)
              .flatMap {
                case o: AmfObject => Some(o)
                case _            => None
              }
          case e: AmfObject => Some(e)
          case _            => None
      })
      a.foreach(result = _)
    } while (a.isDefined)
    result
  }

  private def containsLine(li: LexicalInformation, line: Int): Boolean =
    Range(li.range.start.line, li.range.end.line + 1).contains(line)

  private def containsPosition(li: LexicalInformation, amfPosition: Position): Boolean =
    PositionRange(Position(li.range.start.line, li.range.start.column),
                  Position(li.range.end.line, li.range.end.column)) contains amfPosition

  private def arrayContainsPosition(amfArray: AmfArray, amfPosition: Position): Boolean =
    amfArray.values.exists(_.position() match {
      case Some(p) => containsPosition(p, amfPosition)
      case _       => false
    })

  private def findInArray(array: AmfArray, amfPosition: Position): Option[AmfElement] =
    array.values.find(v =>
      v.position() match {
        case Some(p) => containsPosition(p, amfPosition)
        case _       => false
    })

  def getDialectNode(dialect: Dialect, node: AmfObject): Option[DomainElement] = dialect.declares.find {
    case s: NodeMapping => s.nodetypeMapping.value() == node.meta.`type`.head.iri()
    case _              => false
  }
}
