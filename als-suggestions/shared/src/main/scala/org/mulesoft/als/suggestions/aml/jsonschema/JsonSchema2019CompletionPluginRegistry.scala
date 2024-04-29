package org.mulesoft.als.suggestions.aml.jsonschema

import amf.aml.client.scala.model.document.Dialect
import org.mulesoft.als.suggestions.AMLBaseCompletionPlugins
import org.mulesoft.als.suggestions.aml.webapi.WebApiCompletionPluginRegistry
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.amfintegration.dialect.dialects.jsonschema.draft2019.JsonSchemaDraft2019Dialect

object JsonSchema2019CompletionPluginRegistry extends JsonSchemaCompletionPluginRegistry {

  override def dialect: Dialect = JsonSchemaDraft2019Dialect()
}
