package org.mulesoft.als.common

import amf.core.annotations.{LexicalInformation, SourceAST}
import amf.core.metamodel.document.BaseUnitModel
import amf.core.model.document.BaseUnit
import amf.core.model.domain.{AmfObject, DomainElement}
import amf.core.parser.{Annotations, FieldEntry, Position => AmfPosition}
import org.mulesoft.als.common.AmfSonElementFinder._
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.amfintegration.AmfImplicits._
import org.mulesoft.amfintegration.FieldEntryOrdering
import org.yaml.model.{YMapEntry, YNode}
import org.yaml.model.YMapEntry
import org.mulesoft.amfintegration.AmfImplicits._

case class ObjectInTree(obj: AmfObject, stack: Seq[AmfObject], amfPosition: AmfPosition) {

  /**
    * return the first field entry for that contains the position in his value.
    *
    * key: value
    * only compares against " value" range using amfelement.position
    */
  lazy val fieldEntry: Option[FieldEntry] = getFieldEntry(FieldEntryOrdering)

  /**
    * return the first field entry for that contains the position in his entry(key or value).
    * key: value
    * compares against "key: value" range (all line) using field.value.position
    */
  private def getFieldEntry(ordering: Ordering[FieldEntry]): Option[FieldEntry] =
    // todo: maybe this should be a seq and not an option
    obj.fields
      .fields()
      .filter(f => inField(f) && (inValue(f) || notInKey(f.value.annotations)))
      .toList
      .sorted(ordering)
      .lastOption
      .orElse(singleLineNull())

  private def singleLineNull() = {
    obj.fields.fields().find { fe =>
      fe.value.annotations.ast() match {
        case Some(node: YNode) if node.isNull =>
          node.value.range.lineFrom == node.value.range.lineTo && node.value.range.lineTo == amfPosition.line
        case _ => false
      }
    }
  }
  private def inField(f: FieldEntry) =
    f.value.annotations.lexicalInfo.forall(_.contains(amfPosition))

  private def inValue(f: FieldEntry) = f.value.value.position().exists(_.contains(amfPosition))

  private def notInKey(a: Annotations) = {
    a.find(classOf[SourceAST]) match {
      case Some(SourceAST(e: YMapEntry)) => notInKeyAtEntry(e)
      case _                             => false
    }
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
}

object ObjectInTreeBuilder {

  def fromUnit(bu: BaseUnit, position: AmfPosition, location: Option[String]): ObjectInTree = {
    val (obj, stack) =
      bu.findSonWithStack(position, location, Seq((f: FieldEntry) => f.field != BaseUnitModel.References))
    ObjectInTree(obj, stack, position)
  }

  def fromSubTree(element: DomainElement,
                  position: AmfPosition,
                  location: Option[String],
                  previousStack: Seq[AmfObject]): ObjectInTree = {
    val (obj, stack) = element.findSonWithStack(position, location, Seq.empty)
    ObjectInTree(obj, stack ++ previousStack, position)
  }
}
