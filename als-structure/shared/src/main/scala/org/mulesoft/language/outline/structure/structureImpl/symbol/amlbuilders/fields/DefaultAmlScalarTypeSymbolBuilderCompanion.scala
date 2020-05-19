package org.mulesoft.language.outline.structure.structureImpl.symbol.amlbuilders.fields

import amf.plugins.document.vocabularies.metamodel.document.DialectModel
import org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders.DefaultMappedScalarTypeSymbolBuilderCompanion

object DefaultAmlScalarTypeSymbolBuilderCompanion extends DefaultMappedScalarTypeSymbolBuilderCompanion {
  override protected val mapName = Map(
    DialectModel.Version -> "version"
  )
}
