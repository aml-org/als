package org.mulesoft.als.suggestions.aml.webapi

import amf.plugins.document.vocabularies.model.document.Dialect
import org.mulesoft.als.suggestions.CompletionsPluginHandler
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.amfintegration.AmfInstance

trait WebApiCompletionPluginRegistry {
  def plugins: Seq[AMLCompletionPlugin]
  def dialect: Dialect

  def init(amfInstance: AmfInstance, completionsPluginHandler: CompletionsPluginHandler): Unit = {
    completionsPluginHandler.registerPlugins(plugins, dialect.id)
  }
}
