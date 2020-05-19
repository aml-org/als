package org.mulesoft.language.outline.structure.structureImpl.factory.amlfactory

import org.mulesoft.language.outline.structure.structureImpl.BuilderFactory
import org.mulesoft.language.outline.structure.structureImpl.companion.FieldCompanionList
import org.mulesoft.language.outline.structure.structureImpl.symbol.amlbuilders.fields.DefaultAmlScalarTypeSymbolBuilderCompanion

object AmlBuilderFactory extends BuilderFactory {

  override protected def companion: FieldCompanionList = super.companion
}
