package org.mulesoft.language.outline.structure.structureImpl.symbol.builders.fieldbuilders

import amf.core.model.domain.AmfElement
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.FieldTypeSymbolBuilder

trait NamedFieldSymbolBuilder[ElementType <: AmfElement] extends FieldTypeSymbolBuilder[ElementType] {
  protected def name: String
  override protected def optionName: Option[String] = Some(name)
}
