package org.mulesoft.language.outline.structure.structureImpl.factory.webapi

import org.mulesoft.language.outline.structure.structureImpl.companion.FieldCompanionList
import org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.fields.PayloadFieldSymbolCompanion
import org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.oasbuilders.{
  Oas20DeclaresFieldSymbolBuilderCompanion,
  OasBaseUrlFieldSymbolBuilderCompanion
}

object Oas20BuilderFactory extends AmfBuilderFactory {

  override protected def companion: FieldCompanionList =
    super.companion + OasBaseUrlFieldSymbolBuilderCompanion +
      PayloadFieldSymbolCompanion +
      Oas20DeclaresFieldSymbolBuilderCompanion
}
