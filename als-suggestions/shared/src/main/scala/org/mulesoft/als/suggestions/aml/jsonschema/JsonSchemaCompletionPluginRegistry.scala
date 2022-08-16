package org.mulesoft.als.suggestions.aml.jsonschema
import org.mulesoft.als.suggestions.AMLBaseCompletionPlugins
import org.mulesoft.als.suggestions.aml.webapi.WebApiCompletionPluginRegistry
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin

trait JsonSchemaCompletionPluginRegistry extends WebApiCompletionPluginRegistry {
  protected val all: Seq[AMLCompletionPlugin] =
    AMLBaseCompletionPlugins.all
  def plugins: Seq[AMLCompletionPlugin] = all
}
