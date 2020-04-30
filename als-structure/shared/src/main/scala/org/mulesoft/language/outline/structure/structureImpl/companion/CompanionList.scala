package org.mulesoft.language.outline.structure.structureImpl.companion

import org.mulesoft.language.outline.structure.structureImpl._

abstract class CompanionList[ElementType, ListType <: SymbolBuilderCompanion[_ <: ElementType]](list: List[ListType]) {

  protected def newInstance(list: List[ListType]): CompanionList[ElementType, ListType]

  def +(builder: ListType): CompanionList[ElementType, ListType] = newInstance(builder +: list)
  def ++(builders: List[ListType]): CompanionList[ElementType, ListType] =
    newInstance(builders ++ list.filter(!builders.contains(_)))

  def -(builder: ListType): CompanionList[ElementType, ListType] = newInstance(list.filter(_ != builder))
  def replaceFor(target: ListType, newValue: ListType): CompanionList[ElementType, ListType] =
    newInstance(newValue +: list.filter(_ != target))

  def find(element: ElementType): Option[SymbolBuilder[_ <: ElementType]]

}
