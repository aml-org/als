package org.mulesoft.als.suggestions.aml.webapi

import amf.dialects.OAS20Dialect
import org.mulesoft.als.suggestions.plugins.aml.webapi.oas.OASRequiredObjectCompletionPlugin
import org.mulesoft.als.suggestions.{AMLBaseCompletionPlugins, CompletionsPluginHandler}

object OasCompletionPluginRegistry {

  private val all = AMLBaseCompletionPlugins.all :+ OASRequiredObjectCompletionPlugin

  def init(): Unit = CompletionsPluginHandler.registerPlugins(all, OAS20Dialect().id)
}
