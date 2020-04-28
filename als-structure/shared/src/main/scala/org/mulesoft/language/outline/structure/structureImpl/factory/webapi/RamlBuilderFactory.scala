package org.mulesoft.language.outline.structure.structureImpl.factory.webapi

import amf.dialects.{RAML08Dialect, RAML10Dialect}
import org.mulesoft.language.outline.structure.structureImpl.{CompanionList, ElementSymbolBuilderCompanion}
import org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.ramlbuilders.{
  RamlBaseUnitSymbolBuilder,
  RamlSecuritySchemesSettingsSymbolBuilder,
  RamlWebApiSymbolBuilder
}

object RamlBuilderFactory extends AmfBuilderFactory {

  override protected def companion: CompanionList =
    super.companion + RamlSecuritySchemesSettingsSymbolBuilder + RamlWebApiSymbolBuilder

  override def baseUnitBuilder: ElementSymbolBuilderCompanion =
    RamlBaseUnitSymbolBuilder

  override val supportedDialects: Set[String] = Set(RAML10Dialect.dialect.id, RAML08Dialect.dialect.id)
}
