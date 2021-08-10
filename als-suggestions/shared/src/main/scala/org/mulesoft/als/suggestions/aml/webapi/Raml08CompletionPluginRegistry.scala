package org.mulesoft.als.suggestions.aml.webapi

import amf.aml.client.scala.model.document.Dialect
import org.mulesoft.als.suggestions.AMLBaseCompletionPlugins
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.raml._
import org.mulesoft.als.suggestions.plugins.aml.webapi.raml.raml08._
import org.mulesoft.als.suggestions.plugins.aml.webapi.raml.raml08.structure.ResolvePayload
import org.mulesoft.als.suggestions.plugins.aml.webapi.raml.raml10.structure.ResolveShapeAndSecurity
import org.mulesoft.als.suggestions.plugins.aml.webapi.{
  ObjectExamplePropertiesCompletionPlugin,
  SecuredByCompletionPlugin
}
import org.mulesoft.als.suggestions.plugins.aml.{ResolveDefault, StructureCompletionPlugin}
import org.mulesoft.amfintegration.dialect.dialects.raml.raml08.Raml08TypesDialect

object Raml08CompletionPluginRegistry extends WebApiCompletionPluginRegistry {

  private val all: Seq[AMLCompletionPlugin] =
    AMLBaseCompletionPlugins.all :+
      StructureCompletionPlugin(
        List(
          ResolveShapeAndSecurity,
          ResolveUriParameter,
          ResolvePayload,
          ResolveDefault
        )) :+
      Raml08ParamsCompletionPlugin :+
      Raml08BooleanPropertyValue :+
      Raml08TypeFacetsCompletionPlugin :+
      RamlTypeDeclarationReferenceCompletionPlugin :+
      RamlCustomFacetsCompletionPlugin :+
      RamlResourceTypeReference :+
      RamlParametrizedDeclarationVariablesRef :+
      RamlAbstractDefinition :+
      RamlTraitReference :+
      BaseUriParameterCompletionPlugin :+
      Raml08BaseUriParameterFacets :+
      RamlPayloadMediaTypeCompletionPlugin :+
      RamlNumberShapeFormatValues :+
      SecurityScopesCompletionPlugin :+
      SecuritySettingsFacetsCompletionPlugin :+
      ObjectExamplePropertiesCompletionPlugin :+
      ExampleStructure :+
      WebApiExtensionsPropertyCompletionPlugin :+
      RamlDeclarationsReferencesCompletionPlugin :+
      NodeShapeDiscriminatorProperty :+
      SecuredByCompletionPlugin :+
      Raml08SecuritySchemeStructureCompletionPlugin :+
      UnitDocumentationFacet :+
      DefaultVariablesAbstractDefinition :+
//      OperationRequest :+
      Raml08KnownValueCompletionPlugin :+
      Raml08TypeDeclarationReferenceCompletionPlugin :+
      RamlSemanticExtensionsCompletionPlugin

  override def plugins: Seq[AMLCompletionPlugin] = all

  override def dialect: Dialect = Raml08TypesDialect()
}
