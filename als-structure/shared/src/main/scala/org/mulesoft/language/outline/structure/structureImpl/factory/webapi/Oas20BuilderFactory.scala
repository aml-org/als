package org.mulesoft.language.outline.structure.structureImpl.factory.webapi

import amf.dialects.OAS20Dialect
import org.mulesoft.language.outline.structure.structureImpl.{CompanionList, ElementSymbolBuilderCompanion}
import org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.oasbuilders.{
  Oas20BaseUnitSymbolBuilder,
  Oas20WebApiSymbolBuilder
}

object Oas20BuilderFactory extends AmfBuilderFactory {

  override protected def companion: CompanionList =
    super.companion + Oas20WebApiSymbolBuilder
  override def baseUnitBuilder: ElementSymbolBuilderCompanion =
    Oas20BaseUnitSymbolBuilder

  override val supportedDialects: Set[String] = Set(OAS20Dialect.dialect.id)
}
