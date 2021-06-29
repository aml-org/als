package org.mulesoft.language.outline.structure.structureImpl.symbol.builders.fieldbuilders

import amf.core.client.scala.model.domain.{AmfElement, AmfScalar}
import org.mulesoft.language.outline.structure.structureImpl.DocumentSymbol
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.{
  FieldTypeSymbolBuilder,
  FieldTypeSymbolBuilderCompanion
}

// separate between optional and mandatory name for aml?
trait ScalarFieldTypeSymbolBuilder extends FieldTypeSymbolBuilder[AmfScalar] {}

trait ScalarFieldTypeSymbolBuilderCompanion extends FieldTypeSymbolBuilderCompanion[AmfScalar] {
  override def getElementType: Class[_ <: AmfElement] = classOf[AmfScalar]
}

trait DefaultScalarTypeSymbolBuilder extends ScalarFieldTypeSymbolBuilderCompanion

trait NamedScalarFieldTypeSymbolBuilder extends ScalarFieldTypeSymbolBuilder with NamedFieldSymbolBuilder[AmfScalar] {
  override protected val children: List[DocumentSymbol] = Nil
}
