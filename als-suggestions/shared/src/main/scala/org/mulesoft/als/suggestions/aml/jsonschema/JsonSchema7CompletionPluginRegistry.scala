package org.mulesoft.als.suggestions.aml.jsonschema

import amf.aml.client.scala.model.document.Dialect
import org.mulesoft.als.suggestions.AMLBaseCompletionPlugins
import org.mulesoft.als.suggestions.aml.webapi.WebApiCompletionPluginRegistry
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.amfintegration.dialect.dialects.jsonschema.draft7.JsonSchemaDraft7Dialect

object JsonSchema7CompletionPluginRegistry extends JsonSchemaCompletionPluginRegistry {

  override def dialect: Dialect = JsonSchemaDraft7Dialect()
}
