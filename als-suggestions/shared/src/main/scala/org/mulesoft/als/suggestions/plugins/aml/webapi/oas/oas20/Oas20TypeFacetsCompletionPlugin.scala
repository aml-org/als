package org.mulesoft.als.suggestions.plugins.aml.webapi.oas.oas20

import amf.plugins.document.vocabularies.model.document.Dialect
import org.mulesoft.als.suggestions.plugins.aml.webapi.oas.OasTypeFacetsCompletionPlugin
import org.mulesoft.amfmanager.dialect.webapi.oas.{JsonSchemaForOasWrapper, Oas20DialectWrapper}

object Oas20TypeFacetsCompletionPlugin extends OasTypeFacetsCompletionPlugin {
  override def jsonSchameObj: JsonSchemaForOasWrapper = Oas20DialectWrapper.JsonSchemas

  override def dialect: Dialect = Oas20DialectWrapper.dialect
}
