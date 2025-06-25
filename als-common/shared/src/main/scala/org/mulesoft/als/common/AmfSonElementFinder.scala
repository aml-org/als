package org.mulesoft.als.common

import amf.aml.client.scala.model.domain.DialectDomainElement
import amf.aml.internal.metamodel.domain.DialectDomainElementModel
import amf.apicontract.client.scala.model.domain.Payload
import amf.apicontract.internal.metamodel.domain.bindings._
import amf.core.client.scala.model.domain.{AmfArray, AmfObject, DataNode}
import amf.core.internal.metamodel.ModelDefaultBuilder
import amf.core.internal.metamodel.Type.ArrayLike
import amf.core.internal.metamodel.document.{BaseUnitModel, DocumentModel}
import amf.core.internal.metamodel.domain.{DataNodeModel, DomainElementModel, ShapeModel}
import amf.core.internal.parser.domain.FieldEntry
import amf.shapes.internal.domain.metamodel.AnyShapeModel
import org.mulesoft.als.common.ASTElementWrapper._
import org.mulesoft.als.common.AlsAmfElement._
import org.mulesoft.amfintegration.AmfImplicits.{AmfAnnotationsImp, AmfObjectImp, DialectImplicits, FieldEntryImplicit, NodeMappingImplicit}
import org.mulesoft.amfintegration.amfconfiguration.DocumentDefinition
import org.mulesoft.common.client.lexical.PositionRange
import org.yaml.model.{YMap, YMapEntry}

import scala.language.implicitConversions

object AmfSonElementFinder {

  implicit class AlsAmfObject(obj: AmfObject) {

    def findSon(location: String, documentDefinition: DocumentDefinition, astBranch: ASTPartBranch): SonFinder#Branch =
      SonFinder(location, documentDefinition, astBranch: ASTPartBranch).find(obj, documentDefinition)

    case class SonFinder(location: String, documentDefinition: DocumentDefinition, astBranch: ASTPartBranch) {

      private val fieldAstFilter: FieldEntry => Boolean = (f: FieldEntry) =>
        f.value.annotations
          .containsAstBranch(astBranch)
          .getOrElse(
            f.value.annotations.isInferred ||
              f.value.annotations.isVirtual ||
              isDeclares(f)
          )
      // why do we assume that inferred/virtual/declared would have the position? should we not look inside? what if there is more than one such case?

      private val fieldFilters: Seq[FieldEntry => Boolean] = Seq(
        (f: FieldEntry) => f.field != BaseUnitModel.References,
        fieldAstFilter
      )

      private def rangeFor(a: Branch): Option[PositionRange] =
        a.obj.range.orElse(a.branch.headOption.flatMap(_.range))

      private def appliesReduction(fe: FieldEntry) =
        (!fe.value.annotations.isInferred) || fe.value.value.annotations.containsAstBranch(astBranch).getOrElse(false)

      def find(obj: AmfObject, documentDefinition: DocumentDefinition): Branch = {
        val entryPoint = Branch(obj, Nil, None)
        find(entryPoint, documentDefinition) match {
          case Nil => entryPoint
          case list =>
            pickOne(filterCandidates(list))
        }
      }

      private def filterCandidates(list: Seq[Branch]) =
        list.map(br => {
          if (
            (br.fe.isEmpty && ((br.obj.annotations.isInferred || br.obj.annotations.isSynthesized || br.obj.annotations.isVirtual || br.fe
              .exists(isDeclares)) && br.obj.range.isEmpty)) || (br.fe.nonEmpty && br.fe
              .exists(f => !appliesReduction(f)))
          )
            br.unstacked()
          else br
        })

      private def pickOne(list: Seq[Branch]) =
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
              case (Some(ra), Some(rb)) if rb.contains(ra) => a // most specific
              case (Some(_), Some(_)) =>
                if (!a.obj.containsYPart(astBranch) && b.obj.containsYPart(astBranch)) b else a // most specific
              //                  case (Some(_), None) => a (same as default)
              case (None, Some(_)) => b
              case _               => a
            }
          }
        })

      private def find(branch: Branch, documentDefinition: DocumentDefinition): Seq[Branch] = {
        val children: Seq[Either[AmfObject, FieldEntry]] =
          filterFields(branch.obj).flatMap(fe => {
            val seq = nextObject(fe, branch.obj, documentDefinition).map(Left(_))
            if (seq.nonEmpty) seq
            else Seq(Right(fe))
          })
        if (children.isEmpty) Seq(branch)
        else
          children.flatMap {
            case Left(obj) =>
              if (branch.branch.contains(obj)) Some(branch)
              else find(branch.newLeaf(obj), documentDefinition)
            case Right(fe)
                // todo: check this clause, it is a weird predicate
                if !fe.value.annotations.isInferred || fe.value.value.containsYPart(astBranch) =>
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

      private def nextObject(fe: FieldEntry, parent: AmfObject, documentDefinition: DocumentDefinition): Seq[AmfObject] =
        if (fe.objectSon && fe.value.value.location().forall(l => l.isEmpty || l == location))
          fe.value.value match {
            case e: AmfArray =>
              // todo: here "if e.containsYPart(yPartBranch)" is not possible because some tests depend on this matching incorrect nodes
              val objects = nextObject(e, documentDefinition)
              if (objects.isEmpty) buildFromMeta(parent, fe, e).toSeq
              else objects
            case o: AmfObject if o.containsYPart(astBranch) || o.annotations.isVirtual =>
              Seq(o)
            case _ =>
              Seq.empty
          }
        else Seq.empty

      def buildFromMeta(parent: AmfObject, fe: FieldEntry, arr: AmfArray): Option[AmfObject] =
        if (explicitArray(fe, parent, documentDefinition)) matchInnerArrayElement(fe, arr, documentDefinition, parent)
        else None

      /** @param amfObject
        * @param documentDefinition
        * @return
        *   true if this object should be filtered OUT
        */
      private def exceptionCase(amfObject: AmfObject, documentDefinition: DocumentDefinition): Boolean =
        exceptionList.exists(_(amfObject, documentDefinition))

      private val exceptionList: Seq[(AmfObject, DocumentDefinition) => Boolean] = Seq(
        exceptionAsyncPayload
      )

      /** TODO: Remove and fix annotation of Async Payload in AMF
        */
      private def exceptionAsyncPayload(amfObject: AmfObject, documentDefinition: DocumentDefinition): Boolean = amfObject match {
        case p: Payload if documentDefinition.nameAndVersion() == "asyncapi 2.0.0" =>
          val correctAstContains = p.annotations
            .astElement()
            .flatMap {
              case m: YMap => m.entries.find(_.key.asScalar.exists(_.text == "payload"))
              case _       => None
            }
            .exists(_.contains(astBranch.position, strict = false))
          val childContains =
            p.fields
              .fields()
              .flatMap(_.element.annotations.astElement())
              .exists(_.contains(astBranch.position, strict = false))
          !(correctAstContains || childContains) // if any other node matches, return true. If the entry with `payload` as  key matches, return false
        case _ => false
      }

      def nextObject(array: AmfArray, documentDefinition: DocumentDefinition): Seq[AmfObject] =
//        if (array.containsYPart(yPartBranch)) {
        // todo: this array comparison is rancid, we should see why the containsYPart is not sufficient
        //  (why we expect a virtual/synthetized to always match even if no element inside does)
        if (isInArray(array)) {
          val objects = array.values.collect({ case o: AmfObject => o })
          val candidates = objects
            .filter(_.containsYPart(astBranch))
            .filterNot(exceptionCase(_, documentDefinition))
          if (candidates.isEmpty)
            objects.filter(v =>
              (v.annotations.isVirtual && v.annotations
                .baseVirtualNode()
                .isEmpty) || // if there is a baseVirtualNode and it matches, it would have matched with `containsYPart`
                v.annotations.isSynthesized
            )
          else candidates
        } else Seq.empty

      private def isInArray(array: AmfArray): Boolean =
        array.annotations
          .astElement()
          .forall { s =>
            // todo: should this contemplate `containsYPart`? if so, check also  what to do withVirtual/Inferred
            s.contains(astBranch.position, strict = false)
          }

      def filterFields(amfObject: AmfObject): Seq[FieldEntry] =
        amfObject.fields.fields().filter(f => fieldFilters.forall(fn => fn(f))).toSeq

      private def explicitArray(entry: FieldEntry, parent: AmfObject, documentDefinition: DocumentDefinition) =
        (entry.astValueArray() && isExplicitArray(entry, parent, documentDefinition) || !entry
          .astValueArray()) && astBranch.position.column > 0 // TODO: Check why this hack (pos > 0) is necessary

      private def isExplicitArray(entry: FieldEntry, parent: AmfObject, documentDefinition: DocumentDefinition) =
        documentDefinition
          .findNodeMappingByTerm(parent.meta.`type`.head.iri())
          .flatMap { nm =>
            nm.findPropertyByTerm(entry.field.value.iri())
          }
          .exists(p => p.allowMultiple().value())

      private def matchInnerArrayElement(entry: FieldEntry, e: AmfArray, documentDefinition: DocumentDefinition, parent: AmfObject) =
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
              .flatMap(documentDefinition.findNodeMapping)
              .map { nodeMapping =>
                DialectDomainElement()
                  .withInstanceTypes(nodeMapping.nodetypeMapping.value() +: d.`type`.map(_.iri()))
                  .withDefinedBy(nodeMapping)
              }
          case d: DomainElementModel if d.`type`.headOption.exists(_.iri() == DataNodeModel.`type`.head.iri()) =>
            e.values.collectFirst({ case d: DataNode => d })
          case d: DomainElementModel if d.`type`.headOption.exists(_.iri() == DomainElementModel.`type`.head.iri()) =>
            e.values.collectFirst({ case o: AmfObject => o }) match {
              case Some(_: DialectDomainElement) if isDeclares(entry)  => None
              case Some(_) if isDeclares(entry) && isInDeclarationName => None
              case Some(other) if other.containsYPart(astBranch)       => Some(other)
              case _                                                   => None
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

      private lazy val isInDeclarationName: Boolean =
        obj.fields
          .fields()
          .find(_.field == DocumentModel.Declares)
          .exists(_.value.annotations.declarationKeys().map(_.entry).exists {
            case entry: YMapEntry =>
              entry.value.range.contains(astBranch.position) && astBranch.position.column > entry.range.columnFrom
            case _ => false
          })
    }
  }

  private def isDeclares(fe: FieldEntry) =
    fe.field == DocumentModel.Declares
}
