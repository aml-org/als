package org.mulesoft.als.suggestions.aml

import org.mulesoft.als.suggestions.{AMLBaseCompletionPlugins, CompletionsPluginHandler}
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.metadialect.{
  AnyUriValueCompletionPlugin,
  MapLabelInPropertyMappingCompletionPlugin,
  NamespaceForTermCompletionPlugin
}
import org.mulesoft.amfintegration.dialect.dialects.metadialect.MetaDialect

object MetaDialectPluginRegistry {

  private val all: Seq[AMLCompletionPlugin] =
    AMLBaseCompletionPlugins.all :+
      NamespaceForTermCompletionPlugin :+
      AnyUriValueCompletionPlugin :+
      MapLabelInPropertyMappingCompletionPlugin

  def init(): Unit =
    CompletionsPluginHandler.registerPlugins(all, MetaDialect().id)
}
