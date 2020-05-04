package org.mulesoft.language.outline.structure.structureImpl.factory.webapi

import amf.plugins.document.vocabularies.model.document.Dialect
import org.mulesoft.amfmanager.dialect.webapi.oas.Oas20DialectWrapper
import org.mulesoft.language.outline.structure.structureImpl.companion.FieldCompanionList
import org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.oasbuilders.OasBaseUrlFieldSymbolBuilderCompanion

object Oas20BuilderFactory extends AmfBuilderFactory {

  override protected def companion: FieldCompanionList = super.companion + OasBaseUrlFieldSymbolBuilderCompanion

  override val dialect: Dialect = Oas20DialectWrapper.dialect
}
