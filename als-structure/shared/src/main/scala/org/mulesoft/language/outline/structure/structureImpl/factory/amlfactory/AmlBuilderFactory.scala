package org.mulesoft.language.outline.structure.structureImpl.factory.amlfactory

import org.mulesoft.language.outline.structure.structureImpl.symbol.amlbuilders.AmlUnitSymbolBuilder
import org.mulesoft.language.outline.structure.structureImpl.{BuilderFactory, ElementSymbolBuilderCompanion}

object AmlBuilderFactory extends BuilderFactory {
  override def baseUnitBuilder: ElementSymbolBuilderCompanion = AmlUnitSymbolBuilder
}
