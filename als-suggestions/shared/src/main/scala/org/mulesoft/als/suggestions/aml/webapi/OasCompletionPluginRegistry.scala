package org.mulesoft.als.suggestions.aml.webapi

import amf.dialects.OAS20Dialect
import org.mulesoft.als.suggestions.plugins.aml.AMLStructureCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.SecuredByCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.oas._
import org.mulesoft.als.suggestions.{AMLBaseCompletionPlugins, CompletionsPluginHandler}

object OasCompletionPluginRegistry {

  private val all = AMLBaseCompletionPlugins.all :+
    OASRequiredObjectCompletionPlugin :+
    OasInBodyParametersCompletionPlugin :+
    OasTypeDeclarationReferenceCompletionPlugin :+
    SecuredByCompletionPlugin :+
    ExampleMediaType :+
    OasStructurePlugin :+
    ParameterStructure :+
    OasTypeFacetsCompletionPlugin

  def init(): Unit =
    CompletionsPluginHandler.registerPlugins(all, OAS20Dialect().id)
}
