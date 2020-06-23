package org.mulesoft.als.common

import amf.core.model.document.BaseUnit
import amf.core.model.domain.{AmfArray, AmfObject, DomainElement}
import amf.core.parser.{Position => AmfPosition}
import AmfSonElementFinder._
import amf.core.annotations.{LexicalInformation, SynthesizedField}
import amf.core.metamodel.document.BaseUnitModel
import amf.core.parser.FieldEntry
import org.mulesoft.amfintegration.FieldEntryOrdering

case class ObjectInTree(obj: AmfObject, stack: Seq[AmfObject], amfPosition: AmfPosition) {
  lazy val fieldEntry: Option[FieldEntry] = ObjectInTree.getFieldEntry(this, amfPosition, FieldEntryOrdering)
}

object ObjectInTree {
  def getFieldEntry(objectInTree: ObjectInTree,
                    position: AmfPosition,
                    fieldEntryOrdering: Ordering[FieldEntry]): Option[FieldEntry] =
    // todo: maybe this should be a seq and not an option
    objectInTree.obj.fields
      .fields()
      .filter(f =>
        f.value.value match {
          case _: AmfArray =>
            f.value.annotations
              .find(classOf[LexicalInformation])
              .exists(_.containsCompletely(position))
          case v =>
            v.position()
              .exists(_.contains(position)) && (f.value.annotations
              .find(classOf[LexicalInformation])
              .forall(_.containsCompletely(position)) && !f.value.value.annotations
              .contains(classOf[SynthesizedField]))
      })
      .toList
      .sorted(fieldEntryOrdering)
      .lastOption
}

object ObjectInTreeBuilder {

  def fromUnit(bu: BaseUnit, position: AmfPosition): ObjectInTree = {
    val (obj, stack) = bu.findSonWithStack(position, Seq((f: FieldEntry) => f.field != BaseUnitModel.References))
    ObjectInTree(obj, stack, position)
  }

  def fromSubTree(element: DomainElement, position: AmfPosition, previousStack: Seq[AmfObject]): ObjectInTree = {
    val (obj, stack) = element.findSonWithStack(position, Seq.empty)
    ObjectInTree(obj, stack ++ previousStack, position)
  }
}
