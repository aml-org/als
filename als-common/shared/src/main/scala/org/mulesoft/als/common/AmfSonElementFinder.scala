package org.mulesoft.als.common

import amf.core.metamodel.{Field, ModelDefaultBuilder}
import amf.core.annotations.SourceAST
import amf.core.metamodel.ModelDefaultBuilder
import amf.core.metamodel.Type.ArrayLike
import amf.core.metamodel.document.{BaseUnitModel, DocumentModel}
import amf.core.metamodel.domain.{DataNodeModel, DomainElementModel, ShapeModel}
import amf.core.model.domain._
import amf.core.parser
import amf.core.parser.{FieldEntry, Position => AmfPosition}
import amf.plugins.document.vocabularies.metamodel.domain.DialectDomainElementModel
import amf.plugins.document.vocabularies.model.document.Dialect
import amf.plugins.document.vocabularies.model.domain.DialectDomainElement
import amf.plugins.document.webapi.annotations.DeclarationKeys
import amf.plugins.domain.webapi.metamodel.bindings.{
  ChannelBindingModel,
  EmptyBindingModel,
  MessageBindingModel,
  OperationBindingModel,
  OperationBindingsModel,
  ServerBindingModel,
  ServerBindingsModel
}
import amf.plugins.domain.webapi.metamodel.bindings._
import org.mulesoft.als.common.YamlWrapper._
import org.mulesoft.amfintegration.AmfImplicits._
import org.yaml.model.YPart
import org.mulesoft.amfintegration.AmfImplicits.AmfAnnotationsImp
import org.mulesoft.amfintegration.AmfImplicits.{AmfAnnotationsImp, _}

import scala.language.implicitConversions
import scala.annotation.tailrec
import scala.collection.mutable
import scala.collection.mutable.{ArrayBuffer, ListBuffer}

object AmfSonElementFinder {

  implicit class AlsAmfObject(obj: AmfObject) {

    private def sonContainsNonVirtualPosition(amfElement: AmfElement, amfPosition: AmfPosition): Boolean =
      amfElement match {
        case amfObject: AmfObject =>
          amfObject.fields.fields().exists { f =>
            (f.value.annotations.isVirtual && sonContainsNonVirtualPosition(f.value.value, amfPosition))
          }
      }

    private def positionFinderFN(amfPosition: AmfPosition, location: Option[String]): FieldEntry => Boolean =
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
                      if f.value.annotations.containsAstPosition(amfPosition).getOrElse(true) &&
                        (obj.annotations.isVirtual &&
                          sonContainsNonVirtualPosition(obj, amfPosition) || obj.containsPosition(amfPosition)) =>
                    obj
                })
                .nonEmpty)

          case v =>
            f.value.annotations.containsAstPosition(amfPosition).getOrElse(true) &&
              v.annotations
                .ast()
                .map(ast => ast.contains(amfPosition, editionMode = true))
                .getOrElse(f.value.annotations.isSynthesized || f.value.value.annotations.isVirtual)
        })
      }

    def findSon(amfPosition: AmfPosition, filterFns: Seq[FieldEntry => Boolean], definedBy: Dialect): AmfObject =
      findSonWithStack(amfPosition, "", filterFns, definedBy)._1

    case class SonFinder(location: String, definedBy: Dialect, amfPosition: AmfPosition) {

      val fieldAstFilter: FieldEntry => Boolean = (f: FieldEntry) =>
        f.value.annotations
          .containsAstPosition(amfPosition)
          .getOrElse(
            f.value.annotations.isInferred || f.value.annotations.isVirtual || f.value.annotations.isSynthesized || isDeclares(
              f))
      // why do we assume that inferred/virtual/synthetized/declared would have the position? should we not look inside? what if there is more than one such case?

      private val traversed: ListBuffer[AmfObject] = ListBuffer()

      val fieldFilters: Seq[FieldEntry => Boolean] = Seq(
        (f: FieldEntry) => f.field != BaseUnitModel.References,
        fieldAstFilter
      )

      // only object can have sons. Scalar and arrays are field from objects.
      def buildStack(obj: AmfObject): Seq[(AmfObject, Option[FieldEntry])] = {
        if (traversed.contains(obj)) Nil
        else {
          traversed += obj
          val f = findField(obj)
          val son: Option[AmfObject] = f.flatMap { fe =>
            nextObject(fe, obj)
          }
          son.map(buildStack).getOrElse(Nil) :+ (obj, f)
        }
      }

      def nextObject(fe: FieldEntry, parent: AmfObject): Option[AmfObject] = {
        if (fe.objectSon && fe.value.value.location().forall(_ == location))
          fe.value.value match {
            case e: AmfArray =>
              nextObject(e).orElse(buildFromMeta(parent, fe, e))
            case o: AmfObject if o.containsPosition(amfPosition) =>
              Some(o)
            case _ =>
              None
          } else None
      }

      def buildFromMeta(parent: AmfObject, fe: FieldEntry, arr: AmfArray): Option[AmfObject] = {
        if (explicitArray(fe, parent, definedBy)) matchInnerArrayElement(fe, arr, definedBy, parent)
        else None
      }

      def nextObject(array: AmfArray): Option[AmfObject] = {
        val objects = array.values.collect({ case o: AmfObject => o })
        objects
          .find(_.annotations.containsPosition(amfPosition))
          .orElse(objects.find(v => v.annotations.isVirtual || v.annotations.isSynthesized))
      }

      private def declaredPosition(fe: FieldEntry): Boolean =
        isDeclares(fe) &&
          fe.value.annotations.find(classOf[DeclarationKeys]).exists { dk =>
            dk.keys.exists(k => k.entry.contains(amfPosition))
          }

      def findField(amfObject: AmfObject): Option[FieldEntry] = {
        amfObject.fields.fields().filter(f => fieldFilters.forall(fn => fn(f))) match {
          case Nil =>
            None
          case head :: Nil =>
            Some(head)
          case list =>
            val entries = list.filterNot(v => v.value.annotations.isVirtual || v.value.annotations.isSynthesized)
            entries.find(declaredPosition) match { // if the position is inside a declaration key range, then prioritize
              case Some(declares) =>
                Some(declares)
              case None =>
                entries.filterNot(isDeclares).lastOption.orElse(list.lastOption).map(f => f)
            }

        }
      }
    }

    def findSonWithStack(amfPosition: AmfPosition,
                         location: String,
                         filterFns: Seq[FieldEntry => Boolean],
                         definedBy: Dialect): (AmfObject, Seq[AmfObject]) = {
      val tuples  = SonFinder(location, definedBy, amfPosition).buildStack(obj)
      val objects = tuples.map(_._1)
//        .dropWhile(o => !o.annotations.nonEmpty) // any field without annotations should be ignored as it is clearly incomplete
      (objects.head, objects.tail)
    }
  }

  private def isDeclares(fe: FieldEntry) =
    fe.field == DocumentModel.Declares

  private def explicitArray(entry: FieldEntry, parent: AmfObject, definedBy: Dialect) =
    entry.astValueArray() && isExplicitArray(entry, parent, definedBy) || !entry.astValueArray()

  private def isExplicitArray(entry: FieldEntry, parent: AmfObject, definedBy: Dialect) =
    definedBy
      .findNodeMappingByTerm(parent.meta.`type`.head.iri())
      .flatMap { nm =>
        nm.findPropertyByTerm(entry.field.value.iri())
      }
      .exists(p => p.allowMultiple().value())

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
      case binding
          if binding == MessageBindingModel || binding == ChannelBindingModel || binding == ServerBindingModel || binding == OperationBindingModel =>
        Some(EmptyBindingModel.modelInstance)
      case m: ModelDefaultBuilder =>
        val instance = m.modelInstance
        instance.add(e.annotations) // new instance has no annotation, so it inherits it's parents
        Some(instance)
      case _ => None
    }
}
