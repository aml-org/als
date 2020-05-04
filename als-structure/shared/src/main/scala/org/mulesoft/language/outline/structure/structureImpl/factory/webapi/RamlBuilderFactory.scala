package org.mulesoft.language.outline.structure.structureImpl.factory.webapi

import amf.plugins.document.vocabularies.model.document.Dialect
import org.mulesoft.amfmanager.dialect.webapi.raml.raml08.Raml08TypesDialect
import org.mulesoft.amfmanager.dialect.webapi.raml.raml10.Raml10TypesDialect
import org.mulesoft.language.outline.structure.structureImpl.companion.FieldCompanionList
import org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.ramlbuilders.{
  RamlBaseUriFieldSymbolBuilderCompanion,
  RamlSecuritySchemesSettingsSymbolBuilder
}

trait RamlBuilderFactory extends AmfBuilderFactory {
  override protected def companion: FieldCompanionList =
    super.companion + RamlSecuritySchemesSettingsSymbolBuilder + RamlBaseUriFieldSymbolBuilderCompanion
}

object Raml10BuilderFactory extends RamlBuilderFactory {
  override def dialect: Dialect = Raml10TypesDialect()
}

object Raml08BuilderFactory extends RamlBuilderFactory {
  override def dialect: Dialect = Raml08TypesDialect()
}
