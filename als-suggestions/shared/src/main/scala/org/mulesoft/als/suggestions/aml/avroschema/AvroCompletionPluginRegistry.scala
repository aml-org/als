package org.mulesoft.als.suggestions.aml.avroschema

import amf.aml.client.scala.model.document.Dialect
import org.mulesoft.als.suggestions.AMLBaseCompletionPlugins
import org.mulesoft.als.suggestions.aml.webapi.WebApiCompletionPluginRegistry
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.avroschema.AvroTypesCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.avroschema.logicaltypes.AvroLogicalTypesCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.avroschema.structure.{
  ResolveField,
  ResolveFixed,
  ResolveMap,
  ResolveUnion
}
import org.mulesoft.als.suggestions.plugins.aml.{ResolveDefault, StructureCompletionPlugin}
import org.mulesoft.amfintegration.dialect.dialects.avro.AvroDialect

object AvroCompletionPluginRegistry extends WebApiCompletionPluginRegistry {

  private val all: Seq[AMLCompletionPlugin] =
    AMLBaseCompletionPlugins.all :+
      StructureCompletionPlugin(
        List(
          ResolveMap,
          ResolveFixed,
          ResolveField,
          ResolveUnion,
          ResolveDefault
        )
      ) :+
      AvroTypesCompletionPlugin :+
      AvroLogicalTypesCompletionPlugin

  override def plugins: Seq[AMLCompletionPlugin] = all

  override def dialect: Dialect = AvroDialect()
}
