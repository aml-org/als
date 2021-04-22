package org.mulesoft.als.suggestions.aml

import amf.plugins.document.vocabularies.model.document.Dialect
import org.mulesoft.als.suggestions.aml.webapi.WebApiCompletionPluginRegistry
import org.mulesoft.als.suggestions.{AMLBaseCompletionPlugins, CompletionsPluginHandler}
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.metadialect.{
  AnyUriValueCompletionPlugin,
  MapLabelInPropertyMappingCompletionPlugin,
  NamespaceForTermCompletionPlugin,
  TypeDiscriminatorCompletionPlugin
}
import org.mulesoft.amfintegration.dialect.dialects.metadialect.MetaDialect

object MetaDialectPluginRegistry extends WebApiCompletionPluginRegistry {

  private val all: Seq[AMLCompletionPlugin] =
    AMLBaseCompletionPlugins.all :+
      NamespaceForTermCompletionPlugin :+
      AnyUriValueCompletionPlugin :+
      MapLabelInPropertyMappingCompletionPlugin :+
      TypeDiscriminatorCompletionPlugin

  override def plugins: Seq[AMLCompletionPlugin] = all

  override def dialect: Dialect = MetaDialect()
}
