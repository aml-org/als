package org.mulesoft.language.outline.structure.structureImpl.symbol.builders.fieldbuilders

import amf.core.client.scala.model.domain.{AmfElement, AmfObject}
import org.mulesoft.language.outline.structure.structureImpl.DocumentSymbol
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.{
  FieldTypeSymbolBuilder,
  FieldTypeSymbolBuilderCompanion
}

trait ObjectFieldTypeSymbolBuilder extends FieldTypeSymbolBuilder[AmfObject] {}

trait ObjectFieldTypeSymbolBuilderCompanion extends FieldTypeSymbolBuilderCompanion[AmfObject] {
  override def getElementType: Class[_ <: AmfElement] = classOf[AmfObject]
}

trait DefaultObjectTypeSymbolBuilder extends ObjectFieldTypeSymbolBuilderCompanion

trait SingleObjectFieldSymbolBuilder extends ObjectFieldTypeSymbolBuilder with NamedFieldSymbolBuilder[AmfObject] {

  override protected val children: List[DocumentSymbol] = Nil
}
