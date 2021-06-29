package org.mulesoft.language.outline.structure.structureImpl.symbol.builders.fieldbuilders

import amf.core.client.scala.model.domain.{AmfArray, AmfElement}
import org.mulesoft.language.outline.structure.structureImpl.DocumentSymbol
import org.mulesoft.language.outline.structure.structureImpl.symbol.ChildrenCollector
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.{
  FieldTypeSymbolBuilder,
  FieldTypeSymbolBuilderCompanion
}

trait ArrayFieldTypeSymbolBuilder extends FieldTypeSymbolBuilder[AmfArray] with ChildrenCollector {
  protected val children: List[DocumentSymbol] = collectChildren(value)
}
trait ArrayFieldTypeSymbolBuilderCompanion extends FieldTypeSymbolBuilderCompanion[AmfArray] {
  override def getElementType: Class[_ <: AmfElement] = classOf[AmfArray]
}

trait DefaultArrayTypeSymbolBuilder extends ArrayFieldTypeSymbolBuilderCompanion

trait NamedArrayFieldTypeSymbolBuilder extends ArrayFieldTypeSymbolBuilder with NamedFieldSymbolBuilder[AmfArray] {}
