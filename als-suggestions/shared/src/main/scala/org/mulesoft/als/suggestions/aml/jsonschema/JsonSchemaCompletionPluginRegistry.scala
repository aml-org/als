package org.mulesoft.als.suggestions.aml.jsonschema
import org.mulesoft.als.suggestions.AMLBaseCompletionPlugins
import org.mulesoft.als.suggestions.aml.webapi.WebApiCompletionPluginRegistry
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.validationprofiles.ValidationProfileTermsSuggestions
import org.mulesoft.als.suggestions.plugins.aml.webapi.jsonSchema.{JsonSchemaRefTag, JsonSchemaRequiredObjectCompletionPlugin, ResolveJsonSchemaRequiredProperties}
import org.mulesoft.als.suggestions.plugins.aml.{AMLUnionNodeCompletionPlugin, AMLUnionRangeCompletionPlugin, ResolveDefault, StructureCompletionPlugin}

trait JsonSchemaCompletionPluginRegistry extends WebApiCompletionPluginRegistry {
  protected val all: Seq[AMLCompletionPlugin] =
    AMLBaseCompletionPlugins.all :+
      StructureCompletionPlugin(
        List(
          AMLUnionNodeCompletionPlugin,
          AMLUnionRangeCompletionPlugin,
          ValidationProfileTermsSuggestions,
          ResolveJsonSchemaRequiredProperties,
          ResolveDefault
        )
      ) :+
      JsonSchemaRefTag :+
      JsonSchemaRequiredObjectCompletionPlugin
  def plugins: Seq[AMLCompletionPlugin] = all
}
