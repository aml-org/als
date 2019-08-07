package org.mulesoft.als.common

import amf.core.model.document.BaseUnit
import amf.core.model.domain.{AmfObject, DomainElement}
import org.mulesoft.als.common.dtoTypes.Position
import AmfSonElementFinder._
import amf.core.metamodel.document.BaseUnitModel
import amf.core.parser.FieldEntry

case class ObjectInTree(obj: AmfObject, stack: Seq[AmfObject])

object ObjectInTreeBuilder {

  def fromUnit(bu: BaseUnit, position: Position): ObjectInTree = {
    val (obj, stack) = bu.findSonWithStack(position, Seq((f: FieldEntry) => f.field != BaseUnitModel.References))
    ObjectInTree(obj, stack)
  }

  def fromSubTree(element: DomainElement, position: Position, previousStack: Seq[AmfObject]): ObjectInTree = {
    val (obj, stack) = element.findSonWithStack(position, Seq.empty)
    ObjectInTree(obj, stack ++ previousStack)
  }
}
