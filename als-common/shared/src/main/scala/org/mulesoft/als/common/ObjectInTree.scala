package org.mulesoft.als.common

import amf.aml.client.scala.model.document.Dialect
import amf.aml.client.scala.model.domain.SemanticExtension
import amf.core.client.scala.model.document.{BaseUnit, DeclaresModel}
import amf.core.client.scala.model.domain.extensions.DomainExtension
import amf.core.client.scala.model.domain.{AmfObject, DomainElement}
import amf.core.internal.annotations.{DeclaredElement, DefinedBySpec, SourceAST}
import amf.core.internal.metamodel.domain.LinkableElementModel
import amf.core.internal.parser.domain.{Annotations, FieldEntry}
import amf.core.internal.remote.{AmlDialectSpec, Spec}
import org.mulesoft.als.common.AmfSonElementFinder.AlsAmfObject
import org.mulesoft.als.common.YamlWrapper.AlsYPart
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.amfintegration.AmfImplicits.AmfAnnotationsImp
import org.mulesoft.amfintegration.FieldEntryOrdering
import org.yaml.model.YMapEntry

case class ObjectInTree(obj: AmfObject,
                        stack: Seq[AmfObject],
                        fieldEntry: Option[FieldEntry],
                        yPartBranch: YPartBranch) {
  def objSpec(findDialectForSemantic: String => Option[(SemanticExtension, Dialect)]): Option[Spec] = {
    def findSpecificDefinition(objects: Seq[AmfObject]): Option[Spec] =
      objects.flatMap {
        case de: DomainExtension if de.name.nonEmpty =>
          findDialectForSemantic(de.name.value()).map(t => AmlDialectSpec(t._2.id))
        case o => o.annotations.find(classOf[DefinedBySpec]).map(_.spec)
      }.headOption

    findSpecificDefinition(obj +: stack)
  }

  /**
    * return the first field entry for that contains the position in his value.
    *
    * key: value
    * only compares against " value" range using amfelement.position
    */
  lazy val fieldValue: Option[FieldEntry] = getFieldEntry(justValueFn, FieldEntryOrdering, obj)

  lazy val nonVirtualObj: Option[AmfObject] =
    obj.annotations
      .containsYPart(yPartBranch)
      .map(_ => obj)
      .orElse(stack.headOption)

  private val justValueFn = (f: FieldEntry) => inField(f) && (inValue(f) || notInKey(f.value.annotations))

  /**
    * return the first field entry for that contains the position in his entry(key or value).
    * key: value
    * compares against "key: value" range (all line) using field.value.position
    */
  private def getFieldEntry(filterFn: FieldEntry => Boolean,
                            ordering: Ordering[FieldEntry],
                            o: AmfObject): Option[FieldEntry] = {
    // todo: maybe this should be a seq and not an option
    val fields   = o.fields.fields()
    val filtered = fields.filter(filterFn).toList
    filtered
      .sorted(ordering)
      .lastOption
  }

  private def inField(f: FieldEntry) =
    f.field != LinkableElementModel.Target &&
      (f.value.annotations.ast() match {
        case Some(e: YMapEntry) =>
          e.contains(yPartBranch.position) && !(e.key.range.lineTo == yPartBranch.position.line && e.key.range.columnFrom == yPartBranch.position.column) // start of the entry
        case _ => f.value.annotations.containsYPart(yPartBranch).getOrElse(f.value.annotations.isInferred)
      })

  private def inValue(f: FieldEntry) =
    f.value.value.annotations.ast().exists(_.contains(yPartBranch.position))

  private def notInKey(a: Annotations) =
    a.find(classOf[SourceAST]) match {
      case Some(SourceAST(e: YMapEntry)) => notInKeyAtEntry(e)
      case _                             => false
    }

  /**
    *   hack for new empty line. Is a new field.
    *   This is part of the value:
    *      e:
    *        *
    *   this should not
    *     e: value
    *     *
    */
  private def notInKeyAtEntry(e: YMapEntry) =
    !PositionRange(e.key.range)
      .contains(Position(yPartBranch.position)) && (e.range.columnTo > e.range.columnFrom || e.range.columnTo == 0) && e.value.isNull

  def isDeclared: Boolean = {
    obj.annotations.contains(classOf[DeclaredElement]) ||
    stack.headOption.exists({
      case d: DeclaresModel => d.declares.contains(obj)
      case _                => false
    })
  }
}

object ObjectInTreeBuilder {

  def fromUnit(bu: BaseUnit, location: String, definedBy: Dialect, yPartBranch: YPartBranch): ObjectInTree = {
    val branch =
      bu.findSon(location, definedBy, yPartBranch)
    ObjectInTree(branch.obj, branch.branch, branch.fe, yPartBranch)
  }

  def fromSubTree(element: DomainElement,
                  location: String,
                  previousStack: Seq[AmfObject],
                  definedBy: Dialect,
                  yPartBranch: YPartBranch): ObjectInTree = {
    val branch = element.findSon(location, definedBy, yPartBranch)
    ObjectInTree(branch.obj, branch.branch ++ previousStack, branch.fe, yPartBranch)
  }
}
