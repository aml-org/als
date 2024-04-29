package org.mulesoft.als.suggestions.aml.webapi

import amf.aml.client.scala.model.document.Dialect
import org.mulesoft.als.suggestions.CompletionsPluginHandler
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin

trait WebApiCompletionPluginRegistry {
  def plugins: Seq[AMLCompletionPlugin]
  def dialect: Dialect

  def init(completionsPluginHandler: CompletionsPluginHandler): Unit = {
    completionsPluginHandler.registerPlugins(plugins, dialect.id)
  }
}
