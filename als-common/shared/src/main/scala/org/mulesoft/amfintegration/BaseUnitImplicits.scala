package org.mulesoft.amfmanager

import amf.core.model.document.{BaseUnit, DeclaresModel, Document, EncodesModel}
import amf.core.model.domain.{AmfArray, AmfElement, AmfObject, RecursiveShape}
import amf.core.parser.FieldEntry

import scala.collection.mutable

object BaseUnitImplicits {
  implicit class BaseUnitImp(bu: BaseUnit) {
    def flatRefs: Seq[BaseUnit] = {
      val set: mutable.Set[BaseUnit] = mutable.Set.empty

      def innerRefs(refs: Seq[BaseUnit]): Unit =
        refs.foreach { bu =>
          if (set.add(bu)) innerRefs(bu.references)
        }

      innerRefs(bu.references)
      set.toSeq
    }
//
//    def applyToFields[T](fn0: (Either[AmfElement, FieldEntry]) => T): List[T] = {
//      val l: mutable.ListBuffer[T] = mutable.ListBuffer()
//      def fn1(e: Either[AmfElement, FieldEntry]): T = {
//        val r = fn0(e)
//        l.append(r)
//        r
//      }
//      def nextFields(element: AmfElement): Boolean = {
//        element match {
//          case amfObject: AmfObject =>
//            amfObject.fields.fields().foreach { f =>
//              fn1(Right(f))
//              nextFields(f.element)
//            }
//          case array: AmfArray => array.values.foreach{ v =>
//            nextFields(v)
//          }
//          case _ => // ignore
//        }
//        fn1(Left(element))
//        element.isInstanceOf[RecursiveShape]
//      }
//
//      nextFields(bu)
//      fn1(Left(bu))
//      l.toList
//    }

    def identifier: String = bu.location().getOrElse(bu.id)
  }

}
