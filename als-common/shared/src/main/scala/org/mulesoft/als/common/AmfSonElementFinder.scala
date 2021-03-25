package org.mulesoft.als.common

import amf.core.annotations.SourceAST
import amf.core.metamodel.ModelDefaultBuilder
import amf.core.metamodel.Type.ArrayLike
import amf.core.metamodel.document.{BaseUnitModel, DocumentModel}
import amf.core.metamodel.domain.{DataNodeModel, DomainElementModel, ShapeModel}
import amf.core.model.domain.{AmfArray, AmfObject, DataNode}
import amf.core.parser.{FieldEntry, Position => AmfPosition}
import amf.plugins.document.vocabularies.metamodel.domain.DialectDomainElementModel
import amf.plugins.document.vocabularies.model.document.Dialect
import amf.plugins.document.vocabularies.model.domain.DialectDomainElement
import amf.plugins.document.webapi.annotations.DeclarationKeys
import amf.plugins.domain.webapi.metamodel.bindings._
import jdk.internal.org.objectweb.asm.tree.FieldInsnNode
import org.mulesoft.als.common.YamlWrapper._
import org.mulesoft.amfintegration.AmfImplicits.{AmfAnnotationsImp, _}

import scala.collection.mutable.ListBuffer

object AmfSonElementFinder {

  implicit class AlsAmfObject(obj: AmfObject) {

    def findSon(amfPosition: AmfPosition, location:String, definedBy:Dialect ): SonFinder#Branch = {
      SonFinder(location, definedBy, amfPosition).find(obj)
    }
    case class SonFinder(location: String, definedBy: Dialect, amfPosition: AmfPosition) {

      val fieldAstFilter: FieldEntry => Boolean = (f: FieldEntry) =>
        f.value.annotations
          .containsAstPosition(amfPosition)
          .getOrElse(
            f.value.annotations.isInferred || f.value.annotations.isVirtual || isDeclares(
              f))
      // why do we assume that inferred/virtual/declared would have the position? should we not look inside? what if there is more than one such case?


      val fieldFilters: Seq[FieldEntry => Boolean] = Seq(
        (f: FieldEntry) => f.field != BaseUnitModel.References,
        fieldAstFilter
      )

      def find(obj: AmfObject): Branch = {
        val entryPoint = Branch(obj, Nil,None)
        find(entryPoint) match {
          case Nil => entryPoint
          case head :: Nil =>
            head
          case list =>
            val l = list
            list.reduce((a, b) => {
              if(a.branch.contains(b.obj)) a
              else if (b.branch.contains(a.obj)) b
              else{
                // check also head in case that is a builded in array element
                a.obj.range.orElse(a.branch.headOption.flatMap(_.range)) match {
                  case Some(r) if r.contains(b.obj.range.orElse(b.branch.headOption.flatMap(_.range)).getOrElse(amf.core.parser.Range.NONE)) => b
                  case _ => a
                }
              }
            })
        }
      }

      def find(branch: Branch): Seq[Branch] = {
        val children: Seq[Either[AmfObject, FieldEntry]] = filterFields(branch.obj).map(fe => nextObject(fe, obj).map(Left(_)).getOrElse(Right(fe)))
        if(children.isEmpty) Seq(branch)
        else
          children.flatMap { either => either match {
            case Left(obj) =>
              if (branch.branch.contains(obj)) Some(branch)
              else find(branch.newLeaf(obj))
            case Right(fe) => Some(branch.forField(fe))
          }

          }
      }

      // only object can have sons. Scalar and arrays are field from objects.
      case class Branch(obj: AmfObject, branch: Seq[AmfObject], fe:Option[FieldEntry]) {
        def newLeaf(leaf: AmfObject): Branch = copy(leaf, obj +: branch)

        def forField(fe:FieldEntry): Branch = copy(fe = Some(fe))
      }

      def nextObject(fe: FieldEntry, parent: AmfObject): Option[AmfObject] =
        if (fe.objectSon && fe.value.value.location().forall(l => l.isEmpty || l == location))
          fe.value.value match {
            case e: AmfArray =>
              nextObject(e).orElse(buildFromMeta(parent, fe, e))
            case o: AmfObject if o.containsPosition(amfPosition) =>
              Some(o)
            case _ =>
              None
          } else None

      def buildFromMeta(parent: AmfObject, fe: FieldEntry, arr: AmfArray): Option[AmfObject] =
        if (explicitArray(fe, parent, definedBy)) matchInnerArrayElement(fe, arr, definedBy, parent)
        else None

      def nextObject(array: AmfArray): Option[AmfObject] =
        if (isInArray(array)) {
          val objects = array.values.collect({ case o: AmfObject => o })
          objects
            .find(_.annotations.containsPosition(amfPosition))
            .orElse(objects.find(v => v.annotations.isVirtual || v.annotations.isSynthesized))
        } else None

      private def isInArray(array: AmfArray): Boolean =
        array.annotations
          .find(classOf[SourceAST])
          .map(_.ast)
          .forall { s =>
            s.contains(amfPosition)
          }

      private def declaredPosition(fe: FieldEntry): Boolean =
        isDeclares(fe) &&
          fe.value.annotations.find(classOf[DeclarationKeys]).exists { dk =>
            dk.keys.exists(k => k.entry.contains(amfPosition))
          }

      def filterFields(amfObject: AmfObject): Seq[FieldEntry] =
        amfObject.fields.fields().filter(f => fieldFilters.forall(fn => fn(f))).toSeq

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
