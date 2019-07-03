package org.mulesoft.als.common

import amf.core.annotations.LexicalInformation
import amf.core.model.document.BaseUnit
import amf.core.model.domain.{AmfArray, AmfElement, AmfObject}
import amf.core.parser.FieldEntry
import amf.plugins.document.vocabularies.model.domain.DialectDomainElement
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}

object AmfUtils {
  def getNodeByPosition(bu: BaseUnit, amfPosition: Position): AmfObject =
    findInnerSon(bu, amfPosition)

  def findInnerSon(amfObject: AmfObject, amfPosition: Position): AmfObject = {
    def innerNode: Option[FieldEntry] =
      amfObject.fields
        .fields()
        .find(f => {
          f.value.value match {
            case a: AmfArray =>
              arrayContainsPosition(a, amfPosition) ||
                f.value.annotations.find(classOf[LexicalInformation]).exists(li => containsPosition(li, amfPosition))
            case v =>
              v.annotations.find(classOf[LexicalInformation]) match {
                case Some(p) =>
                  containsPosition(p, amfPosition)
                case _ =>
                  f.value.annotations.find(classOf[LexicalInformation]).exists(li => containsPosition(li, amfPosition))
              }
          }
        })

    val maybeEntry: Option[FieldEntry] = innerNode
    maybeEntry
      .map(entry =>
        entry.value.value match {
          case e: AmfArray =>
            findInArray(e, amfPosition)
              .map {
                case o: AmfObject => findInnerSon(o, amfPosition)
                case _            => amfObject
              }
              .getOrElse(amfObject)
          case e: AmfObject => findInnerSon(e, amfPosition)
          case _            => amfObject
      })
      .getOrElse(amfObject)
  }

  def getFieldEntryByPosition(amfObject: AmfObject, amfPosition: Position): Option[FieldEntry] = {
    def innerFieldEntry(fieldEntries: Iterable[FieldEntry], amfPosition: Position): Option[FieldEntry] = {
      val maybeEntry = fieldEntries.find(fieldEntry => {
        fieldEntry.value.annotations
          .find(classOf[LexicalInformation])
          .exists(li => containsPosition(li, amfPosition)) ||
        fieldEntry.value.value.position().exists(containsPosition(_, amfPosition))
      })
      maybeEntry.map(fieldEntry => {
        val inner = fieldEntry.value.value match {
          case dde: DialectDomainElement => innerFieldEntry(dde.fields.fields(), amfPosition)
          case a: AmfArray =>
            a.values.find(element => element.position().exists(li => containsPosition(li, amfPosition))) match {
              case Some(element: DialectDomainElement) => innerFieldEntry(element.fields.fields(), amfPosition)
              case _                                   => None
            }
          case _ => None
        }
        inner.getOrElse(fieldEntry)
      })
    }
    innerFieldEntry(amfObject.fields.fields(), amfPosition)
  }

  private def containsPosition(li: LexicalInformation, amfPosition: Position): Boolean =
    PositionRange(Position(li.range.start.line, li.range.start.column),
                  Position(li.range.end.line, li.range.end.column)) contains amfPosition

  private def arrayContainsPosition(amfArray: AmfArray, amfPosition: Position): Boolean =
    amfArray.values.exists(v =>
      v.annotations.find(classOf[LexicalInformation]) match {
        case Some(p) => containsPosition(p, amfPosition)
        case _       => false
    })

  private def findInArray(array: AmfArray, amfPosition: Position): Option[AmfElement] = {
    array.values.find(v =>
      v.annotations.find(classOf[LexicalInformation]) match {
        case Some(p) => containsPosition(p, amfPosition)
        case _       => false
    })
  }
}
