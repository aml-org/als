package org.mulesoft.language.outline.structure.structureImpl.symbol.amlbuilders

import amf.aml.internal.metamodel.domain.PropertyMappingModel
import amf.core.client.scala.model.domain.AmfArray
import amf.core.internal.parser.domain.FieldEntry
import org.mulesoft.language.outline.structure.structureImpl.StructureContext
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.fieldbuilders.ArrayFieldTypeSymbolBuilderCompanion
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.{
  FieldTypeSymbolBuilder,
  IriFieldSymbolBuilderCompanion
}
import org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders.DefaultArrayFieldTypeSymbolBuilder

class AmlEnumSymbolBuilder(override val value: AmfArray, override val element: FieldEntry)(
    override implicit val ctx: StructureContext)
    extends DefaultArrayFieldTypeSymbolBuilder(value, element) {

  override protected def name = "enum"
}

object AmlEnumSymbolBuilderCompanion extends ArrayFieldTypeSymbolBuilderCompanion with IriFieldSymbolBuilderCompanion {

  override def construct(element: FieldEntry, value: AmfArray)(
      implicit ctx: StructureContext): Option[FieldTypeSymbolBuilder[AmfArray]] = {
    Some(new AmlEnumSymbolBuilder(value, element))
  }

  override val supportedIri: String = PropertyMappingModel.Enum.value.iri()
}
