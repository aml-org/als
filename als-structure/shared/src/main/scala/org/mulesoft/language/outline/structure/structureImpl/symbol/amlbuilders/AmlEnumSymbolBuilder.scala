package org.mulesoft.language.outline.structure.structureImpl.symbol.amlbuilders

import amf.core.model.domain.{AmfArray, AmfObject}
import amf.core.parser.FieldEntry
import amf.plugins.document.vocabularies.metamodel.domain.PropertyMappingModel
import amf.plugins.document.vocabularies.model.domain.{ClassTerm, DatatypePropertyTerm, ObjectPropertyTerm}
import org.mulesoft.language.outline.structure.structureImpl.StructureContext
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.{
  FieldTypeSymbolBuilder,
  IriFieldSymbolBuilderCompanion,
  IriSymbolBuilderCompanion
}
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.fieldbuilders.{
  ArrayFieldTypeSymbolBuilderCompanion,
  DefaultArrayTypeSymbolBuilder
}
import org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders.{
  DeclaresFieldSymbolBuilder,
  DefaultArrayFieldTypeSymbolBuilder
}

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
