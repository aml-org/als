package org.mulesoft.als.suggestions.aml.webapi

import amf.aml.client.scala.model.document.Dialect
import org.mulesoft.als.suggestions.AMLBaseCompletionPlugins
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.raml._
import org.mulesoft.als.suggestions.plugins.aml.webapi.raml.raml10._
import org.mulesoft.als.suggestions.plugins.aml.webapi.raml.raml10.structure.ResolveShapeAndSecurity
import org.mulesoft.als.suggestions.plugins.aml.webapi.{
  ObjectExamplePropertiesCompletionPlugin,
  RamlParametersCompletionPlugin,
  SecuredByCompletionPlugin,
  WebApiKnownValueCompletionPlugin
}
import org.mulesoft.als.suggestions.plugins.aml.{ResolveDefault, StructureCompletionPlugin}
import org.mulesoft.amfintegration.dialect.dialects.raml.raml10.Raml10TypesDialect

object RamlCompletionPluginRegistry extends WebApiCompletionPluginRegistry {

  private val all: Seq[AMLCompletionPlugin] =
    AMLBaseCompletionPlugins.all :+
      StructureCompletionPlugin(List(
        ResolveShapeAndSecurity,
        ResolveUriParameter,
        ResolveDefault
      )) :+
      Raml10BooleanPropertyValue :+
      Raml10ParamsCompletionPlugin :+
      Raml10TypeFacetsCompletionPlugin :+
      RamlTypeDeclarationReferenceCompletionPlugin :+
      RamlParametersCompletionPlugin :+
      RamlCustomFacetsCompletionPlugin :+
      AnnotationReferenceCompletionPlugin :+
      RamlResourceTypeReference :+
      RamlParametrizedDeclarationVariablesRef :+
      RamlAbstractDefinition :+
      RamlTraitReference :+
      BaseUriParameterCompletionPlugin :+
      Raml10BaseUriParameterFacets :+
      RamlPayloadMediaTypeCompletionPlugin :+
      RamlNumberShapeFormatValues :+
      Raml10HeaderCompletionPlugin :+
      SecurityScopesCompletionPlugin :+
      SecuredByCompletionPlugin :+
      SecuritySettingsFacetsCompletionPlugin :+
      ObjectExamplePropertiesCompletionPlugin :+
      ExampleStructure :+
      WebApiExtensionsPropertyCompletionPlugin :+
      RamlDeclarationsReferencesCompletionPlugin :+
      AnnotationFacets :+
      NodeShapeDiscriminatorProperty :+
      Raml10SecuritySchemeStructureCompletionPlugin :+
      UnitDocumentationFacet :+
      DefaultVariablesAbstractDefinition :+
      WebApiKnownValueCompletionPlugin :+
      RamlEnumCompletionPlugin :+
      RamlSemanticExtensionsCompletionPlugin

  override def plugins: Seq[AMLCompletionPlugin] = all

  override def dialect: Dialect = Raml10TypesDialect()
}
