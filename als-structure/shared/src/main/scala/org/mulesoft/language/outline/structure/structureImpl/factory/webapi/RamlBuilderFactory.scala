package org.mulesoft.language.outline.structure.structureImpl.factory.webapi

import org.mulesoft.language.outline.structure.structureImpl.companion.FieldCompanionList
import org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.ramlbuilders.{
  RamlBaseUriFieldSymbolBuilderCompanion,
  RamlSecuritySchemeSettingsFieldSymbolBuilderCompanion,
  RamlSecuritySchemesSettingsSymbolBuilder
}

object RamlBuilderFactory extends AmfBuilderFactory {
  override protected def companion: FieldCompanionList =
    super.companion + RamlSecuritySchemeSettingsFieldSymbolBuilderCompanion + RamlBaseUriFieldSymbolBuilderCompanion
}
