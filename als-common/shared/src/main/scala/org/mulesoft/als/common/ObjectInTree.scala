package org.mulesoft.als.common

import amf.core.annotations.{DeclaredElement, DefinedByVendor, SourceAST}
import amf.core.metamodel.domain.LinkableElementModel
import amf.core.model.document.{BaseUnit, DeclaresModel}
import amf.core.model.domain.{AmfObject, DomainElement}
import amf.core.parser.{Annotations, FieldEntry, Position => AmfPosition}
import amf.core.remote.Vendor
import amf.plugins.document.vocabularies.model.document.Dialect
import org.mulesoft.als.common.AmfSonElementFinder._
import org.mulesoft.als.common.YamlWrapper.AlsYPart
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.amfintegration.AmfImplicits._
import org.mulesoft.amfintegration.FieldEntryOrdering
import org.yaml.model.YMapEntry

case class ObjectInTree(obj: AmfObject,
                        stack: Seq[AmfObject],
                        amfPosition: AmfPosition,
                        fieldEntry: Option[FieldEntry]) {

  def objVendor: Option[Vendor] =
    (obj +: stack).flatMap(_.annotations.find(classOf[DefinedByVendor])).headOption.map(_.vendor)

  /**
    * return the first field entry for that contains the position in his value.
    *
    * key: value
    * only compares against " value" range using amfelement.position
    */
  lazy val fieldValue: Option[FieldEntry] = getFieldEntry(justValueFn, FieldEntryOrdering, obj)

  lazy val nonVirtualObj: Option[AmfObject] = obj.annotations.ast() match {
    case Some(_) => Some(obj)
    case None    => stack.headOption
  }

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
          e.contains(amfPosition) && !(e.key.range.lineTo == amfPosition.line && e.key.range.columnFrom == amfPosition.column) // start of the entry
        case _ => f.value.annotations.containsAstPosition(amfPosition).getOrElse(f.value.annotations.isInferred)
      })

  private def inValue(f: FieldEntry) =
    f.value.value.annotations.ast().exists(_.contains(amfPosition))

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
      .contains(Position(amfPosition)) && (e.range.columnTo > e.range.columnFrom || e.range.columnTo == 0) && e.value.isNull

  def isDeclared(): Boolean = {
    obj.annotations.contains(classOf[DeclaredElement]) ||
    stack.headOption.exists({
      case d: DeclaresModel => d.declares.contains(obj)
      case _                => false
    })
  }
}

object ObjectInTreeBuilder {

  def fromUnit(bu: BaseUnit, position: AmfPosition, location: String, definedBy: Dialect): ObjectInTree = {
    val branch =
      bu.findSon(position, location, definedBy)
    ObjectInTree(branch.obj, branch.branch, position, branch.fe)
  }

  def fromSubTree(element: DomainElement,
                  position: AmfPosition,
                  location: String,
                  previousStack: Seq[AmfObject],
                  definedBy: Dialect): ObjectInTree = {
    val branch = element.findSon(position, location, definedBy)
    ObjectInTree(branch.obj, branch.branch ++ previousStack, position, branch.fe)
  }
}
