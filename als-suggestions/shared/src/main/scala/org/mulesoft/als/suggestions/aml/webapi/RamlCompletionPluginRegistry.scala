package org.mulesoft.als.suggestions.aml.webapi

import amf.dialects.RAML10Dialect
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.{
  AMLDeclarationsReferencesCompletionPlugin,
  AMLStructureCompletionPlugin
}
import org.mulesoft.als.suggestions.plugins.aml.webapi.raml._
import org.mulesoft.als.suggestions.{AMLBaseCompletionPlugins, CompletionsPluginHandler}

object RamlCompletionPluginRegistry {

  private val all: Seq[AMLCompletionPlugin] =
    AMLBaseCompletionPlugins.all.filterNot(p =>
      p == AMLStructureCompletionPlugin || p == AMLDeclarationsReferencesCompletionPlugin) :+
      RamlStructureCompletionPlugin :+
      RamlParamsCompletionPlugin :+
      RamlTypeFacetsCompletionPlugin :+
      RamlTypeDeclarationReferenceCompletionPlugin :+
      RamlCustomFacetsCompletionPlugin :+
      AnnotationReferenceCompletionPlugin :+
      RamlResourceTypeReference :+
      RamlParametrizedDeclarationVariablesRef :+
      RamlAbstractDefinition :+
      RamlTraitReference :+
      BaseUriParameterCompletionPlugin :+
      RamlBaseUriParameterFacets :+
      RamlPayloadMediaTypeCompletionPlugin :+
      RamlNumberShapeFormatValues :+
      Raml10HeaderCompletionPlugin :+
      SecurityScopesCompletionPlugin :+
      SecuredByCompletionPlugin :+
      SecuritySchemeStructureCompletionPlugin :+
      SecuritySettingsFacetsCompletionPlugin :+
      ObjectExamplePropertiesCompletionPlugin :+
      ExampleStructure :+
      WebApiExtensionsPropertyCompletionPlugin :+
      RAMLDeclarationsReferencesCompletionPlugin

  def init(): Unit =
    CompletionsPluginHandler.registerPlugins(all, RAML10Dialect().id)
}
