package org.mulesoft.als.suggestions.aml.jsonschema
import org.mulesoft.als.suggestions.AMLBaseCompletionPlugins
import org.mulesoft.als.suggestions.aml.webapi.WebApiCompletionPluginRegistry
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.jsonSchema.JsonSchemaRequiredObjectCompletionPlugin

trait JsonSchemaCompletionPluginRegistry extends WebApiCompletionPluginRegistry {
  protected val all: Seq[AMLCompletionPlugin] =
    AMLBaseCompletionPlugins.all :+
      JsonSchemaRequiredObjectCompletionPlugin
  def plugins: Seq[AMLCompletionPlugin] = all
}
