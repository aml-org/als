package org.mulesoft.als.common

import amf.core.metamodel.ModelDefaultBuilder
import amf.core.metamodel.Type.ArrayLike
import amf.core.metamodel.domain.{DataNodeModel, DomainElementModel, ShapeModel}
import amf.core.model.domain.{AmfArray, AmfElement, AmfObject, DataNode}
import amf.core.parser.{FieldEntry, Position => AmfPosition}
import amf.core.vocabulary.Namespace
import amf.plugins.document.vocabularies.metamodel.domain.DialectDomainElementModel
import amf.plugins.document.vocabularies.model.document.Dialect
import amf.plugins.document.vocabularies.model.domain.DialectDomainElement
import org.mulesoft.als.common.YamlWrapper._
import org.mulesoft.amfintegration.AmfImplicits._
import org.yaml.model.YPart
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
            (f.value.annotations.isVirtual && sonContainsNonVirtualPosition(f.value.value, amfPosition))
          }
      }

    private def positionFinderFN(amfPosition: AmfPosition, location: Option[String])(): FieldEntry => Boolean =
      (f: FieldEntry) => {
        val value = f.value.value
        location.forall(l => value.annotations.location().isEmpty || value.annotations.location().contains(l)) &&
        (value match {
          case arr: AmfArray =>
            f.isArrayIncluded(amfPosition) ||
              f.value.annotations.isSynthesized || (f.value.annotations.lexicalInformation().isEmpty &&
              arr.values
                .collectFirst({
                  case obj: AmfObject
                      if f.value.annotations.containsPosition(amfPosition).getOrElse(true) &&
                        (obj.annotations.isVirtual &&
                          sonContainsNonVirtualPosition(obj, amfPosition) || obj.containsPosition(amfPosition)) =>
                    obj
                })
                .nonEmpty)

          case v =>
            f.value.annotations.containsPosition(amfPosition).getOrElse(true) &&
              v.annotations
                .containsPosition(amfPosition)
                .getOrElse(f.value.annotations.isSynthesized || f.value.value.annotations.isVirtual)
        })
      }

    def findSon(amfPosition: AmfPosition, filterFns: Seq[FieldEntry => Boolean], definedBy: Dialect): AmfObject =
      findSonWithStack(amfPosition, None, filterFns, definedBy)._1

    private def containsAsValue(maybePart: Option[YPart], amfPosition: AmfPosition): Boolean =
      maybePart.exists(_.isValue(amfPosition))

    def findSonWithStack(amfPosition: AmfPosition,
                         location: Option[String],
                         filterFns: Seq[FieldEntry => Boolean],
                         definedBy: Dialect): (AmfObject, Seq[AmfObject]) = {
      val posFilter = positionFinderFN(amfPosition, location)

      def innerNode(amfObject: AmfObject): Option[FieldEntry] =
        amfObject.fields
          .fields()
          .filter(f => {
            filterFns.forall(fn => fn(f)) &&
            posFilter(f)
          }) match {
          case Nil =>
            None
          case list =>
            val entries = list
              .filterNot(v => v.value.annotations.isVirtual || v.value.annotations.isSynthesized)
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
              e.findChild(amfPosition, location, filterFns: Seq[FieldEntry => Boolean]) match {
                case Some(o: AmfObject) if o.containsPosition(amfPosition) || o.annotations.isVirtual =>
                  Some(o)
                case _ if entry.field.`type`.isInstanceOf[ArrayLike] =>
                  matchInnerArrayElement(entry, e, definedBy, result)
                case _ => None
              }
            case e: AmfObject
                if e.containsPosition(amfPosition) || containsAsValue(entry.value.annotations.ast(), amfPosition) =>
              Some(e)
            case _ => None
        })
        a.headOption.foreach(head => {
          if (!stack.contains(head)) {
            stack.prepend(result)
            result = head
          }
        })
      } while (a.nonEmpty && a.head == result)
      (result, stack)
    }
  }

  private def matchInnerArrayElement(entry: FieldEntry, e: AmfArray, definedBy: Dialect, parent: AmfObject) =
    entry.field.`type`.asInstanceOf[ArrayLike].element match {
      case d: DialectDomainElementModel =>
        val maybeMapping = parent match {
          case parentDd: DialectDomainElement =>
            parentDd.definedBy.propertiesMapping().find { pm =>
              pm.nodePropertyMapping().option().contains(entry.field.value.iri())
            }
          case _ => None
        }
        maybeMapping
          .flatMap(_.objectRange().headOption)
          .flatMap(_.option())
          .flatMap(definedBy.findNodeMapping)
          .map { nodeMapping =>
            DialectDomainElement()
              .withInstanceTypes(nodeMapping.nodetypeMapping.value() +: d.`type`.map(_.iri()))
              .withDefinedBy(nodeMapping)
          }
      case d: DomainElementModel if d.`type`.headOption.exists(_.iri() == DataNodeModel.`type`.head.iri()) =>
        e.values.collectFirst({ case d: DataNode => d })
      case d: DomainElementModel if d.`type`.headOption.exists(_.iri() == DomainElementModel.`type`.head.iri()) =>
        e.values.collectFirst({ case o: AmfObject => o })
      case s: ShapeModel if s.`type`.headOption.exists(_.iri() == ShapeModel.`type`.head.iri()) =>
        e.values.collectFirst({ case o: AmfObject => o })
      case m: ModelDefaultBuilder =>
        Some(m.modelInstance)
      case _ => None
    }

  implicit class AlsAmfArray(array: AmfArray) {
    private def minor(left: AmfElement, right: AmfElement): AmfElement =
      (left.annotations.ast(), right.annotations.ast()) match {
        case (Some(l), Some(r)) =>
          if (l.contains(r.range)) right
          else left
        case (None, Some(_)) => right
        //        case (Some(_), None) => left
        case _ =>
          left // todo: check?? (should be None?)
      }

    @scala.annotation.tailrec
    private def findMinor(elements: List[AmfElement]): Option[AmfElement] =
      elements match {
        case Nil         => None
        case head :: Nil => Some(head)
        case list =>
          val m = minor(list.head, list.tail.head)
          findMinor(m +: list.tail.tail)
      }

    def findChild(amfPosition: AmfPosition,
                  location: Option[String],
                  filterFns: Seq[FieldEntry => Boolean]): Option[AmfElement] = {
      val children: Seq[AmfElement] = array.values.filter(v =>
        v.annotations.ast() match {
          case Some(p) if p.contains(amfPosition) =>
            true
          case _ =>
            v.annotations.isVirtual
      })
      findMinor(children.filter(_.annotations.isVirtual).toList).orElse(findMinor(children.toList))
    }
  }

  implicit class AlsAmfElement(element: AmfElement) {

    def findSon(position: AmfPosition,
                location: Option[String],
                filterFns: Seq[FieldEntry => Boolean],
                definedBy: Dialect): Option[AmfElement] = // todo: recursive with cycle control?
      element match {
        case obj: AmfObject =>
          Some(obj.findSon(position, filterFns, definedBy))
        case array: AmfArray =>
          array.findChild(position, location, filterFns)
        case _ =>
          None
      }
  }
}
