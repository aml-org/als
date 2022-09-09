package org.mulesoft.als.suggestions.aml.jsonschema

import amf.aml.client.scala.model.document.Dialect
import org.mulesoft.amfintegration.dialect.dialects.jsonschema.draft4.JsonSchemaDraft4Dialect

object JsonSchema4CompletionPluginRegistry extends JsonSchemaCompletionPluginRegistry {
  override def dialect: Dialect = JsonSchemaDraft4Dialect()
}
