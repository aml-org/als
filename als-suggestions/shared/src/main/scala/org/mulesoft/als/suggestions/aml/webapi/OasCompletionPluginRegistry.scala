package org.mulesoft.als.suggestions.aml.webapi

import amf.aml.client.scala.model.document.Dialect
import org.mulesoft.als.suggestions.AMLBaseCompletionPlugins
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.extensions.OasLikeSemanticExtensionsFlavour
import org.mulesoft.als.suggestions.plugins.aml.webapi.oas._
import org.mulesoft.als.suggestions.plugins.aml.webapi.oas.oas20.structure.{ResolveParameterEndpoint, ResolveRequest}
import org.mulesoft.als.suggestions.plugins.aml.webapi.oas.oas20.{
  Oas20ParameterStructure,
  Oas20TypeFacetsCompletionPlugin
}
import org.mulesoft.als.suggestions.plugins.aml.webapi.oas.oas30._
import org.mulesoft.als.suggestions.plugins.aml.webapi.oas.structure._
import org.mulesoft.als.suggestions.plugins.aml.webapi.{
  ObjectExamplePropertiesCompletionPlugin,
  SecuredByCompletionPlugin,
  WebApiKnownValueCompletionPlugin
}
import org.mulesoft.als.suggestions.plugins.aml.{ResolveDefault, StructureCompletionPlugin}
import org.mulesoft.amfintegration.dialect.dialects.oas.{OAS20Dialect, OAS30Dialect}

trait OasBaseCompletionRegistry extends WebApiCompletionPluginRegistry {
  val common: Seq[AMLCompletionPlugin] = AMLBaseCompletionPlugins.all :+
    OASRequiredObjectCompletionPlugin :+
    SecuredByCompletionPlugin :+
    ExampleMediaType :+
    StructureCompletionPlugin(
      List(
        ResolveParameterShapes,
        ResolveDeclaredResponse,
        ResolveTag,
        ResolveInfo,
        IriTemplateMappingIgnore,
        SchemaExampleException,
        ResolveDefault
      )) :+
    ParameterReferenceCompletionPlugin :+
    OASRefTag :+
    OperationTags :+
    ObjectExamplePropertiesCompletionPlugin :+
    OasNumberShapeFormatValues :+
    QueryParamNamesFromPath :+
    WebApiKnownValueCompletionPlugin :+
    ResponsesDeclarationCodes
}

object Oas20CompletionPluginRegistry extends OasBaseCompletionRegistry {

  private val all = common :+
    Oas20ParameterStructure :+
    StructureCompletionPlugin(
      List(
        ResolveParameterShapes,
        ResolveParameterEndpoint,
        ResolveRequest,
        ResolveDeclaredResponse,
        ResolveTag,
        ResolveInfo,
        ResponseExampleException,
        ResolveDefault
      )) :+
    OaslikeSecurityScopesCompletionPlugin :+
    Oas20TypeFacetsCompletionPlugin :+
    OasLikeSemanticExtensionsFlavour

  override def plugins: Seq[AMLCompletionPlugin] = all

  override def dialect: Dialect = OAS20Dialect()
}

object Oas30CompletionPluginRegistry extends OasBaseCompletionRegistry {

  private val all = common :+
    StructureCompletionPlugin(List(
      ResolveParameterShapes,
      ResolveDeclaredResponse,
      ResolveTag,
      ResolveInfo,
      IriTemplateMappingIgnore,
      SchemaExampleException,
      ResolveParameterInRequest,
      ResolveDefault
    )) :+
    Oas30ParameterStructure :+
    EncodingPropertyName :+
    Oas30TypeFacetsCompletionPlugin :+
    OasUrlTemplateParam :+
    OAS30RefTag :+
    RefToParameters :+
    DiscriminatorFacet :+
    Oas3VariableValueParam :+
    DiscriminatorObject :+
    DiscriminatorMappingValue :+
    FlowNames :+
    OAS30EnumCompletionPlugin :+
    OaslikeSecurityScopesCompletionPlugin :+
    OasRuntimeExpressionsCompletionPlugin

  override def plugins: Seq[AMLCompletionPlugin] = all

  override def dialect: Dialect = OAS30Dialect()
}
