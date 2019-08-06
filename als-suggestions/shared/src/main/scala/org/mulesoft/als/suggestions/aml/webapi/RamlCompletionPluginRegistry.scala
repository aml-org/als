package org.mulesoft.als.suggestions.aml.webapi

import amf.dialects.RAML10Dialect
import org.mulesoft.als.suggestions.plugins.aml.webapi.raml.{
  RamlCustomFacetsCompletionPlugin,
  RamlParamsCompletionPlugin,
  RamlTypeDeclarationReferenceCompletionPlugin,
  RamlTypeFacetsCompletionPlugin
}
import org.mulesoft.als.suggestions.{AMLBaseCompletionPlugins, CompletionsPluginHandler}

object RamlCompletionPluginRegistry {

  private val all =
    AMLBaseCompletionPlugins.all :+
      RamlParamsCompletionPlugin :+
      RamlTypeFacetsCompletionPlugin :+
      RamlTypeDeclarationReferenceCompletionPlugin :+
      RamlCustomFacetsCompletionPlugin

  def init(): Unit =
    CompletionsPluginHandler.registerPlugins(all, RAML10Dialect().id)
}
