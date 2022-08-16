package org.mulesoft.als.suggestions.aml.jsonschema

import amf.aml.client.scala.model.document.Dialect
import org.mulesoft.als.suggestions.AMLBaseCompletionPlugins
import org.mulesoft.als.suggestions.aml.webapi.WebApiCompletionPluginRegistry
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.{ResolveDefault, StructureCompletionPlugin}
import org.mulesoft.als.suggestions.plugins.aml.webapi.oas.structure.{
  IriTemplateMappingIgnore,
  ResolveDeclaredResponse,
  ResolveInfo,
  ResolveParameterShapes,
  ResolveTag,
  SchemaExampleException
}
import org.mulesoft.amfintegration.dialect.dialects.jsonschema.draft4.JsonSchemaDraft4Dialect

object JsonSchema4CompletionPluginRegistry extends JsonSchemaCompletionPluginRegistry {
  override def dialect: Dialect = JsonSchemaDraft4Dialect()
}
