package org.mulesoft.als.suggestions.aml

import amf.plugins.document.vocabularies.model.document.Dialect
import org.mulesoft.als.suggestions.AMLBaseCompletionPlugins
import org.mulesoft.als.suggestions.aml.webapi.WebApiCompletionPluginRegistry
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.metadialect.{
  AnyUriValueCompletionPlugin,
  MetaDialectDocumentsCompletionPlugin,
  MapLabelInPropertyMappingCompletionPlugin,
  NamespaceForTermCompletionPlugin
}
import org.mulesoft.als.suggestions.plugins.aml.{ResolveDefault, StructureCompletionPlugin}
import org.mulesoft.amfintegration.dialect.dialects.metadialect.MetaDialect

object MetaDialectPluginRegistry extends WebApiCompletionPluginRegistry {

  private val all: Seq[AMLCompletionPlugin] =
    AMLBaseCompletionPlugins.all :+
      StructureCompletionPlugin(
        List(
          MetaDialectDocumentsCompletionPlugin,
          ResolveDefault
        )) :+
      NamespaceForTermCompletionPlugin :+
      AnyUriValueCompletionPlugin :+
      MapLabelInPropertyMappingCompletionPlugin

  override def plugins: Seq[AMLCompletionPlugin] = all

  override def dialect: Dialect = MetaDialect()
}
