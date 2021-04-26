package org.mulesoft.als.common

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
import amf.plugins.domain.shapes.metamodel.AnyShapeModel
import amf.plugins.domain.webapi.metamodel.bindings._
import org.mulesoft.als.common.YamlWrapper._
import org.mulesoft.amfintegration.AmfImplicits.{AmfAnnotationsImp, _}

import scala.language.implicitConversions

object AmfSonElementFinder {

  implicit class AlsAmfObject(obj: AmfObject) {

    def findSon(amfPosition: AmfPosition, location: String, definedBy: Dialect): SonFinder#Branch =
      SonFinder(location, definedBy, amfPosition).find(obj)

    case class SonFinder(location: String, definedBy: Dialect, amfPosition: AmfPosition) {

      // TODO: new traverse check deletion
      private def fieldContainsInner(f: FieldEntry): Boolean =
        elementContainsInner(f.value.value)

      private def elementContainsInner(amfElement: AmfElement): Boolean = {
        implicit def defaultFalse(predicate: Option[Boolean]): Boolean = predicate.getOrElse(false)

        amfElement match {
          case AmfArray(values, annotations) =>
            annotations.containsAstPosition(amfPosition) ||
              values.exists(elementContainsInner)
          case amfObject: AmfObject =>
            amfObject.annotations.containsAstPosition(amfPosition) ||
              amfObject.fields.fields().exists(fieldContainsInner)
          case AmfScalar(_, annotations) => annotations.containsAstPosition(amfPosition)
          case _                         => false
        }
      }

      private val fieldAstFilter: FieldEntry => Boolean = (f: FieldEntry) =>
        f.value.annotations
          .containsAstPosition(amfPosition)
          .getOrElse(
            ((f.value.annotations.isInferred ||
              f.value.annotations.isVirtual)) ||
//              fieldContainsInner(f)) || // todo: try to optimize this recursive search
              isDeclares(f))
      // why do we assume that inferred/virtual/declared would have the position? should we not look inside? what if there is more than one such case?

      private val fieldFilters: Seq[FieldEntry => Boolean] = Seq(
        (f: FieldEntry) => f.field != BaseUnitModel.References,
        fieldAstFilter
      )

      private def rangeFor(a: Branch): Option[parser.Range] =
        a.obj.range.orElse(a.branch.headOption.flatMap(_.range))

      private def appliesReduction(fe: FieldEntry) =
        (!fe.value.annotations.isInferred) || fe.value.value.annotations.containsPosition(amfPosition)
      def find(obj: AmfObject): Branch = {
        val entryPoint = Branch(obj, Nil, None)
        find(entryPoint) match {
          case Nil => entryPoint
          case head :: Nil =>
            head
          case list => pickOne(filterCandidates(list))
        }
      }

      private def filterCandidates(list: Seq[Branch]) = {
        list.map(br => {
          if ((br.fe.isEmpty && (br.obj.annotations.isVirtual && br.obj.range.isEmpty)) || (br.fe.nonEmpty && br.fe
                .exists(f => !appliesReduction(f))))
            br.unstacked()
          else br
        })
      }

      private def pickOne(list: Seq[Branch]) = {
        list.reduce((a, b) => {
          if (b.fe.nonEmpty && appliesReduction(b.fe.get)) b
          else if (a.fe.nonEmpty && appliesReduction(a.fe.get)) a
          else if (a.branch.contains(b.obj) && !a.obj.annotations.isVirtual) a
          else if (b.branch.contains(a.obj) && !b.obj.annotations.isVirtual) b
          else if (rangeFor(b).isEmpty) a
          else if (rangeFor(a).isEmpty) b
          else {
            // check also head in case that is a built in array element
            (rangeFor(a), rangeFor(b)) match {
              case (Some(ra), Some(rb)) if ra.contains(rb) => b // most specific
              case (Some(_), Some(_)) =>
                if (!a.obj.containsPosition(amfPosition) && b.obj.containsPosition(amfPosition)) b else a // most specific
              //                  case (Some(_), None) => a (same as default)
              case (None, Some(_)) => b
              case _               => a
            }
          }
        })

      }
      private def find(branch: Branch): Seq[Branch] = {
        val children: Seq[Either[AmfObject, FieldEntry]] =
          filterFields(branch.obj).flatMap(fe => {
            val seq = nextObject(fe, branch.obj).map(Left(_))
            if (seq.nonEmpty) seq
            else Seq(Right(fe))
          })
        if (children.isEmpty) Seq(branch)
        else
          children.flatMap {
            case Left(obj) =>
              if (branch.branch.contains(obj)) Some(branch)
              else find(branch.newLeaf(obj))
            case Right(fe)
                if !fe.value.annotations.isInferred || fe.value.value.annotations.containsPosition(amfPosition) =>
              Some(branch.forField(fe))
            case _ => Some(branch)
          }
      }

      // only object can have sons. Scalar and arrays are field from objects.
      case class Branch(obj: AmfObject, branch: Seq[AmfObject], fe: Option[FieldEntry]) {
        def unstacked(): Branch = {
          if (fe.isDefined) copy(fe = None)
          else if (branch.nonEmpty) copy(obj = branch.head, branch = branch.tail)
          else this
        }

        def newLeaf(leaf: AmfObject): Branch = copy(leaf, obj +: branch)

        def forField(fe: FieldEntry): Branch = copy(fe = Some(fe))
      }

      private def nextObject(fe: FieldEntry, parent: AmfObject): Seq[AmfObject] =
        if (fe.objectSon && fe.value.value.location().forall(l => l.isEmpty || l == location))
          fe.value.value match {
            case e: AmfArray =>
              val objects = nextObject(e)
              if (objects.isEmpty) buildFromMeta(parent, fe, e).toSeq
              else objects
            case o: AmfObject if o.containsPosition(amfPosition) || o.annotations.isVirtual =>
              Seq(o)
            case _ =>
              Seq.empty
          } else Seq.empty

      def buildFromMeta(parent: AmfObject, fe: FieldEntry, arr: AmfArray): Option[AmfObject] =
        if (explicitArray(fe, parent, definedBy)) matchInnerArrayElement(fe, arr, definedBy, parent)
        else None

      def nextObject(array: AmfArray): Seq[AmfObject] =
        if (isInArray(array)) {
          val objects = array.values.collect({ case o: AmfObject => o })
          val candidates = objects
            .filter(_.annotations.containsPosition(amfPosition))
          if (candidates.isEmpty) objects.filter(v => v.annotations.isVirtual || v.annotations.isSynthesized)
          else candidates
        } else Seq.empty

      private def isInArray(array: AmfArray): Boolean =
        array.annotations
          .find(classOf[SourceAST])
          .map(_.ast)
          .forall { s =>
            s.contains(amfPosition)
          }
//
//      private def declaredPosition(fe: FieldEntry): Boolean =
//        isDeclares(fe) &&
//          fe.value.annotations.find(classOf[DeclarationKeys]).exists { dk =>
//            dk.keys.exists(k => k.entry.contains(amfPosition))
//          }

      def filterFields(amfObject: AmfObject): Seq[FieldEntry] =
        amfObject.fields.fields().filter(f => fieldFilters.forall(fn => fn(f))).toSeq

      private def explicitArray(entry: FieldEntry, parent: AmfObject, definedBy: Dialect) =
        (entry.astValueArray() && isExplicitArray(entry, parent, definedBy) || !entry
          .astValueArray()) && amfPosition.column > 0

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
            e.values.collectFirst({ case o: AmfObject => o }) match {
              case Some(_: DialectDomainElement) if isDeclares(entry) => None
              case other                                              => other
            }
          case s: ShapeModel if s.`type`.headOption.exists(_.iri() == ShapeModel.`type`.head.iri()) =>
            Some(AnyShapeModel.modelInstance)
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
  }

  private def isDeclares(fe: FieldEntry) =
    fe.field == DocumentModel.Declares
}
