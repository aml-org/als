package org.mulesoft.language.outline.structure.structureImpl.factory.webapi

import org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.WebApiSymbolBuilder
import org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.oasbuilders.Oas30BaseUnitSymbolBuilder
import org.mulesoft.language.outline.structure.structureImpl.{CompanionList, ElementSymbolBuilderCompanion}

object Oas30BuilderFactory extends AmfBuilderFactory {

  override def baseUnitBuilder: ElementSymbolBuilderCompanion =
    Oas30BaseUnitSymbolBuilder

  override def companion: CompanionList = super.companion + WebApiSymbolBuilder
}
