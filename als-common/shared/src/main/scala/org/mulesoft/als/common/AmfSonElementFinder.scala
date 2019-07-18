package org.mulesoft.als.common

import amf.core.annotations.LexicalInformation
import amf.core.model.domain.{AmfArray, AmfElement, AmfObject}
import amf.core.parser.FieldEntry
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}

object AmfSonElementFinder {

  implicit class AlsAmfObject(obj: AmfObject) {

    private def minor(left: FieldEntry, right: FieldEntry): FieldEntry = {
      right.value.value.position().orElse(right.value.annotations.find(classOf[LexicalInformation])) match {
        case Some(LexicalInformation(rightRange)) =>
          left.value.value.position().orElse(left.value.annotations.find(classOf[LexicalInformation])) match {
            case Some(LexicalInformation(leftRange)) =>
              if (leftRange.contains(rightRange)) right
              else left
            case _ => right
          }
        case None => left
      }
    }

    private def findMinor(fields: Seq[FieldEntry]): Option[FieldEntry] = {
      fields match {
        case Nil          => None
        case head :: Nil  => Some(head)
        case head :: tail => findMinor(tail)
      }
    }

    private def positionFinderFN(amfPosition: Position)(): FieldEntry => Boolean = (f: FieldEntry) => {
      f.value.value match {
        case a: AmfArray =>
          a.position()
            .map(
              p =>
                p.contains(amfPosition) && f.value.annotations
                  .find(classOf[LexicalInformation])
                  .forall(_.containsCompletely(amfPosition)))
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
                .forall(_.containsCompletely(amfPosition))

            case _ => false
          }
      }
    }
    def findSon(amfPosition: Position, filterFns: Seq[FieldEntry => Boolean]): AmfObject = {
      val posFilter = positionFinderFN(amfPosition)
      def innerNode(amfObject: AmfObject): Option[FieldEntry] =
        amfObject.fields
          .fields()
          .filter(f => {
            filterFns.forall(fn => fn(f)) && posFilter(f)
          }) match {
          case Nil          => None
          case head :: Nil  => Some(head)
          case head :: tail => findMinor(tail).orElse(Some(head))
        }

      var a: Iterable[AmfObject] = None // todo: recursive instead of tail recursive?
      var result                 = obj
      do {
        a = innerNode(result).flatMap(entry =>
          entry.value.value match {
            case e: AmfArray =>
              e.findSon(amfPosition, filterFns: Seq[FieldEntry => Boolean])
                .flatMap {
                  case o: AmfObject
                      if entry.value.annotations
                        .find(classOf[LexicalInformation])
                        .forall(_.containsCompletely(amfPosition)) =>
                    Some(o)
                  case _ => None
                }
            case e: AmfObject => Some(e)
            case _            => None
        })
        a.headOption.foreach(result = _)
      } while (a.nonEmpty)
      result
    }
  }

  implicit class AlsAmfArray(array: AmfArray) {
    def findSon(amfPosition: Position, filterFns: Seq[FieldEntry => Boolean]): Option[AmfElement] =
      array.values.find(v =>
        v.position() match {
          case Some(p) => p.contains(amfPosition)
          case _       => false
      })
  }

  implicit class AlsAmfElement(element: AmfElement) {

    def findSon(position: Position, filterFns: Seq[FieldEntry => Boolean]): Option[AmfElement] = { // todo: recursive with cycle control?
      element match {
        case obj: AmfObject  => Some(obj.findSon(position, filterFns))
        case array: AmfArray => array.findSon(position, filterFns)
        case _               => None
      }
    }
  }

  implicit class AlsLexicalInformation(li: LexicalInformation) {

    def contains(pos: Position): Boolean =
      Range(li.range.start.line, li.range.end.line + 1)
        .contains(pos.line) && !isLastLine(pos)

    def isLastLine(pos: Position): Boolean =
      li.range.end.column == 0 && pos.line == li.range.end.line

    def containsCompletely(pos: Position): Boolean =
      PositionRange(Position(li.range.start.line, li.range.start.column),
                    Position(li.range.end.line, li.range.end.column))
        .contains(pos) && !isLastLine(pos)
  }

  private def arrayContainsPosition(amfArray: AmfArray,
                                    amfPosition: Position,
                                    fieldLi: Option[LexicalInformation]): Boolean =
    amfArray.values.exists(_.position() match {
      case Some(p) =>
        p.contains(amfPosition) && fieldLi.forall(_.containsCompletely(amfPosition))
      case _ => false
    })

}
