package org.mulesoft.als.common

import amf.core.annotations.LexicalInformation
import amf.core.model.document.BaseUnit
import amf.core.model.domain.{AmfArray, AmfElement, AmfObject, DomainElement}
import amf.core.parser.FieldEntry
import amf.plugins.document.vocabularies.model.document.Dialect
import amf.plugins.document.vocabularies.model.domain.{NodeMapping, PropertyMapping}
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}

object AmfUtils {
  def getPropertyMappings(amfObject: AmfObject,
                          amfPosition: Position,
                          dialect: Dialect,
                          fieldEntry: Option[FieldEntry]): Seq[PropertyMapping] = {
    val mappings = getDialectNode(dialect, amfObject, fieldEntry) match {
      case Some(nm: NodeMapping) => nm.propertiesMapping()
      case _                     => Nil
    }
    fieldEntry match {
      case Some(e) =>
        if (e.value.value.position().exists(li => containsPosition(li, amfPosition, None)))
          mappings
            .find(
              pm =>
                pm.fields
                  .fields()
                  .exists(f => f.value.toString == e.field.value.iri()))
            .map(Seq(_))
            .getOrElse(Nil)
        else mappings
      case _ => mappings
    }
  }

  def getFieldEntryByPosition(amfObject: AmfObject, amfPosition: Position): Option[FieldEntry] =
    amfObject.fields
      .fields()
      .find(f =>
        f.value.value match {
          case _: AmfArray =>
            f.value.annotations
              .find(classOf[LexicalInformation])
              .exists(
                containsPosition(_,
                                 amfPosition,
                                 f.value.annotations
                                   .find(classOf[LexicalInformation])))
          case v =>
            v.position()
              .exists(
                containsPosition(_,
                                 amfPosition,
                                 f.value.annotations
                                   .find(classOf[LexicalInformation])))
      })

  def getNodeByPosition(bu: BaseUnit, amfPosition: Position): AmfObject =
    findInnerSon(bu, amfPosition)

  def findInnerSon(amfObject: AmfObject, amfPosition: Position): AmfObject = {
    def innerNode(amfObject: AmfObject): Option[FieldEntry] =
      amfObject.fields
        .fields()
        .find(f => {
          f.value.value match {
            case a: AmfArray =>
              a.position()
                .map(
                  p =>
                    containsPosition(p,
                                     amfPosition,
                                     f.value.annotations
                                       .find(classOf[LexicalInformation])))
                .getOrElse(
                  arrayContainsPosition(a,
                                        amfPosition,
                                        f.value.annotations
                                          .find(classOf[LexicalInformation])))

            case v =>
              v.position() match {
                case Some(p) =>
                  containsPosition(p,
                                   amfPosition,
                                   f.value.annotations
                                     .find(classOf[LexicalInformation]))
                case _ => false
              }
          }
        })

    var a: Option[AmfObject] = None
    var result               = amfObject
    do {
      a = innerNode(result).flatMap(entry =>
        entry.value.value match {
          case e: AmfArray =>
            findInArray(e,
                        amfPosition,
                        entry.value.annotations
                          .find(classOf[LexicalInformation]))
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

  /**
    * In the same line as the value and inside the range of a field
    * @param li -> LexicalInformation for the value
    * @param amfPosition -> Position (1 based) to search for
    * @param fieldLi -> Optional Lexical information for the field
    * @return -> Is in the same line as the value and inside the range of a field
    */
  def containsPosition(li: LexicalInformation, amfPosition: Position, fieldLi: Option[LexicalInformation]): Boolean =
    fieldLi.forall(l =>
      PositionRange(Position(l.range.start.line, l.range.start.column), Position(l.range.end.line, l.range.end.column))
        .contains(amfPosition)) &&
      Range(li.range.start.line, li.range.end.line + 1)
        .contains(amfPosition.line)

  private def arrayContainsPosition(amfArray: AmfArray,
                                    amfPosition: Position,
                                    fieldLi: Option[LexicalInformation]): Boolean =
    amfArray.values.exists(_.position() match {
      case Some(p) => containsPosition(p, amfPosition, fieldLi)
      case _       => false
    })

  private def findInArray(array: AmfArray,
                          amfPosition: Position,
                          fieldLi: Option[LexicalInformation]): Option[AmfElement] =
    array.values.find(v =>
      v.position() match {
        case Some(p) => containsPosition(p, amfPosition, fieldLi)
        case _       => false
    })

  def getDialectNode(dialect: Dialect, amfObject: AmfObject, fieldEntry: Option[FieldEntry]): Option[DomainElement] =
    dialect.declares.find {
      case s: NodeMapping =>
        s.nodetypeMapping.value() == amfObject.meta.`type`.head.iri() &&
          fieldEntry.forall(f => {
            s.propertiesMapping()
              .find(
                pm =>
                  pm.fields
                    .fields()
                    .exists(_.value.toString == f.field.value.iri()))
              .exists(_.mapTermKeyProperty().isNullOrEmpty)
          })
      case _ => false
    }
}
