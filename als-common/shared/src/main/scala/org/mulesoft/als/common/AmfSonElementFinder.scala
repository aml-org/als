package org.mulesoft.als.common

import amf.core.annotations.LexicalInformation
import amf.core.model.domain.{AmfArray, AmfElement, AmfObject}
import amf.core.parser.FieldEntry
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}

object AmfSonElementFinder {

  implicit class AlsAmfObject(obj: AmfObject) {
    def findSon(amfPosition: Position): AmfObject = {
      def innerNode(amfObject: AmfObject): Option[FieldEntry] =
        amfObject.fields
          .fields()
          .find(f => {
            f.value.value match {
              case a: AmfArray =>
                a.position()
                  .map(
                    p =>
                      p.contains(amfPosition) && f.value.annotations
                        .find(classOf[LexicalInformation])
                        .forall(_.containsCompletly(amfPosition)))
                  .getOrElse(
                    arrayContainsPosition(a,
                                          amfPosition,
                                          f.value.annotations
                                            .find(classOf[LexicalInformation])))

              case v =>
                v.position() match {
                  case Some(p) =>
                    p.contains(amfPosition) && f.value.value.annotations
                      .find(classOf[LexicalInformation])
                      .forall(_.containsCompletly(amfPosition))

                  case _ => false
                }
            }
          })

      var a: Option[AmfObject] = None // todo: recursive instead of tail recursive?
      var result               = obj
      do {
        a = innerNode(result).flatMap(entry =>
          entry.value.value match {
            case e: AmfArray =>
              e.findSon(amfPosition)
                .flatMap {
                  case o: AmfObject
                      if entry.value.annotations
                        .find(classOf[LexicalInformation])
                        .forall(_.containsCompletly(amfPosition)) =>
                    Some(o)
                  case _ => None
                }
            case e: AmfObject => Some(e)
            case _            => None
        })
        a.foreach(result = _)
      } while (a.isDefined)
      result
    }
  }

  implicit class AlsAmfArray(array: AmfArray) {
    def findSon(amfPosition: Position): Option[AmfElement] =
      array.values.find(v =>
        v.position() match {
          case Some(p) => p.contains(amfPosition)
          case _       => false
      })
  }

  implicit class AlsAmfElement(element: AmfElement) {

    def findSon(position: Position): Option[AmfElement] = { // todo: recursive with cicly control?
      element match {
        case obj: AmfObject => Some(obj.findSon(position))
        case arra: AmfArray => arra.findSon(position)
        case other          => None
      }
    }
  }

  implicit class AlsLexicalInformation(li: LexicalInformation) {

    /**
      * In the same line as the value and inside the range of a field
      * @param li -> LexicalInformation for the value
      * @param amfPosition -> Position (1 based) to search for
      * @return -> Is in the same line as the value and inside the range of a field
      */
    def contains(pos: Position): Boolean = Range(li.range.start.line, li.range.end.line + 1).contains(pos.line)

    def containsCompletly(pos: Position): Boolean =
      PositionRange(Position(li.range.start.line, li.range.start.column),
                    Position(li.range.end.line, li.range.end.column))
        .contains(pos)
  }

  private def arrayContainsPosition(amfArray: AmfArray,
                                    amfPosition: Position,
                                    fieldLi: Option[LexicalInformation]): Boolean =
    amfArray.values.exists(_.position() match {
      case Some(p) => p.contains(amfPosition) && fieldLi.forall(_.containsCompletly(amfPosition))
      case _       => false
    })

}
