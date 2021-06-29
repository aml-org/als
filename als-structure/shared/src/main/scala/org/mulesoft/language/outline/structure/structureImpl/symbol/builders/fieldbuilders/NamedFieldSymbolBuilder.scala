package org.mulesoft.language.outline.structure.structureImpl.symbol.builders.fieldbuilders

import amf.core.client.scala.model.domain.AmfElement
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.FieldTypeSymbolBuilder

trait NamedFieldSymbolBuilder[ElementType <: AmfElement] extends FieldTypeSymbolBuilder[ElementType] {
  protected def name: String
  override protected def optionName: Option[String] = if (name.isEmpty) None else Some(name)
}
