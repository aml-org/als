package org.mulesoft.language.outline.structure.structureImpl.factory.amlfactory

import org.mulesoft.language.outline.structure.structureImpl.factory.webapi.AmfBuilderFactory
import org.mulesoft.language.outline.structure.structureImpl.symbol.amlbuilders.{
  AmlDocumentModelSymbolBuilderCompanion,
  AmlMetaDialectSymbolBuilder
}
import org.mulesoft.language.outline.structure.structureImpl.{CompanionList, ElementSymbolBuilderCompanion}

object AmlMetaDialectBuilderFactory extends AmfBuilderFactory {
  override def baseUnitBuilder: ElementSymbolBuilderCompanion = AmlMetaDialectSymbolBuilder

  override protected def companion: CompanionList =
    super.companion + AmlDocumentModelSymbolBuilderCompanion
}
