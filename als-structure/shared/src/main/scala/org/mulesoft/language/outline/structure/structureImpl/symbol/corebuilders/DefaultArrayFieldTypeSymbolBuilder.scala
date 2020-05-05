package org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders

import amf.core.model.domain.AmfArray
import amf.core.parser.FieldEntry
import org.mulesoft.language.outline.structure.structureImpl._
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.FieldTypeSymbolBuilder
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.fieldbuilders.{
  ArrayFieldTypeSymbolBuilderCompanion,
  DefaultArrayTypeSymbolBuilder,
  NamedArrayFieldTypeSymbolBuilder
}

class DefaultArrayFieldTypeSymbolBuilder(override val value: AmfArray, override val element: FieldEntry)(
    override implicit val ctx: StructureContext)
    extends NamedArrayFieldTypeSymbolBuilder {
  override protected def name: String = element.field.value.name
}

object DefaultArrayFieldTypeSymbolBuilderCompanion extends DefaultArrayTypeSymbolBuilder {
  override def construct(element: FieldEntry, value: AmfArray)(
      implicit ctx: StructureContext): Option[FieldTypeSymbolBuilder[AmfArray]] =
    Some(new DefaultArrayFieldTypeSymbolBuilder(value, element))
}
