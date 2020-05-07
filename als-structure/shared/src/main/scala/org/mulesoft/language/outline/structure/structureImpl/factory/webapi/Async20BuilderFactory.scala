package org.mulesoft.language.outline.structure.structureImpl.factory.webapi
import org.mulesoft.language.outline.structure.structureImpl.companion.FieldCompanionList
import org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.asyncbuilders.{
  Async20DeclaresFieldSymbolBuilderCompanion,
  AsyncPayloadFieldSymbolCompanion
}

object Async20BuilderFactory extends AmfBuilderFactory {

  override protected def companion: FieldCompanionList =
    super.companion + Async20DeclaresFieldSymbolBuilderCompanion + AsyncPayloadFieldSymbolCompanion
}
