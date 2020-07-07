package org.mulesoft.als.common

import amf.core.model.document.BaseUnit
import amf.core.model.domain.{AmfArray, AmfObject, DomainElement}
import amf.core.parser.{Position => AmfPosition}
import AmfSonElementFinder._
import amf.core.annotations.{LexicalInformation, SynthesizedField}
import amf.core.metamodel.document.BaseUnitModel
import amf.core.parser.FieldEntry
import org.mulesoft.amfintegration.FieldEntryOrdering
import org.mulesoft.amfintegration.AmfImplicits._

case class ObjectInTree(obj: AmfObject, stack: Seq[AmfObject], amfPosition: AmfPosition) {

  /**
    * return the first field entry for that contains the position in his value.
    *
    * key: value
    * only compares against " value" range using amfelement.position
    */
  lazy val fieldValue: Option[FieldEntry] = ObjectInTree.getFieldEntry(this, amfPosition, FieldEntryOrdering)

  /**
    * return the first field entry for that contains the position in his entry(key or value).
    * key: value
    * compares against "key: value" range (all line) using field.value.position
    */
  lazy val fieldEntry: Option[FieldEntry] = obj.fields
    .fields()
    .filter(
      f =>
        f.value.annotations
          .lexicalInformation()
          .exists(_.containsCompletely(amfPosition)) && !f.value.value.annotations
          .contains(classOf[SynthesizedField]))
    .toList
    .sorted(FieldEntryOrdering)
    .lastOption
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
          case arr: AmfArray =>
            f.value.annotations
              .lexicalInformation()
              .exists(_.containsCompletely(position)) &&
              arr.annotations
                .lexicalInformation()
                .forall(_.arrayContainsPosition(position))
          case v =>
            v.position()
              .exists(_.containsAtField(position)) && (f.value.annotations
              .lexicalInformation()
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
