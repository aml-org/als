package org.mulesoft.language.outline.structure.structureImpl.factory.webapi

import amf.dialects.OAS30Dialect
import org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.WebApiSymbolBuilder
import org.mulesoft.language.outline.structure.structureImpl.{CompanionList, ElementSymbolBuilderCompanion}
import org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.oasbuilders.Oas30BaseUnitSymbolBuilder

object Oas30BuilderFactory extends AmfBuilderFactory {

  override def baseUnitBuilder: ElementSymbolBuilderCompanion =
    Oas30BaseUnitSymbolBuilder

  override def companion: CompanionList = super.companion + WebApiSymbolBuilder

  override val supportedDialects: Set[String] = Set(OAS30Dialect.dialect.id)
}
