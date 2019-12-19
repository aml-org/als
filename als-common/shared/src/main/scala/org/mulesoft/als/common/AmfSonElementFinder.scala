package org.mulesoft.als.common

import amf.core.annotations.{LexicalInformation, SynthesizedField, VirtualObject}
import amf.core.model.domain.{AmfArray, AmfElement, AmfObject}
import amf.core.parser.FieldEntry
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import amf.core.parser.{Position => AmfPosition}

import scala.collection.mutable.ArrayBuffer

object AmfSonElementFinder {

  implicit class AlsAmfObject(obj: AmfObject) {

    private def positionForArray(arr: AmfArray, amfPosition: AmfPosition, f: FieldEntry) = {
      arr
        .position()
        .map(
          p =>
            p.contains(amfPosition) && f.value.annotations
              .find(classOf[LexicalInformation])
              .forall(_.containsCompletely(amfPosition)))
        .getOrElse(
          arrayContainsPosition(arr,
                                amfPosition,
                                f.value.annotations
                                  .find(classOf[LexicalInformation])))
    }

    private def sonContainsNonVirtualPosition(amfElement: AmfElement, amfPosition: AmfPosition): Boolean =
      amfElement match {
        case amfObject: AmfObject =>
          amfObject.fields.fields().exists { f =>
            f.value.annotations.find(classOf[LexicalInformation]).exists(_.containsCompletely(amfPosition)) ||
            (f.value.annotations.contains(classOf[VirtualObject]) && sonContainsNonVirtualPosition(f.value.value,
                                                                                                   amfPosition))
          }
      }

    private def positionFinderFN(amfPosition: AmfPosition)(): FieldEntry => Boolean =
      (f: FieldEntry) => {
        f.value.value match {
          case arr: AmfArray =>
            positionForArray(arr, amfPosition, f) ||
              f.value.annotations.contains(classOf[SynthesizedField]) ||
              arr.values
                .collectFirst({
                  case obj: AmfObject
                      if obj.annotations.contains(classOf[VirtualObject]) &&
                        sonContainsNonVirtualPosition(obj, amfPosition) =>
                    obj
                })
                .nonEmpty

          case v =>
            v.position() match {
              case Some(p) =>
                p.contains(amfPosition) && f.value.value.annotations
                  .find(classOf[LexicalInformation])
                  .forall(_.containsCompletely(amfPosition))

              case _ =>
                f.value.annotations
                  .contains(classOf[SynthesizedField]) || f.value.value.annotations
                  .contains(classOf[VirtualObject])
            }
        }
      }

    def findSon(amfPosition: AmfPosition, filterFns: Seq[FieldEntry => Boolean]): AmfObject =
      findSonWithStack(amfPosition, filterFns)._1

    def findSonWithStack(amfPosition: AmfPosition,
                         filterFns: Seq[FieldEntry => Boolean]): (AmfObject, Seq[AmfObject]) = {
      val posFilter = positionFinderFN(amfPosition)

      def innerNode(amfObject: AmfObject): Option[FieldEntry] =
        amfObject.fields
          .fields()
          .filter(f => {
            filterFns.forall(fn => fn(f)) && posFilter(f)
          }) match {
          case Nil => None
          case list =>
            list
              .filterNot(v => v.value.annotations.contains(classOf[VirtualObject]))
              .lastOption
              .orElse(list.lastOption)
        }

      var a: Iterable[AmfObject]        = None // todo: recursive instead of tail recursive?
      val stack: ArrayBuffer[AmfObject] = ArrayBuffer()
      var result                        = obj
      do {
        a = innerNode(result).flatMap(entry =>
          entry.value.value match {
            case e: AmfArray =>
              e.findSon(amfPosition, filterFns: Seq[FieldEntry => Boolean])
                .flatMap {
                  case o: AmfObject
                      if entry.value.annotations
                        .find(classOf[LexicalInformation])
                        .forall(_.containsCompletely(amfPosition)) || o.annotations.contains(classOf[VirtualObject]) =>
                    Some(o)
                  case _ => None
                }
            case e: AmfObject => Some(e)
            case _            => None
        })
        a.headOption.foreach(head => {
          stack.prepend(result)
          result = head
        })
      } while (a.nonEmpty)
      (result, stack)
    }
  }

  implicit class AlsAmfArray(array: AmfArray) {
    private def minor(left: AmfElement, right: AmfElement) = {
      right
        .position() match {
        case Some(LexicalInformation(rightRange)) =>
          left.position() match {
            case Some(LexicalInformation(leftRange)) =>
              if (leftRange.contains(rightRange)) right
              else left
            case _ => right
          }
        case None => left
      }
    }

    @scala.annotation.tailrec
    private def findMinor(elements: List[AmfElement]): Option[AmfElement] = {
      elements match {
        case Nil         => None
        case head :: Nil => Some(head)
        case list =>
          val m = minor(list.head, list.tail.head)
          findMinor(m +: list.tail.tail)
      }
    }

    def findSon(amfPosition: AmfPosition, filterFns: Seq[FieldEntry => Boolean]): Option[AmfElement] = {
      val sons: Seq[AmfElement] = array.values.filter(v =>
        v.position() match {
          case Some(p) if p.contains(amfPosition) => true
          case _                                  => v.annotations.contains(classOf[VirtualObject])
      })
      findMinor(sons.filter(_.annotations.contains(classOf[VirtualObject])).toList).orElse(findMinor(sons.toList))
    }
  }

  implicit class AlsAmfElement(element: AmfElement) {

    def findSon(position: AmfPosition, filterFns: Seq[FieldEntry => Boolean]): Option[AmfElement] = { // todo: recursive with cycle control?
      element match {
        case obj: AmfObject  => Some(obj.findSon(position, filterFns))
        case array: AmfArray => array.findSon(position, filterFns)
        case _               => None
      }
    }
  }

  implicit class AlsLexicalInformation(li: LexicalInformation) {

    def contains(pos: AmfPosition): Boolean =
      Range(li.range.start.line, li.range.end.line + 1)
        .contains(pos.line) && !isLastLine(pos)

    def isLastLine(pos: AmfPosition): Boolean =
      li.range.end.column == 0 && pos.line == li.range.end.line

    def containsCompletely(pos: AmfPosition): Boolean =
      PositionRange(Position(li.range.start), Position(li.range.end))
        .contains(Position(pos)) && !isLastLine(pos)
  }

  private def arrayContainsPosition(amfArray: AmfArray,
                                    amfPosition: AmfPosition,
                                    fieldLi: Option[LexicalInformation]): Boolean =
    amfArray.values.exists(_.position() match {
      case Some(p) =>
        p.contains(amfPosition) && fieldLi.forall(_.containsCompletely(amfPosition))
      case _ => false
    })
}
