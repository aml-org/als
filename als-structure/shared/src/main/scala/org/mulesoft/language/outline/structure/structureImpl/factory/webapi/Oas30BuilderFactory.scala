package org.mulesoft.language.outline.structure.structureImpl.factory.webapi

import amf.plugins.document.vocabularies.model.document.Dialect
import org.mulesoft.amfmanager.dialect.webapi.oas.Oas30DialectWrapper
import org.mulesoft.language.outline.structure.structureImpl.companion.FieldCompanionList

object Oas30BuilderFactory extends AmfBuilderFactory {

  override def companion: FieldCompanionList = super.companion

  override def dialect: Dialect = Oas30DialectWrapper.dialect
}
