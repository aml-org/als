package org.mulesoft.language.outline.structure.structureImpl.factory.webapi
import org.mulesoft.language.outline.structure.structureImpl.companion.FieldCompanionList
import org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.eventedbuilders.EventedDeclaresFieldSymbolBuilderCompanion
import org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.oasbuilders.{
  OasDocumentationFieldSymbolBuilder,
  ServerArrayFieldSymbolBuilderCompanion
}

trait EventedLikeBuilderFactory extends AmfBuilderFactory {

  override protected def companion: FieldCompanionList =
    super.companion + ServerArrayFieldSymbolBuilderCompanion + EventedDeclaresFieldSymbolBuilderCompanion + OasDocumentationFieldSymbolBuilder

}
