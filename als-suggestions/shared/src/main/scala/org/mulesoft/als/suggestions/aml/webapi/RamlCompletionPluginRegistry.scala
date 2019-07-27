package org.mulesoft.als.suggestions.aml.webapi

import amf.dialects.RAML10Dialect
import org.mulesoft.als.suggestions.plugins.aml.webapi.raml.{
  RamlParamsCompletionPlugin,
  RamlTypeFacetsCompletionPlugin
}
import org.mulesoft.als.suggestions.{AMLBaseCompletionPlugins, CompletionsPluginHandler}

object RamlCompletionPluginRegistry {

  private val all = AMLBaseCompletionPlugins.all :+ RamlParamsCompletionPlugin :+ RamlTypeFacetsCompletionPlugin

  def init(): Unit = CompletionsPluginHandler.registerPlugins(all, RAML10Dialect().id)
}
