package org.mulesoft.language.outline.structure.structureImpl.factory.amlfactory

import org.mulesoft.language.outline.structure.structureImpl.companion.FieldCompanionList
import org.mulesoft.language.outline.structure.structureImpl.factory.webapi.AmfBuilderFactory
import org.mulesoft.language.outline.structure.structureImpl.symbol.amlbuilders.{
  AmlDocumentModelSymbolBuilderCompanion,
  AmlEnumSymbolBuilderCompanion,
  AmlMetaDialectSymbolBuilder,
  AmlNodeMappingsFieldSymbolBuilder
}
import org.mulesoft.language.outline.structure.structureImpl.symbol.amlbuilders.fields.DefaultAmlScalarTypeSymbolBuilderCompanion

object AmlMetaDialectBuilderFactory extends AmfBuilderFactory {
  override protected def companion: FieldCompanionList =
    super.companion + AmlMetaDialectSymbolBuilder + AmlDocumentModelSymbolBuilderCompanion + DefaultAmlScalarTypeSymbolBuilderCompanion + AmlNodeMappingsFieldSymbolBuilder + AmlEnumSymbolBuilderCompanion
}
