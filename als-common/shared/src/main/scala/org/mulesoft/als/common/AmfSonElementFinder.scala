package org.mulesoft.als.common

import amf.core.annotations.{LexicalInformation, SourceAST, SynthesizedField, VirtualObject}
import amf.core.metamodel.{Field, ModelDefaultBuilder}
import amf.core.metamodel.Type.ArrayLike
import amf.core.metamodel.domain.{DataNodeModel, DomainElementModel}
import amf.core.model.document.EncodesModel
import amf.core.model.domain.{AmfArray, AmfElement, AmfObject, AmfScalar, DataNode}
import amf.core.parser.{Annotations, FieldEntry, Position => AmfPosition}
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.amfintegration.AmfImplicits._
import org.mulesoft.amfintegration.FieldEntryOrdering
import org.yaml.model.{YMapEntry, YNode, YPart, YType}
import YamlWrapper._
import amf.core.parser.{Position => AmfPosition}
import org.mulesoft.amfintegration.AmfImplicits._

import scala.collection.mutable.ArrayBuffer

object AmfSonElementFinder {

  implicit class AlsAmfObject(obj: AmfObject) {

    private def sonContainsNonVirtualPosition(amfElement: AmfElement, amfPosition: AmfPosition): Boolean =
      amfElement match {
        case amfObject: AmfObject =>
          amfObject.fields.fields().exists { f =>
            containsAsValue(f.value.annotations.ast(), amfPosition) ||
            (f.value.annotations.contains(classOf[VirtualObject]) && sonContainsNonVirtualPosition(f.value.value,
                                                                                                   amfPosition))
          }
      }

    private def positionFinderFN(amfPosition: AmfPosition, location: Option[String])(): FieldEntry => Boolean =
      (f: FieldEntry) => {
        val value = f.value.value
        location.forall(l => value.annotations.location().isEmpty || value.annotations.location().contains(l)) &&
        (value match {
          case arr: AmfArray =>
            f.isArrayIncluded(amfPosition) ||
              f.value.annotations
                .contains(classOf[SynthesizedField]) || (!f.value.annotations.contains(classOf[LexicalInformation]) &&
              arr.values
                .collectFirst({
                  case obj: AmfObject
                      if obj.annotations.contains(classOf[VirtualObject]) &&
                        sonContainsNonVirtualPosition(obj, amfPosition) || obj.containsPosition(amfPosition) =>
                    obj
                })
                .nonEmpty)

          case v =>
            v.position() match {
              case Some(p) =>
                p.contains(amfPosition) ||
                  (v.isInstanceOf[AmfObject] &&
                    containsAsValue(f.value.annotations.ast(), amfPosition))
              case _ =>
                f.value.annotations
                  .contains(classOf[SynthesizedField]) || f.value.value.annotations
                  .contains(classOf[VirtualObject])
            }
        })
      }

    def findSon(amfPosition: AmfPosition, filterFns: Seq[FieldEntry => Boolean]): AmfObject =
      findSonWithStack(amfPosition, None, filterFns)._1

    private def containsAsValue(maybePart: Option[YPart], amfPosition: AmfPosition): Boolean =
      maybePart.exists(_.isValue(amfPosition))

    def findSonWithStack(amfPosition: AmfPosition,
                         location: Option[String],
                         filterFns: Seq[FieldEntry => Boolean]): (AmfObject, Seq[AmfObject]) = {
      val posFilter = positionFinderFN(amfPosition, location)

      def innerNode(amfObject: AmfObject): Option[FieldEntry] =
        amfObject.fields
          .fields()
          .filter(f => {
            filterFns.forall(fn => fn(f)) && posFilter(f)
          }) match {
          case Nil =>
            None
          case list =>
            val entries = list
              .filterNot(
                v =>
                  v.value.annotations.contains(classOf[VirtualObject]) || v.value.annotations
                    .contains(classOf[SynthesizedField]))
            entries.lastOption
              .orElse(list.lastOption)
        }

      var a: Iterable[AmfObject]        = None // todo: recursive instead of tail recursive?
      val stack: ArrayBuffer[AmfObject] = ArrayBuffer()
      var result                        = obj
      do {
        a = innerNode(result).flatMap(entry =>
          entry.value.value match {
            case e: AmfArray =>
              e.findSon(amfPosition, location,filterFns: Seq[FieldEntry => Boolean]) match {
                case Some(o: AmfObject)
                    if (entry.value.annotations
                      .find(classOf[LexicalInformation])
                      .forall(_.contains(amfPosition)) && o.containsPosition(amfPosition)) || o.annotations.contains(
                      classOf[VirtualObject]) =>
                  Some(o)
                case _ if entry.field.`type`.isInstanceOf[ArrayLike] =>
                  entry.field.`type`.asInstanceOf[ArrayLike].element match {
                    case d: DomainElementModel
                        if d.`type`.headOption.exists(_.iri() == DataNodeModel.`type`.head.iri()) =>
                      e.values.collectFirst({ case d: DataNode => d })
                    case d: DomainElementModel
                        if d.`type`.headOption.exists(_.iri() == DomainElementModel.`type`.head.iri()) =>
                      e.values.collectFirst({ case o: AmfObject => o })
                    case m: ModelDefaultBuilder => Some(m.modelInstance)
                    case _                      => None
                  }
                case _ => None
              }
            case e: AmfObject
                if e.containsPosition(amfPosition) || containsAsValue(entry.value.annotations.ast(), amfPosition) =>
              Some(e)
            case _ => None
        })
        a.headOption.foreach(head => {
          stack.prepend(result)
          result = head
        })
      } while (a.nonEmpty && !stack.contains(result))
      (result, stack)
    }
  }

  implicit class AlsAmfArray(array: AmfArray) {
    private def minor(left: AmfElement, right: AmfElement) =
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

    def findSon(amfPosition: AmfPosition,
                location: Option[String],
                filterFns: Seq[FieldEntry => Boolean]): Option[AmfElement] = {
      val sons: Seq[AmfElement] = array.values.filter(v =>
        v.position() match {
          case Some(p) if p.contains(amfPosition) && location.forall(v.location().contains(_)) => true
          case _                                                                               => v.annotations.contains(classOf[VirtualObject])
      })
      findMinor(sons.filter(_.annotations.contains(classOf[VirtualObject])).toList).orElse(findMinor(sons.toList))
    }
  }

  implicit class AlsAmfElement(element: AmfElement) {

    def findSon(position: AmfPosition,
                location: Option[String],
                filterFns: Seq[FieldEntry => Boolean]): Option[AmfElement] = { // todo: recursive with cycle control?
      element match {
        case obj: AmfObject  => Some(obj.findSon(position, filterFns))
        case array: AmfArray => array.findSon(position, location, filterFns)
        case _               => None
      }
  }
}
