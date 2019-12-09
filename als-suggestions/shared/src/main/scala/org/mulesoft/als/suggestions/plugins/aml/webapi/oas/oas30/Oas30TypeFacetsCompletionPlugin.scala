package org.mulesoft.als.suggestions.plugins.aml.webapi.oas.oas30

import amf.plugins.document.vocabularies.model.document.Dialect
import org.mulesoft.als.suggestions.plugins.aml.webapi.oas.OasTypeFacetsCompletionPlugin
import org.mulesoft.amfmanager.dialect.webapi.oas.{JsonSchemaForOasWrapper, Oas30DialectWrapper}

object Oas30TypeFacetsCompletionPlugin extends OasTypeFacetsCompletionPlugin {
  override def jsonSchameObj: JsonSchemaForOasWrapper = Oas30DialectWrapper.JsonSchemas

  override def dialect: Dialect = Oas30DialectWrapper.dialect
}
