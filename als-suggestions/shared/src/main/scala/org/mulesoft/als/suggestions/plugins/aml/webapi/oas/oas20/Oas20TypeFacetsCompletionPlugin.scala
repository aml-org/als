package org.mulesoft.als.suggestions.plugins.aml.webapi.oas.oas20

import amf.aml.client.scala.model.document.Dialect
import org.mulesoft.als.suggestions.plugins.aml.webapi.oas.OasTypeFacetsCompletionPlugin
import org.mulesoft.amfintegration.dialect.dialects.jsonschema.JsonSchemaForOasWrapper
import org.mulesoft.amfintegration.dialect.dialects.jsonschema.oas.oas2.JsonSchemas
import org.mulesoft.amfintegration.dialect.dialects.oas.OAS20Dialect

object Oas20TypeFacetsCompletionPlugin extends OasTypeFacetsCompletionPlugin {
  override def jsonSchemaObj: JsonSchemaForOasWrapper = JsonSchemas

  override def dialect: Dialect = OAS20Dialect.dialect
}
